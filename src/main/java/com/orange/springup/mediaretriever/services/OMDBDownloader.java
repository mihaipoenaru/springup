package com.orange.springup.mediaretriever.services;

import com.orange.springup.mediaretriever.business.objects.Episode;
import com.orange.springup.mediaretriever.business.objects.Media;
import com.orange.springup.mediaretriever.business.objects.Movie;
import com.orange.springup.mediaretriever.business.objects.Show;
import com.orange.springup.mediaretriever.dtos.EpisodeDto;
import com.orange.springup.mediaretriever.dtos.MediaDto;
import com.orange.springup.mediaretriever.dtos.RatingDto;
import com.orange.springup.mediaretriever.dtos.SeasonEpisodesDto;
import com.orange.springup.mediaretriever.feign.client.OMDBClient;
import com.orange.springup.mediaretriever.mongo.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

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

    public List<Media> getAllMedia() throws IOException, URISyntaxException {
        return Files.readAllLines(Paths.get(OMDBDownloader.class.getClassLoader().getResource("media_list.txt").toURI()))
                .stream()
                .map(this::searchAsync)
                .collect(toList())
                .stream()
                .map(this::waitForResult)
                .filter(Objects::nonNull)
                .map(this::mediaDtoToMedia)
                .collect(toList());

    }

    private Media mediaDtoToMedia(MediaDto mediaDto) {
        Media result;
        switch (mediaDto.type.toLowerCase()) {
            case "movie":
                result = new Movie();
                ((Movie) result).runtime = parseDuration(mediaDto.runtime);

                break;
            case "series":
                result = new Show();
                Show show = (Show) result;

                show.numberOfSeasons = Integer.parseInt(mediaDto.totalSeasons);
                show.seasons = retrieveAllSeasons(mediaDto.imdbID, show.numberOfSeasons);
                show.averageEpisodeRuntime = parseDuration(mediaDto.runtime);
                show.finalSeasonDate = show.seasons.get(show.numberOfSeasons)
                        .stream()
                        .map(e -> e.release)
                        .max(comparing(identity()))
                        .orElse(LocalDate.MIN);

                break;
            default:
                throw new RuntimeException();
        }

        setCommonFields(result, mediaDto);
        return result;
    }

    private Duration parseDuration(String runtimeString) {
        Matcher matcher = Pattern.compile("([0-9]+).*").matcher(runtimeString);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Cannot extract duration from string: " + runtimeString);
        }
        return Duration.ofMinutes(Long.parseLong(matcher.group(1)));
    }

    private Map<Integer, List<Episode>> retrieveAllSeasons(String showId, Integer numberOfSeasons) {
        return IntStream.range(1, numberOfSeasons + 1)
                .mapToObj(seasonNumber -> getSeasonEpisodesAsync(seasonNumber, showId))
                .collect(toList())
                .stream()
                .map(this::waitForResult)
                .filter(this::notNull)
                .collect(toMap(
                        e -> Integer.parseInt(e.seasonNumber),
                        e -> e.episodes.stream().map(this::episodeDtoToEpisode).collect(toList())
                ));
    }

    private boolean notNull(SeasonEpisodesDto seasonEpisodesDto) {
        return seasonEpisodesDto != null && seasonEpisodesDto.seasonNumber != null && seasonEpisodesDto.episodes != null;
    }

    private Episode episodeDtoToEpisode(EpisodeDto episodeDto) {
        Episode result = new Episode();

        result.name = episodeDto.title;
        result.number = Integer.parseInt(episodeDto.episodeNumber);
        result.rating = Double.parseDouble(episodeDto.rating);
        try {
            result.release = LocalDate.parse(episodeDto.released, ISO_DATE);
        } catch (DateTimeParseException e) {
            result.release = null;
        }

        return result;
    }

    private Future<SeasonEpisodesDto> getSeasonEpisodesAsync(int seasonNumber, String showId) {
        return threadPool.submit(() -> client.getSeasonEpisodesById(showId, seasonNumber, apiKey));
    }

    private void setCommonFields(Media result, MediaDto mediaDto) {
        result.title = mediaDto.title;
        result.director = mediaDto.director;
        result.description = generateDescription(mediaDto);
        result.release = LocalDate.parse(mediaDto.released, DateTimeFormatter.ofPattern("dd MMM yyyy"));
        result.actors = getActorSet(mediaDto);
        result.averageRating = calculateAverageRating(mediaDto);
    }

    private String generateDescription(MediaDto mediaDto) {
        return mediaDto.plot + " " + mediaDto.awards;
    }

    private double calculateAverageRating(MediaDto mediaDto) {
        double averageRating = mediaDto.ratingDtos.stream()
                .mapToDouble(this::parseRating)
                .average()
                .orElseGet(() -> Double.parseDouble(mediaDto.imdbRating));
        DecimalFormat formatter = new DecimalFormat("#.#");
        formatter.setRoundingMode(RoundingMode.HALF_EVEN);

        return Double.parseDouble(formatter.format(averageRating));
    }

    private Set<String> getActorSet(MediaDto mediaDto) {
        return Stream.of(mediaDto.actors.split(",\\s*")).collect(toSet());
    }

    private double parseRating(RatingDto rating) {
        if (rating.value.endsWith("%")) {
            return Double.parseDouble(rating.value.replaceFirst("%", ""));
        }

        if (rating.value.contains("/")) {
            String[] splitScore = rating.value.split("/");

            double score = Double.parseDouble(splitScore[0]);
            double maxScore = Double.parseDouble(splitScore[1]);

            return score * (100.0 / maxScore);
        }

        throw new IllegalArgumentException("Unrecognized rating pattern " + rating.value);
    }

    private <Result> Result waitForResult(Future<Result> f) {
        try {
            return f.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Future<MediaDto> searchAsync(String searchQuery) {
        return threadPool.submit(() -> client.searchByTitle(searchQuery, apiKey));
    }

    @Value("${omdb.apiKey}")
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
