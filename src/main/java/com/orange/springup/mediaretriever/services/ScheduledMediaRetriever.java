package com.orange.springup.mediaretriever.services;

import com.orange.springup.mediaretriever.business.objects.Media;
import com.orange.springup.mediaretriever.mongo.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduledMediaRetriever {
    private final OMDBDownloader downloader;
    private final MediaRepository repository;

    @Autowired
    public ScheduledMediaRetriever(OMDBDownloader downloader, MongoTemplate template, MediaRepository repository) {
        this.downloader = downloader;
        this.repository = repository;

    }

    @Scheduled(fixedRate = 30 * 1000)
    public void fillDb() throws Exception {
        System.out.println("pushing data");
        List<Media> allMedia = downloader.getAllMedia();
        allMedia.forEach(this::trySave);
    }

    private void trySave(Media media) {
        try {
            repository.save(media);
        } catch (Exception e) {

        }
    }
}
