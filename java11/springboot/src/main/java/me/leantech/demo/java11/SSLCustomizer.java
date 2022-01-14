package me.leantech.demo.java11;

import lombok.SneakyThrows;
import org.apache.http.HttpHost;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class SSLCustomizer implements RestTemplateCustomizer {

    private final String p12FileName = "lean.p12";
    private final char[] p12FilePassword = "123456".toCharArray();

    @Override
    @SneakyThrows
    public void customize(RestTemplate restTemplate) {
        var sslContext =
                SSLContextBuilder.create().loadTrustMaterial(TrustAllStrategy.INSTANCE)
                        .loadKeyMaterial(new ClassPathResource(p12FileName).getFile(), p12FilePassword, p12FilePassword)
                        .build();
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setSSLContext(sslContext)
                .build();
        ClientHttpRequestFactory clientHttpRequestFactory = new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
        restTemplate.setRequestFactory(clientHttpRequestFactory);
    }


}
