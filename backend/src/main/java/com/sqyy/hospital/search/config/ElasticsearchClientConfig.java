package com.sqyy.hospital.search.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

@Configuration
public class ElasticsearchClientConfig {

    @Bean(destroyMethod = "close")
    @ConditionalOnProperty(prefix = "app.search.elasticsearch", name = "enabled", havingValue = "true")
    public RestClient restClient(SearchProperties properties) throws Exception {
        SearchProperties.Elasticsearch elasticsearch = properties.getElasticsearch();
        String uri = elasticsearch.getUri();
        String caCertPath = elasticsearch.getCaCertPath();

        RestClientBuilder builder = RestClient.builder(HttpHost.create(uri));

        builder.setHttpClientConfigCallback((HttpAsyncClientBuilder httpClientBuilder) -> {
            HttpAsyncClientBuilder config = httpClientBuilder;

            if (elasticsearch.getUsername() != null && !elasticsearch.getUsername().isBlank()) {
                BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(
                        new AuthScope(null, -1),
                        new UsernamePasswordCredentials(elasticsearch.getUsername(),
                                elasticsearch.getPassword() != null ? elasticsearch.getPassword() : ""));
                config = config.setDefaultCredentialsProvider(credentialsProvider);
            }

            if (uri.startsWith("https://") && caCertPath != null && !caCertPath.isBlank()) {
                try {
                    config = config.setSSLContext(buildSslContext(caCertPath));
                } catch (Exception e) {
                    throw new RuntimeException("Failed to build SSL context for Elasticsearch", e);
                }
            }

            return config;
        });

        return builder.build();
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.search.elasticsearch", name = "enabled", havingValue = "true")
    public ElasticsearchTransport elasticsearchTransport(RestClient restClient) {
        return new RestClientTransport(restClient, new JacksonJsonpMapper());
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.search.elasticsearch", name = "enabled", havingValue = "true")
    public ElasticsearchClient elasticsearchClient(ElasticsearchTransport transport) {
        return new ElasticsearchClient(transport);
    }

    private SSLContext buildSslContext(String caCertPath) throws Exception {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        X509Certificate caCertificate;
        try (FileInputStream inputStream = new FileInputStream(Path.of(caCertPath).toFile())) {
            caCertificate = (X509Certificate) factory.generateCertificate(inputStream);
        }

        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);
        trustStore.setCertificateEntry("es-ca", caCertificate);

        return SSLContexts.custom()
                .loadTrustMaterial(trustStore, null)
                .build();
    }
}
