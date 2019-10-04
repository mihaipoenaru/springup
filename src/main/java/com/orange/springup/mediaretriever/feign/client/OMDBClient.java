package com.orange.springup.mediaretriever.feign.client;

import com.orange.springup.mediaretriever.dtos.MediaDto;
import com.orange.springup.mediaretriever.dtos.SeasonEpisodesDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "omdb-client", url = "${omdb.url}")
public interface OMDBClient {

    @GetMapping("/?apiKey={key}&t={title}")
    MediaDto searchByTitle(@PathVariable("title") String title, @PathVariable("key") String key);

    @GetMapping("/?apiKey={key}&t={mediaId}&Season={seasonNumber}")
    SeasonEpisodesDto getSeasonEpisodesById(@PathVariable("mediaId") String mediaId, @PathVariable("seasonNumber") Integer seasonNumber, @PathVariable("key") String key);

}
