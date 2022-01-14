package me.lean.tech.dev;

import nl.altindag.sslcontext.SSLFactory;
import nl.altindag.sslcontext.util.PemUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.core.io.ClassPathResource;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Properties;

public class Mtls {

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
     *
     */

    public static void main(String[] args) throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, UnrecoverableKeyException {
        Properties props = new Properties();
        props.load(Mtls.class.getClassLoader().getResourceAsStream("application.properties"));
        // creating SSL context and passing in the p12 cert store and password
        SSLContext sslContext =
                SSLContextBuilder.create().loadTrustMaterial(TrustAllStrategy.INSTANCE)
                        .loadKeyMaterial(new ClassPathResource(props.getProperty("p12.filename")).getFile(),
                                props.getProperty("p12.passowrd").toCharArray(),
                                props.getProperty("p12.passowrd").toCharArray())
                        .build();
        try (CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build()) {
            HttpGet request = new HttpGet("https://api.leantech.me/banks/v1/");
            // add request headers
            request.addHeader("lean-app-token", props.getProperty("p12.filename"));
            request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                // Get HttpResponse Status
                System.out.println(response.getStatusLine().toString()); // HTTP/1.1 200 OK
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // return it as a String
                    String result = EntityUtils.toString(entity);
                    System.out.println(result);
                }
            }
        }
    }

}
