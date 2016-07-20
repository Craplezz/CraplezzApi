package me.mani.clapi.music;

/**
 * Created by Schuckmann on 12.05.2016.
 */
public enum MusicRequests {

    GENERAL_LOGIN ("bot/login"),
    INSTANCES_LIST("bot/instances"),
    INSTANCES_STATUS ("bot/i/%s/status"),
    FILELIST_LIST ("bot/files"),
    PLAYBACK_PLAY_FILE ("bot/i/%s/play/byId/%s"),
    PLAYBACK_PLAY_PLAYLIST_FILE ("bot/i/%s/play/byList/%s/%d"),
    PLAYBACK_SET_VOLUME ("bot/i/%s/volume/set/%d"),
    PLAYLIST_LIST ("bot/playlists"),
    PLAYLIST_ITEM ("bot/playlists/%s"),
    STREAMING_TOKEN ("bot/i/%s/streamToken"),
    STREAMING_STREAM ("bot/i/%s/stream/%s");

    private String path;

    MusicRequests(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
