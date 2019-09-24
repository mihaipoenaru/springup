
package com.orange.springup.movieretriever.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RatingDto {

    @JsonProperty("Source")
    public String source;
    @JsonProperty("Value")
    public String value;
}
