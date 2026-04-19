package com.sqyy.hospital.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.coze")
public class CozeProperties {

    private String pat = "";
    private String aiBotId = "";
    private String newsBotId = "";
    private String apiBaseUrl = "https://api.coze.cn";

    public String getPat() {
        return pat;
    }

    public void setPat(String pat) {
        this.pat = pat;
    }

    public String getAiBotId() {
        return aiBotId;
    }

    public void setAiBotId(String aiBotId) {
        this.aiBotId = aiBotId;
    }

    public String getNewsBotId() {
        return newsBotId;
    }

    public void setNewsBotId(String newsBotId) {
        this.newsBotId = newsBotId;
    }

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }
}
