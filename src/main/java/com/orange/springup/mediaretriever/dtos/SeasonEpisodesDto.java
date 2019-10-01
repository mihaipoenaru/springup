package com.orange.springup.mediaretriever.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SeasonEpisodesDto {
    @JsonProperty("Season")
    public String seasonNumber;
    @JsonProperty("Episodes")
    public List<EpisodeDto> episodes;
}
