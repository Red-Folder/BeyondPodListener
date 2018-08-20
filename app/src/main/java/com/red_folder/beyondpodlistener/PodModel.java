package com.red_folder.beyondpodlistener;

import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;

public class PodModel {
    private Date created = new Date();
    private boolean playing;
    private String feedname;
    private String feedurl;
    private String episodeName;
    private String episodeUrl;
    private String episodeFile;
    private String episodePostUrl;
    private String episodeMime;
    private String episodeSummary;
    private long episodeDuration;
    private long episodePosition;
    private String artist;
    private String album;
    private String track;

    public Date getCreated() { return created; };
    public void setCreated(Date created) { this.created = created; }

    public boolean getPlaying() { return playing; };
    public void setPlaying(boolean playing) { this.playing = playing; }

    public String getFeedname() { return feedname; };
    public void setFeedname(String feedname) { this.feedname = feedname; }

    public String getFeedurl() { return feedurl; };
    public void setFeedurl(String feedurl) { this.feedurl = feedurl; }

    public String getEpisodeName() { return episodeName; };
    public void setEpisodeName(String episodeName) { this.episodeName = episodeName; }

    public String getEpisodeUrl() { return episodeUrl; };
    public void setEpisodeUrl(String episodeUrl) { this.episodeUrl = episodeUrl; }

    public String getEpisodeFile() { return episodeFile; };
    public void setEpisodeFile(String episodeFile) { this.episodeFile = episodeFile; }

    public String getEpisodePostUrl() { return episodePostUrl; };
    public void setEpisodePostUrl(String episodePostUrl) { this.episodePostUrl = episodePostUrl; }

    public String getEpisodeMime() { return episodeMime; };
    public void setEpisodeMime(String episodeMime) { this.episodeMime = episodeMime; }

    public String getEpisodeSummary() { return episodeSummary; };
    public void setEpisodeSummary(String episodeSummary) { this.episodeSummary = episodeSummary; }

    public long getEpisodeDuration() { return episodeDuration; };
    public void setEpisodeDuration(long episodeDuration) { this.episodeDuration = episodeDuration; }

    public long getEpisodePosition() { return episodePosition; };
    public void setEpisodePosition(long episodePosition) { this.episodePosition = episodePosition; }

    public String getArtist() { return artist; };
    public void setArtist(String artist) { this.artist = artist; }

    public String getAlbum() { return album; };
    public void setAlbum(String album) { this.album = album; }

    public String getTrack() { return track; };
    public void setTrack(String track) { this.track = track; }

    public static PodModel fromIntent(Intent intent) {
        PodModel newModel = new PodModel();
        newModel.setPlaying(intent.getBooleanExtra("playing", false));
        newModel.setFeedname(intent.getStringExtra("feed-name"));
        newModel.setFeedurl(intent.getStringExtra("feed-url"));
        newModel.setEpisodeName(intent.getStringExtra("episode-name"));
        newModel.setEpisodeUrl(intent.getStringExtra("episode-url"));
        newModel.setEpisodeFile(intent.getStringExtra("episode-file"));
        newModel.setEpisodePostUrl(intent.getStringExtra("episode-post-url"));
        newModel.setEpisodeMime(intent.getStringExtra("episode-mime"));
        newModel.setEpisodeSummary(intent.getStringExtra("episode-summary"));
        newModel.setEpisodeDuration(intent.getLongExtra("episode-duration", -1));
        newModel.setEpisodePosition(intent.getLongExtra("episode-position", -1));
        newModel.setArtist(intent.getStringExtra("artist"));
        newModel.setAlbum(intent.getStringExtra("album"));
        newModel.setTrack(intent.getStringExtra("track"));

        return newModel;
    }

    public String toJson() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Type type =  new TypeToken<PodModel>() {}.getType();
        return gson.toJson(this);
    }
}
