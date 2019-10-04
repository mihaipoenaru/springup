package com.orange.springup.mediaretriever.business.objects;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Set;

@Document(collection = "media")
public abstract class Media {

    public String id;
    @Indexed(unique = true)
    public String title;
    public LocalDate release;
    public String description;
    public String director;
    public Set<String> actors;
    public Double averageRating;
}