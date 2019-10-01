
package com.orange.springup.mediaretriever.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MediaDto {

    @JsonProperty("Title")
    public String title;
    @JsonProperty("Year")
    public String year;
    @JsonProperty("Rated")
    public String rated;
    @JsonProperty("Released")
    public String released;
    @JsonProperty("Runtime")
    public String runtime;
    @JsonProperty("Genre")
    public String genre;
    @JsonProperty("Director")
    public String director;
    @JsonProperty("Writer")
    public String writer;
    @JsonProperty("Actors")
    public String actors;
    @JsonProperty("Plot")
    public String plot;
    public String totalSeasons;
    @JsonProperty("Language")
    public String language;
    @JsonProperty("Country")
    public String country;
    @JsonProperty("Awards")
    public String awards;
    @JsonProperty("Poster")
    public String poster;
    @JsonProperty("Ratings")
    public List<RatingDto> ratingDtos = null;
    @JsonProperty("Metascore")
    public String metascore;
    @JsonProperty("imdbRating")
    public String imdbRating;
    @JsonProperty("imdbVotes")
    public String imdbVotes;
    @JsonProperty("imdbID")
    public String imdbID;
    @JsonProperty("Type")
    public String type;
    @JsonProperty("DVD")
    public String dVD;
    @JsonProperty("BoxOffice")
    public String boxOffice;
    @JsonProperty("Production")
    public String production;
    @JsonProperty("Website")
    public String website;
    @JsonProperty("Response")
    public String response;
}
