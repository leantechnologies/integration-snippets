package me.leantech.dev.springboot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Slf4j
@SpringBootApplication
public class MtlsUsingRestTemplate implements CommandLineRunner {

    @Autowired
    private RestTemplate template;

    @Value("base.url")
    private String baseUrl;
    @Value("app.token")
    private String appToken;

    public static void main(String[] args) {
        SpringApplication.run(MtlsUsingRestTemplate.class, args);
    }

    // Instantiating and injecting restTemplate (our http client)
    @Bean
    RestTemplate restTemplate(RestTemplateBuilder preconfiguredBuilder) {
        return preconfiguredBuilder
                .additionalInterceptors(new LoggingInterceptor())
                .rootUri(baseUrl)
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
    public void run(String... args) throws IOException {
        log.info("Starting");
        getBanks();
        log.info("Finished");
    }

    // method to call the banks endpoint
    private void getBanks() {
        String urlPath = "/banks/v1";
        HttpHeaders defaultHeaders = new HttpHeaders();
        defaultHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        defaultHeaders.add("lean-app-token", appToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(defaultHeaders);
        template.exchange(urlPath, HttpMethod.GET, requestEntity, String.class).getBody();

    }
}
