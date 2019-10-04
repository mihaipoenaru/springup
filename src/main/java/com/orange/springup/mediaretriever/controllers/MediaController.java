package com.orange.springup.mediaretriever.controllers;

import com.orange.springup.mediaretriever.business.objects.Media;
import com.orange.springup.mediaretriever.mongo.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MediaController {
    private final MediaRepository repo;
    @Autowired
    public MediaController(MediaRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/GetMedia")
    public List<Media> getMovies(@RequestParam(required = false) String title) {
        if (title == null) {
            return repo.findAll();
        }

        return repo.findAllByTitle(title);
    }
}
