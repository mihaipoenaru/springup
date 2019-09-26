package com.orange.springup.mediaretriever.mongo;

import com.orange.springup.mediaretriever.business.objects.Media;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MediaRepository extends MongoRepository<Media, String> {
    List<Media> findAllByTitle(String title);
}
