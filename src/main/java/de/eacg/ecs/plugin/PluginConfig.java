package de.eacg.ecs.plugin;

/**
 * Holds config parameter for the plugin.
 */
public class PluginConfig {
    private String credentials;
    private String apiKey;
    private String userName;
    private String projectName;
    private boolean skip = false;
    private boolean skipTransfer = false;
    private boolean verbose = false;
    private String baseUrl = "https://app.trustsource.io";
    private String apiPath = "/api/v1";

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public boolean isSkipTransfer() {
        return skipTransfer;
    }

    public void setSkipTransfer(boolean skipTransfer) {
        this.skipTransfer = skipTransfer;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiPath() {
        return apiPath;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }
}
