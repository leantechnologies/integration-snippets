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

    // p12 file name, can be generated using the below command
    // openssl pkcs12 -export -in certs/cert.crt  -inkey certs/key.pem  -certfile certs/ca.pem -out lean.p12
    private final String p12FileName = "lean.p12";
    // password provided while creating the p12 file
    private final char[] p12FilePassword = "123456".toCharArray();

    @Override
    @SneakyThrows
    public void customize(RestTemplate restTemplate) {
        // creating SSL context and passing in the p12 cert store and password
        var sslContext =
                SSLContextBuilder.create().loadTrustMaterial(TrustAllStrategy.INSTANCE)
                        .loadKeyMaterial(new ClassPathResource(p12FileName).getFile(), p12FilePassword, p12FilePassword)
                        .build();
        // creating a http client that uses our sslContext
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setSSLContext(sslContext)
                .build();
        // creating a requestFactory from our http client
        ClientHttpRequestFactory clientHttpRequestFactory = new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
        // setting our restTemplate to use this specific request factory
        restTemplate.setRequestFactory(clientHttpRequestFactory);
    }


}
