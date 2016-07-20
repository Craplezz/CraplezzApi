package me.mani.clapi.music.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Schuckmann on 12.05.2016.
 */
public class Track {

    public static final String TYPE_FILE = "file";
    public static final String TYPE_URL = "url";

    // Required
    private String uuid;
    @SerializedName( "parent" )
    private String parentUuid;
    private String type;

    // Optional
    private String title = "?";
    private String artist = "?";
    private String tempTitle = "?";
    private String tempArtist = "?";
    private String album;
    private String albumArtist;
    @SerializedName( "track" )
    private int trackId;
    private int totalTracks;
    private String copyright;
    private String genre;
    @SerializedName( "thumbnail" )
    private String thumbnailUrl;
    private int duration; // In Milliseconds
    private int bitrate;
    private int channels;
    private int samplerate;
    private int filesize;

    public String getUuid() {
        return uuid;
    }

    public String getParentUuid() {
        return parentUuid;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getTempTitle() {
        return tempTitle;
    }

    public String getTempArtist() {
        return tempArtist;
    }

    public String getAlbum() {
        return album;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    public int getTrackId() {
        return trackId;
    }

    public int getTotalTracks() {
        return totalTracks;
    }

    public String getCopyright() {
        return copyright;
    }

    public String getGenre() {
        return genre;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public int getDuration() {
        return duration;
    }

    public int getBitrate() {
        return bitrate;
    }

    public int getChannels() {
        return channels;
    }

    public int getSamplerate() {
        return samplerate;
    }

    public int getFilesize() {
        return filesize;
    }
}
