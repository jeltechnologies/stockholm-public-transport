package com.jeltechnologies.mcp.sl.departures;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Component
public class SiteRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(SiteRepository.class);
    
    private final static Path FILE_PATH = Paths.get(System.getProperty("java.io.tmpdir") + "/stockholm-public-transport-sites.json");

    private static Duration CACHE_EXPIRES = Duration.ofDays(7);
    
    private record SiteCache(LocalDateTime expires, List<Site> sites) {
        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expires);
        }
    };

    private final RestClient slTransportRestClient;
    
    private final ObjectMapper objectMapper;

    public SiteRepository(ObjectMapper objectMapper, RestClient slTransportRestClient) {
        this.objectMapper = objectMapper;
        this.slTransportRestClient = slTransportRestClient;
    }
    
    public Sites getSites() throws IOException {
        boolean cacheOk = false;
        SiteCache cache = null;
        try {
            cache = load();
            if (cache != null) {
                cacheOk = !cache.isExpired();
            }
        }
        catch (Exception e) {
            LOGGER.warn("Could not load cache from disk, will create new one");
        }
        if (!cacheOk) {
            List<Site> sites = slTransportRestClient.get()
                    .uri("/sites?expand=true")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Site>>() {
                    });
            cache = new SiteCache(LocalDateTime.now().plus(CACHE_EXPIRES), sites);
            save(cache);
        }
        Sites result = new Sites(cache.sites());
        return result;
    }

    private SiteCache load() throws Exception {
        SiteCache result = null;
        if (Files.exists(FILE_PATH)) {
            try (var reader = Files.newBufferedReader(FILE_PATH, StandardCharsets.UTF_8)) {
                result = objectMapper.readValue(reader, new TypeReference<>() {
                });
            }
        }
        if (LOGGER.isDebugEnabled()) {
            String resultDescription;
            if (result == null) {
                resultDescription = "null";
            }
            else {
                resultDescription = "cache expiring at " + result.expires() + ", containing " + result.sites.size() + " sites";
            }
            LOGGER.debug("Loaded " + resultDescription + ", from file " + FILE_PATH.toString());
        }
        return result;
    }

    private void save(SiteCache siteCache) throws IOException {
        if (siteCache != null) {
            Files.createDirectories(FILE_PATH.getParent());
            try (var writer = Files.newBufferedWriter(FILE_PATH, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {
                objectMapper.writerWithDefaultPrettyPrinter()
                        .writeValue(writer, siteCache);
            }
            LOGGER.debug("Saved sites to file " + FILE_PATH.toString());
        }
    }
}
