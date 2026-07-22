package com.example.tcmapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class ExternalHerbService {

    private static final Map<String, String> WIKI_LANGUAGES = Map.of(
            "en", "en", "zh", "zh", "ms", "ms"
    );

    private final RestClient restClient = RestClient.create();

    public List<ExternalHerb> search(String keyword, String language) {
        String lang = WIKI_LANGUAGES.getOrDefault(language, "en");
        URI uri = UriComponentsBuilder
                .fromUriString("https://" + lang + ".wikipedia.org/w/api.php")
                .queryParam("action", "query")
                .queryParam("generator", "search")
                .queryParam("gsrsearch", keyword + " herb")
                .queryParam("gsrnamespace", 0)
                .queryParam("gsrlimit", 8)
                .queryParam("prop", "extracts|pageimages|info")
                .queryParam("exintro", 1)
                .queryParam("explaintext", 1)
                .queryParam("exsentences", 3)
                .queryParam("piprop", "thumbnail")
                .queryParam("pithumbsize", 480)
                .queryParam("inprop", "url")
                .queryParam("format", "json")
                .queryParam("formatversion", 2)
                .build().encode().toUri();

        JsonNode response = restClient.get().uri(uri).retrieve().body(JsonNode.class);
        List<ExternalHerb> results = new ArrayList<>();
        if (response == null) return results;

        for (JsonNode page : response.path("query").path("pages")) {
            String title = page.path("title").asText();
            String extract = page.path("extract").asText();
            if (title.isBlank() || extract.isBlank()) continue;
            results.add(new ExternalHerb(
                    title,
                    page.path("thumbnail").path("source").asText(""),
                    extract,
                    page.path("fullurl").asText(""),
                    "Wikipedia"
            ));
        }
        results.sort(Comparator.comparing(ExternalHerb::name));
        return results;
    }

    public record ExternalHerb(String name, String image, String description,
                               String sourceUrl, String sourceName) {}
}
