package me.lean.tech.dev.apacheclient;

import nl.altindag.ssl.util.CertificateUtils;
import nl.altindag.ssl.util.PemUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.util.List;
import java.util.Properties;

public class MtlsUsingApacheClient {

    //TODO: change this passsword
    private static final String KEY_PASSWORD = "changeit";

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        Properties props = new Properties();
        props.load(MtlsUsingApacheClient.class.getClassLoader().getResourceAsStream("application.properties"));

        String certificatePath = props.getProperty("certificates.path");
        String crtCertificateName = props.getProperty("certificate.name");
        String privateKeyName = props.getProperty("private.key.name");
        String publicKeyName = props.getProperty("public.key.name");
        Path crtCertificatePath = Paths.get(certificatePath, crtCertificateName);
        Path privateKeyPath = Paths.get(certificatePath, privateKeyName);
        Path publicKeyPath = Paths.get(certificatePath, publicKeyName);

        KeyStore keyStore = createKeyStoreUsingHelperLib(crtCertificatePath, privateKeyPath, publicKeyPath);
        SSLContext sslContext = buildSSLContextFromKeystore(keyStore);

        callUsingApacheClient(sslContext, props);
    }

    private static KeyStore createKeyStoreUsingHelperLib(Path clientCrtCertificate, Path privateKeyPem, Path publicKeyPem) throws GeneralSecurityException, IOException {
        List<Certificate> certificates = CertificateUtils.loadCertificate(clientCrtCertificate, publicKeyPem);
        PrivateKey privateKey = PemUtils.loadPrivateKey(privateKeyPem);

        KeyStore keyStore = KeyStore.getInstance("pkcs12");
        keyStore.load(null, null);
        keyStore.setKeyEntry("client-key", privateKey, KEY_PASSWORD.toCharArray(), certificates.toArray(new Certificate[3]));

        return keyStore;
    }

    private static void callUsingApacheClient(SSLContext sslContext, Properties props) throws IOException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create().setSSLContext(sslContext);

        try (CloseableHttpClient httpClient = httpClientBuilder.build()) {
            String baseUrl = props.getProperty("base.url");
            String uri = baseUrl + "/banks/v1/";
            HttpGet request = new HttpGet(uri);
            // add request headers
            request.addHeader("lean-app-token", props.getProperty("app.token"));
            request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                // Get HttpResponse Status
                System.out.println(response.getStatusLine().toString()); // HTTP/1.1 200 OK
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // return it as a String
                    String result = EntityUtils.toString(entity);
                    System.out.println(result);
                } else {
                    System.out.println("Unexpected error: unable to retrieve response");
                }
            }
        }
    }

    private static SSLContext buildSSLContextFromKeystore(KeyStore keyStore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        return SSLContextBuilder.create()
                .loadKeyMaterial(keyStore, KEY_PASSWORD.toCharArray())
                .loadTrustMaterial(new TrustAllStrategy())
                .setProtocol("TLSv1.3")
                .build();
    }
}
