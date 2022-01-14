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
    // Lean app token to go in the header of each call
    private final String leanAppToken = "xxx";

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    // Instantiating and injecting restTemplate (our http client)
    @Bean
    RestTemplate restTemplate(RestTemplateBuilder preconfiguredBuilder) {
        return preconfiguredBuilder
                .additionalInterceptors(new LoggingInterceptor())
                .rootUri("https://api.leantech.me")
                .errorHandler(new RestErrorHandler())
                .requestFactory(() -> new BufferingClientHttpRequestFactory(preconfiguredBuilder.buildRequestFactory()))
                .build();
    }

    // Instantiating and injecting MTLS logic (Will be used by our http client)
    @Bean
    RestTemplateCustomizer sslRestTemplateCustomizer() {
        return new SSLCustomizer();
    }

    @Override
    public void run(String... args) {
        log.info("Starting");
        getBanks();
        log.info("Finished");
    }

    // method to call the banks endpoint
    private String getBanks() {
        var getBanksResponse = makeGetCall("/banks/v1", String.class);
        return getBanksResponse;
    }

    // generic get method to call the bank with the correct headers and return the correct required object class
    protected <T, R> T makeGetCall(String urlPath, Class<T> responseClass) {
        HttpHeaders defaultHeaders = new HttpHeaders();
        defaultHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        defaultHeaders.add("lean-app-token", leanAppToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(defaultHeaders);
        return template.exchange(urlPath, HttpMethod.GET, requestEntity, responseClass).getBody();
    }

}
