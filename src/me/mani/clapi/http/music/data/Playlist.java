package me.mani.clapi.http.music.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Schuckmann on 15.05.2016.
 */
public class Playlist {

    private String uuid;
    private String name;
    private List<String> entries;
    private int count;

    private transient List<Track> tracks = new ArrayList<>();

    public int getCount() {
        return count;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public List<String> getEntries() {
        return entries;
    }

    public boolean isSynced() {
        return tracks.size() == entries.size();
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public static Playlist tempPlaylist(List<Track> tracks) {
        Playlist playlist = new Playlist();
        playlist.uuid = UUID.randomUUID().toString();
        playlist.name = "temp";
        playlist.tracks = tracks;
        playlist.entries = new ArrayList<>();
        for (Track track : tracks)
            playlist.entries.add(track.getUuid());
        playlist.count = tracks.size();
        return playlist;
    }

}
