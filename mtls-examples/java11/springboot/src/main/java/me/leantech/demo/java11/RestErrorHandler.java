package me.leantech.demo.java11;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

@Slf4j
@Component
// Class to handle http client error
class RestErrorHandler extends DefaultResponseErrorHandler {

    @Autowired
    private ObjectMapper mapper;

    @Override
    public void handleError(@NonNull ClientHttpResponse response, @NonNull HttpStatus statusCode) throws IOException {
        log.warn("Server responded with error");
        if (statusCode.series() == HttpStatus.Series.CLIENT_ERROR) {
            log.info("Client error happened");
            try {
                mapper.readValue(getResponseBody(response), Object.class);
            } catch (Exception ex) {
                log.warn("Could not deserialize body", ex);
            }
        } else if (statusCode.series() == HttpStatus.Series.SERVER_ERROR) {
            log.info("Server error happened");
        }
        super.handleError(response, statusCode);
    }
}
