package com.orange.springup.mediaretriever.business.objects;

import org.springframework.data.annotation.Id;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class Show extends Media {
    @Id
    public String id;
    public Integer seasonNumber;
    public LocalDate finalSeasonDate;
    public Duration averageEpisodeRuntime;
    public Map<Integer, List<Episode>> seasons;
}