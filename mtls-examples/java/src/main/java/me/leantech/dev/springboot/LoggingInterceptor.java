package me.leantech.dev.springboot;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
// Class used to log requests and responses to output
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] requestBody, ClientHttpRequestExecution execution) throws IOException {
        log.info("Request body: {}", new String(requestBody, StandardCharsets.UTF_8));
        ClientHttpResponse response = execution.execute(request, requestBody);
        // You must use BufferingClientHttpRequestFactory in rest template to be able to re-read response body as stream can be consumed only once.
        String bodyString = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
        log.info("Response body: {}", bodyString);
        return response;
    }

}
