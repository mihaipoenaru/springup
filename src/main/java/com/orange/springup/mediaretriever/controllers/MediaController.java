package com.orange.springup.mediaretriever.controllers;

import com.orange.springup.mediaretriever.business.objects.Media;
import com.orange.springup.mediaretriever.services.OMDBDownloader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@RestController
public class MediaController {
    private final OMDBDownloader downloader;

    @Autowired
    public MediaController(OMDBDownloader downloader) {
        this.downloader = downloader;
    }

    @GetMapping("/GetMedia")
    public List<Media> getMovies() throws IOException, URISyntaxException {
        return downloader.getAllMedia();
    }
}
