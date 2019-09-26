
package com.orange.springup.mediaretriever.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RatingDto {

    @JsonProperty("Source")
    public String source;
    @JsonProperty("Value")
    public String value;
}
