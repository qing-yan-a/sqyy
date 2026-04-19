package com.sqyy.hospital.search.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.search")
public class SearchProperties {

    private String engine = "elasticsearch";
    private boolean autoSyncOnStartup = true;
    private final Elasticsearch elasticsearch = new Elasticsearch();

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public boolean isAutoSyncOnStartup() {
        return autoSyncOnStartup;
    }

    public void setAutoSyncOnStartup(boolean autoSyncOnStartup) {
        this.autoSyncOnStartup = autoSyncOnStartup;
    }

    public Elasticsearch getElasticsearch() {
        return elasticsearch;
    }

    public static class Elasticsearch {
        private boolean enabled = true;
        private String indexPrefix = "community-hospital";
        private String uri = "https://localhost:9200";
        private String username = "elastic";
        private String password = "";
        private String caCertPath = "";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getIndexPrefix() {
            return indexPrefix;
        }

        public void setIndexPrefix(String indexPrefix) {
            this.indexPrefix = indexPrefix;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getCaCertPath() {
            return caCertPath;
        }

        public void setCaCertPath(String caCertPath) {
            this.caCertPath = caCertPath;
        }
    }
}
