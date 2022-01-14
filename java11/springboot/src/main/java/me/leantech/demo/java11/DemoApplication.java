package me.leantech.demo.java11;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.client.RestTemplate;


@Slf4j
@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    @Autowired
    private RestTemplate template;
    private final String leanAppToken = "xxx";

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    RestTemplate restTemplate(RestTemplateBuilder preconfiguredBuilder) {
        return preconfiguredBuilder
                .additionalInterceptors(new LoggingInterceptor())
                .rootUri("https://api.leantech.me")
                .errorHandler(new RestErrorHandler())
                .requestFactory(() -> new BufferingClientHttpRequestFactory(preconfiguredBuilder.buildRequestFactory()))
                .build();
    }

    @Bean
    @Profile("SSL")
    RestTemplateCustomizer sslRestTemplateCustomizer() {
        return new SSLCustomizer();
    }

    @Bean
    ObjectMapper camelCaseMapper(Jackson2ObjectMapperBuilder objectMapperBuilder) {
        ObjectMapper objectMapper = objectMapperBuilder.build();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Override
    public void run(String... args) {
        log.info("Starting");
        getBanks();
        log.info("Finished");
    }

    private String getBanks() {
        var getBanksResponse = makeGetCall("/banks/v1", String.class);
        return getBanksResponse;
    }

    protected <T, R> T makeGetCall(String urlPath, Class<T> responseClass) {
        HttpHeaders defaultHeaders = new HttpHeaders();
        defaultHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        defaultHeaders.add("lean-app-token", leanAppToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(defaultHeaders);
        return template.exchange(urlPath, HttpMethod.GET, requestEntity, responseClass).getBody();
    }

}
