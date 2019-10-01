package com.orange.springup.mediaretriever.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EpisodeDto {
    @JsonProperty("Title")
    public String title;
    @JsonProperty("Released")
    public String released;
    @JsonProperty("Episode")
    public String episodeNumber;
    @JsonProperty("imdbRating")
    public String rating;
    @JsonProperty("imdbId")
    public String id;
}
