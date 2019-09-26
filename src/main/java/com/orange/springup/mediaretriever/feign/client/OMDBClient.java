package com.orange.springup.mediaretriever.feign.client;

import com.orange.springup.mediaretriever.dtos.MovieDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "omdb-client", url = "http://www.omdbapi.com")
public interface OMDBClient {

    @GetMapping("/?apiKey={key}&t={title}")
    MovieDto searchByTitle(@PathVariable("title") String title, @PathVariable("key") String key);
}
