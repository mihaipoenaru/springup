package com.orange.springup.mediaretriever.services;

import com.orange.springup.mediaretriever.business.objects.Media;
import com.orange.springup.mediaretriever.dtos.MovieDto;
import com.orange.springup.mediaretriever.feign.client.OMDBClient;
import com.orange.springup.mediaretriever.mongo.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class OMDBDownloader {
    private final OMDBClient client;
    private final MediaRepository mongoRepo;
    private final ExecutorService threadPool;

    private String apiKey;

    @Autowired
    public OMDBDownloader(OMDBClient client, MediaRepository mongoRepo) {
        this.client = client;
        this.mongoRepo = mongoRepo;

        threadPool = Executors.newCachedThreadPool();
    }

    private List<Media> getAllMedia() throws IOException {
        Files.readAllLines(Paths.get("media_list.txt"))
                .stream()
                .map(this::searchAsync)
                .collect(Collectors.toList())
                .stream()
                .map(this::waitForResult)
                .filter(Objects::nonNull);

        return null;
    }

    private MovieDto waitForResult(Future<MovieDto> f) {
        try {
            return f.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Future<MovieDto> searchAsync(String searchQuery) {
        return threadPool.submit(() -> client.searchByTitle(searchQuery, apiKey));
    }

    @Value("${omdb.apiKey}")
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
