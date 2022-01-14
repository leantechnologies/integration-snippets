package me.lean.tech.dev.springboot;

import lombok.extern.slf4j.Slf4j;
import me.lean.tech.dev.apacheclient.MtlsUsingApacheClient;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Properties;

@Slf4j
@SpringBootApplication
public class MtlsUsingRestTemplate implements CommandLineRunner {

    @Autowired
    private RestTemplate template;

    public static void main(String[] args) {
        SpringApplication.run(MtlsUsingRestTemplate.class, args);
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
        try {
            getBanks();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("Finished");
    }

    // method to call the banks endpoint
    private void getBanks() throws IOException {
        makeGetCall("/banks/v1", String.class);
    }

    // generic get method to call the bank with the correct headers and return the correct required object class
    private <T> void makeGetCall(String urlPath, Class<T> responseClass) throws IOException {
        Properties props = new Properties();
        props.load(MtlsUsingApacheClient.class.getClassLoader().getResourceAsStream("application.properties"));

        HttpHeaders defaultHeaders = new HttpHeaders();
        defaultHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        defaultHeaders.add("lean-app-token", props.getProperty("app.token"));
        HttpEntity<Void> requestEntity = new HttpEntity<>(defaultHeaders);
        template.exchange(urlPath, HttpMethod.GET, requestEntity, responseClass).getBody();
    }
}
