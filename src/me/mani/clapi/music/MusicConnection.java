package me.mani.clapi.music;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.mani.clapi.http.HttpConnection;
import me.mani.clapi.logger.Logger;
import me.mani.clapi.music.data.Instance;
import me.mani.clapi.music.data.Playlist;
import me.mani.clapi.music.data.Track;
import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Created by Schuckmann on 12.05.2016.
 */
public class MusicConnection extends HttpConnection {

    private String username;
    private String password;
    private String botId;
    private String token;
    private List<String> enabledInstances;

    // Cache
    private Map<String, Track> cachedTrackList;
    private Map<String, Playlist> cachedPlaylistList;

    private MusicConnection(String baseUrl, String host, int port, String username, String password, String botId, Consumer<MusicConnection> consumer) throws IOException {
        super(String.format(baseUrl, host, port));
        this.username = username;
        this.password = password;
        this.botId = botId;
        token = fetchTokenSynced(username, password);
        if (token == null)
            throw new IOException("Failed to fetch token.");
        System.out.println("Your Token: " + token);
        fetchEnabledInstances((instanceList) -> {
            enabledInstances = instanceList;
            consumer.accept(this);
        });
    }

    private String fetchTokenSynced(String username, String password) {
        AtomicReference<String> token = new AtomicReference<>();
        execute(new PostRequest(url(url, MusicRequests.GENERAL_LOGIN)).withBody(TokenUtils.prepareAuth(username, password, botId), ContentType.APPLICATION_JSON).build(), (httpResponse) -> {
            token.set(TokenUtils.readToken(httpResponse));
        });
        return token.get();
    }

