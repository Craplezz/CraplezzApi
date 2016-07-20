package me.mani.clapi.music.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Schuckmann on 13.05.2016.
 */
public class Instance {

    @SerializedName( "v" )
    private String botVersion;
    private Track currentTrack;
    private int position;
    private boolean running;
    private boolean playing;
    private boolean shuffe;
    private boolean repeat;
    private int volume;
    private boolean needsRestart;
    private String playlist;
    private String playlistTrack;
    @SerializedName( "queueLen" )
    private int queueLenght;
    private String queueVersion;
    private int modes;
    private int downloaded;
    private String serverUID;
    private int flags;
    private int muted;

    public String getBotVersion() {
        return botVersion;
    }

    public Track getCurrentTrack() {
        return currentTrack;
    }

    public int getPosition() {
        return position;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isPlaying() {
        return playing;
    }

    public boolean isShuffe() {
        return shuffe;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public int getVolume() {
        return volume;
    }

    public boolean isNeedsRestart() {
        return needsRestart;
    }

    public String getPlaylist() {
        return playlist;
    }

    public String getPlaylistTrack() {
        return playlistTrack;
    }

    public int getQueueLenght() {
        return queueLenght;
    }

    public String getQueueVersion() {
        return queueVersion;
    }

    public int getModes() {
        return modes;
    }

    public int getDownloaded() {
        return downloaded;
    }

    public String getServerUID() {
        return serverUID;
    }

    public int getFlags() {
        return flags;
    }

    public int getMuted() {
        return muted;
    }
}
