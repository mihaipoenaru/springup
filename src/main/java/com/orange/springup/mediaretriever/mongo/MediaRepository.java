package com.orange.springup.mediaretriever.mongo;

import com.orange.springup.mediaretriever.business.objects.Media;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MediaRepository extends MongoRepository<Media, String> {
    @Query(value = "{'title': {$regex: '.*?0.*', $options: 'i'}}")
    List<Media> findAllByTitle(String title);
}