    public void fetchEnabledInstances(Consumer<List<String>> consumer) {
        executeAsync(new GetRequest(url(url, MusicRequests.INSTANCES_LIST)).withAuth(token).build(), (httpResponse) -> {
            List<String> instanceList = new ArrayList<>();
            try {
                read(httpResponse, JsonArray.class).forEach((jsonElement) -> {
                    JsonObject jsonInstance = jsonElement.getAsJsonObject();
                    if (jsonInstance.get("running").getAsBoolean())
                        instanceList.add(jsonInstance.get("uuid").getAsString());
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            consumer.accept(instanceList);
        });
    }

    public void fetchTrackList(Consumer<List<Track>> consumer) {
        service.execute(() -> consumer.accept(fetchTrackListSynced()));
    }

    private List<Track> fetchTrackListSynced() {
        if (cachedTrackList != null)
            return new ArrayList<>(cachedTrackList.values());
        List<Track> trackList = new ArrayList<>();
        execute(new GetRequest(url(url, MusicRequests.FILELIST_LIST)).withAuth(token).build(), (httpResponse) -> {
            try {
                read(httpResponse, JsonArray.class).forEach((jsonElement) -> trackList.add(gson.fromJson(jsonElement, Track.class)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        cachedTrackList = new HashMap<>();
        for (Track track : trackList)
            cachedTrackList.put(track.getUuid(), track);
        return trackList;
    }

    /**
     * Cause there is no api method for this, we need to fetch all tracks first, if not cached.
     *
     * @param uuid The uuid of the track.
     * @param consumer The consumer that consumes the track. Or null if no track was found.
     */
    public void fetchTrack(String uuid, Consumer<Track> consumer) {
        service.execute(() -> consumer.accept(fetchTrackSynced(uuid)));
    }

    private Track fetchTrackSynced(String uuid) {
        if (cachedTrackList != null) {
            if (cachedTrackList.containsKey(uuid))
                return cachedTrackList.get(uuid);
            fetchTrackListSynced();
            if (cachedTrackList.containsKey(uuid))
                return cachedTrackList.get(uuid);
        }
        return null;
    }

    public void fetchInstanceStatus(Consumer<Instance> consumer) {
        executeAsync(new GetRequest(url(url, MusicRequests.INSTANCES_STATUS, enabledInstances.get(0))).withAuth(token).build(), (httpResponse) -> {
            Instance instance = null;
            try {
                instance = read(httpResponse, Instance.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            consumer.accept(instance);
        });
    }

    /**
     * This method actually makes two calls:
     * <ul>
     *     <li>Getting the playlist (uuids).</li>
     *     <li>Getting the entries of the playlist.</li>
     * </ul>
     *
     * @param consumer The consumer that consumes the playlist list
     */
    public void fetchPlaylistList(Consumer<List<Playlist>> consumer) {
        if (cachedPlaylistList != null) {
            consumer.accept(new ArrayList<>(cachedPlaylistList.values()));
            return;
        }
        else if (enabledInstances == null || enabledInstances.isEmpty()) {
            consumer.accept(new ArrayList<>());
            return;
        }
        executeAsync(new GetRequest(url(url, MusicRequests.PLAYLIST_LIST, enabledInstances.get(0))).withAuth(token).build(), (httpResponse) -> {
            List<Playlist> playlistList = new ArrayList<>();
            try {
                JsonArray jsonArray = read(httpResponse, JsonArray.class);
                int size = jsonArray.size();
                AtomicInteger index = new AtomicInteger();
                jsonArray.forEach((jsonSubElement) -> {
                    String uuid = jsonSubElement.getAsJsonObject().get("uuid").getAsString();
                    Playlist playlist = fetchPlaylistSynced(uuid);
                    if (playlist != null)
                        playlistList.add(playlist);
                });
                cachedPlaylistList = new HashMap<>();
                for (Playlist cachedPlaylist : playlistList)
                    cachedPlaylistList.put(cachedPlaylist.getUuid(), cachedPlaylist);
            } catch (IOException e) {
                e.printStackTrace();
            }
            consumer.accept(playlistList);
        });
    }

    public void fetchPlaylist(String uuid, Consumer<Playlist> consumer) {
        service.execute(() -> consumer.accept(fetchPlaylistSynced(uuid)));
    }

    private Playlist fetchPlaylistSynced(String uuid) {
        if (cachedPlaylistList != null && cachedPlaylistList.containsKey(uuid))
            return cachedPlaylistList.get(uuid);
        AtomicReference<Playlist> playlist = new AtomicReference<>();
        execute(new GetRequest(url(url, MusicRequests.PLAYLIST_ITEM, uuid)).withAuth(token).build(), (httpResponse) -> {
            try {
                playlist.set(read(httpResponse, Playlist.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return playlist.get();
    }

    public void fetchPlaylistTrackList(Playlist playlist, Consumer<List<Track>> consumer) {
        service.execute(() -> consumer.accept(fetchPlaylistTrackListSynced(playlist)));
    }

    public void fetchPlaylistTrackList(String uuid, Consumer<List<Track>> consumer) {
        service.execute(() -> consumer.accept(fetchPlaylistTrackListSynced(fetchPlaylistSynced(uuid))));
    }

    private List<Track> fetchPlaylistTrackListSynced(Playlist playlist) {
        List<Track> tracks = new ArrayList<>();
        for (String uuid : playlist.getEntries())
            tracks.add(fetchTrackSynced(uuid));
        return tracks;
    }

    public void fetchStreamingToken(Consumer<String> consumer) {
        executeAsync(new PostRequest(url(url, MusicRequests.STREAMING_TOKEN, enabledInstances.get(0))).withAuth(token).build(), (httpResponse) -> {
            try {
                JsonObject jsonObject = read(httpResponse, JsonObject.class);
                consumer.accept(jsonObject.get("token").getAsString());
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
            consumer.accept(null);
        });
    }

    public void fetchStreamingStream(String token, Consumer<InputStream> consumer) {
        listenAsync(new GetRequest(url(url, MusicRequests.STREAMING_STREAM, enabledInstances.get(0), token)).withAuth(this.token).build(), (httpResponse) -> {
            try {
                consumer.accept(httpResponse.getEntity().getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, (cancel) -> {

        });
    }

    public void pushTrackPlay(String trackId) {
        executeAsync(new PostRequest(url(url, MusicRequests.PLAYBACK_PLAY_FILE, enabledInstances.get(0), trackId)).withAuth(token).build(), (httpResponse) -> {});
    }

    public void pushPlaylistTrackPlay(String playlistId, int trackIndex) {
        executeAsync(new PostRequest(url(url, MusicRequests.PLAYBACK_PLAY_PLAYLIST_FILE, enabledInstances.get(0), playlistId, trackIndex)).withAuth(token).build(), (httpRepsonse) -> {});
    }

    public void pushSetVolume(int volume) {
        executeAsync(new PostRequest(url(url, MusicRequests.PLAYBACK_SET_VOLUME, enabledInstances.get(0), volume)).withAuth(token).build(), (httpReponse) -> {});
    }

    public void clearCaches() {
        cachedTrackList = null;
        cachedPlaylistList = null;
    }

    @Override
    public boolean onSuccess(ByteArrayOutputStream outputStream, int statusCode) {
        try {
            System.out.println(IOUtils.toString(new ByteArrayInputStream(outputStream.toByteArray())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (statusCode >= 200 && statusCode < 300) {
            System.out.println("Request was successful.");
            return true;
        }
        else if (statusCode == 401 || statusCode == 403) {
            System.out.println("Token has elapsed. Refreshing now");
            token = fetchTokenSynced(username, password);
            Logger.log("§cPlease try again, your token had elapsed and is now refreshed.");
        }
        else {
            Logger.log("§cAn error occured: " + statusCode + ", check the console for details.");
        }
        return false;
    }

    @Override
    public void onError(IOException e) {
        Logger.log(
                "§cFailed to connect to SinusBot.",
                "§fCheck the console for errors."
        );
    }

    public static void connect(String host, int port, String username, String password, String botId, Consumer<MusicConnection> consumer) {
        try {
            new MusicConnection("http://%s:%d/api/v1/", host, port, username, password, botId, (musicConnection) -> consumer.accept(musicConnection));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
