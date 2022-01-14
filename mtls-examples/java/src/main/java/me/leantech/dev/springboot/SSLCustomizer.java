package me.leantech.dev.springboot;

import lombok.SneakyThrows;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;


public class SSLCustomizer implements RestTemplateCustomizer {

    @Value("${p12.filename}")
    private String p12FileName;
    @Value("${p12.password}")
    private String p12FilePassword;
    @Value("${certificates.path}")
    private String certificatesPath;

    @Override
    @SneakyThrows
    public void customize(RestTemplate restTemplate) {
        // creating SSL context and passing in the p12 cert store and password
        SSLContext sslContext = buildSSLContextFromPKCS12();
        // creating a http client that uses our sslContext
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setSSLContext(sslContext)
                .build();
        // creating a requestFactory from our http client
        ClientHttpRequestFactory clientHttpRequestFactory = new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
        // setting our restTemplate to use this specific request factory
        restTemplate.setRequestFactory(clientHttpRequestFactory);
    }

    /**
     * How to:
     * - Create a p12 file from the certificates
     * - Go to Lean's dev portal
     * - Select "Integration" from the left side
     * - Download the Certification Authority give it a name, ex: ca
     * - Download the public/private keys zip file by clicking on "Download New Certificate"
     * - Extract the zip file, rename the private key to key.pem, rename the public key to cert.crt
     * - Copy all the certificates in a new folder, name it /certs
     * - Run the following command, you will be asked to put on a password, please remember it
     * ```bash
     * openssl pkcs12 -export -in cert.crt  -inkey key.pem  -certfile ca.pem -out yourp12filename.p12
     * ```
     * - A new file will be generated called yourp12filename.p12, move it to the resources folder
     * - Replace the "p12.filename", "p12.password", "app.token" dummy properties with your own.
     * - Run the main class, you should receive "HTTP/1.1 200" and a valid response in the console.
     */
    private SSLContext buildSSLContextFromPKCS12() throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, CertificateException, IOException, KeyManagementException {
        File p12File = Paths.get(certificatesPath, p12FileName).toFile();

        return SSLContextBuilder.create()
                .loadTrustMaterial(TrustAllStrategy.INSTANCE)
                .loadKeyMaterial(p12File, p12FilePassword.toCharArray(), p12FilePassword.toCharArray())
                .build();
    }
}
