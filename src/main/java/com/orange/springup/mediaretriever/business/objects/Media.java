package com.orange.springup.mediaretriever.business.objects;

import java.time.LocalDate;
import java.util.Set;

public abstract class Media {

    public String id;
    public String title;
    public LocalDate release;
    public String description;
    public String director;
    public Set<String> actors;
    public Double averageRating;
}