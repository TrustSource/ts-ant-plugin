package de.eacg.ecs.plugin;

import de.eacg.ecs.client.*;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Task to scan the Ivy-Dependencies of the project and upload them to TrustSource.
 */
public class EcsDependencyScanTask extends Task {
    private static final String EMPTY_JSON_OBJECT = "{}";
    private static final String PREFIX_PLUGIN_LOG = "trustsourceScan =>";

    private PluginConfig config;
    private ProjectWrapper projectWrapper;
    private RestClient apiClient;

    /**
     * Creates a new config.
     */
    public EcsDependencyScanTask() {
        super();
        config = new PluginConfig();
    }

    /**
     * Executes the plugin.
     */
    @Override
    public void execute() {
        if (config.isSkip()) {
            log("Skipping execution");
            return;
        }

        validateConfig();

        Dependency rootDependency = new DependencyTreeBuilder(getProjectWrapper(), 0, true, config.isVerbose()).build();

        if (config.isVerbose()) {
            printDependencies(rootDependency);
        }

        if (config.isSkipTransfer()) {
            log("Skipping transfer");
        } else {
            if (apiClient == null) apiClient = createApiClient();
            String projectId = String.format("%s:%s", getProjectWrapper().getOrganisation(), getProjectWrapper().getName());
            Scan scan = new Scan(config.getProjectName(), getProjectWrapper().getName(), projectId, rootDependency);
            transferScan(scan);
        }
    }

    protected void validateConfig() {
        if (config != null &&
                config.getProjectName() != null &&
                config.getApiPath() != null &&
                config.getBaseUrl() != null &&
                ((config.getUserName() != null &&
                        config.getApiKey() != null) ||
                        config.getCredentials() != null)) {
            return;
        }
        throw new BuildException("No valid config: There are parameters missing");
    }

    private void printDependencies(Dependency dependency) {
        printDependencies(Arrays.asList(dependency), 0);
    }

    private void printDependencies(Collection<Dependency> dependencies, int level) {
        for (Dependency d : dependencies) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < level; i++) {
                sb.append(" ");
            }
            System.out.printf("%s%s - %s", sb, d.getName(), d.getKey()).println();
            String version = !d.getVersions().isEmpty() ? d.getVersions().iterator().next() : "";
            System.out.printf("%s     %s, %s, %s", sb, d.getDescription(), version, d.getHomepageUrl()).println();
            for (License l : d.getLicenses()) {
                System.out.printf("%s     %s, %s", sb, l.getName(), l.getUrl()).println();
            }
            printDependencies(d.getDependencies(), level + 1);
        }
    }

    private RestClient createApiClient() {
        JsonProperties apiClientConfig = createApiClientConfig();
        String userAgent = String.format("%s/%s", PluginProperties.NAME, PluginProperties.VERSION);
        return new RestClient(apiClientConfig, userAgent);
    }

    protected JsonProperties createApiClientConfig() {
        JsonProperties apiConfig;
        try {
            apiConfig = config.getCredentials() != null ? new JsonProperties(config.getCredentials()) : createEmptyJsonProperties();
        } catch (IOException e) {
            throw new BuildException("Exception while evaluating user credentials", e);
        }

        if (config.getUserName() != null) apiConfig.setUserName(config.getUserName());
        if (config.getApiKey() != null) apiConfig.setApiKey(config.getApiKey());
        if (config.getBaseUrl() != null) apiConfig.setBaseUrl(config.getBaseUrl());
        if (config.getApiPath() != null) apiConfig.setApiPath(config.getApiPath());
        if (config.getProxyUrl() != null) apiConfig.setProxyUrl(config.getProxyUrl());
        if (config.getProxyPort() != null) apiConfig.setProxyPort(config.getProxyPort());
        if (config.getProxyUser() != null) apiConfig.setProxyUser(config.getProxyUser());
        if (config.getProxyPass() != null) apiConfig.setProxyPass(config.getProxyPass());

        List<String> missingConfigKeys = apiConfig.validate();
        if (!missingConfigKeys.isEmpty()) {
            String keys = String.join(", ", missingConfigKeys);
            throw new BuildException(String.format("Missing keys for api-configuration: %s", keys));
        }

        return apiConfig;
    }

    private JsonProperties createEmptyJsonProperties() throws IOException {
        return new JsonProperties(new ByteArrayInputStream(EMPTY_JSON_OBJECT.getBytes(StandardCharsets.UTF_8)));
    }

    private void transferScan(Scan scan) {
        try {
            String body = apiClient.transferScan(scan);
            log(String.format("Response: code: %d, message: %s", apiClient.getResponseStatus(), body));

            if (apiClient.getResponseStatus() != 201)
                log(String.format("Failed : HTTP error code : %d", apiClient.getResponseStatus()));
        } catch (Exception e) {
            log("Transfer failed", e, 2);
        }
    }

    private ProjectWrapper getProjectWrapper() {
        if (projectWrapper == null) {
            projectWrapper = new ProjectWrapper(getProject());
        }
        return projectWrapper;
    }

    /**
     * Logs a message and prefixes it with an identifier for the plugin.
     *
     * @param msg Message to log
     */
    @Override
    public void log(String msg) {
        super.log(String.format("%s %s", PREFIX_PLUGIN_LOG, msg));
    }

    public void setApiClient(RestClient apiClient) {
        this.apiClient = apiClient;
    }

    public void setCredentials(String credentials) {
        config.setCredentials(credentials);
    }

    public void setApiKey(String apiKey) {
        config.setApiKey(apiKey);
    }

    public void setUserName(String userName) {
        config.setUserName(userName);
    }

    public void setProjectName(String projectName) {
        config.setProjectName(projectName);
    }

    public void setSkip(boolean skip) {
        config.setSkip(skip);
    }

    public void setSkipTransfer(boolean skipTransfer) {
        config.setSkipTransfer(skipTransfer);
    }

    public void setVerbose(boolean verbose) {
        config.setVerbose(verbose);
    }

    public void setProxyUrl(String proxyUrl) {
        config.setProxyUrl(proxyUrl);
    }

    public void setProxyPort(String proxyPort) {
        config.setProxyPort(proxyPort);
    }

    public void setProxyUser(String proxyUser) {
        config.setProxyUser(proxyUser);
    }

    public void setProxyPass(String proxyPass) {
        config.setProxyPass(proxyPass);
    }

    public void setBaseUrl(String baseUrl) {
        config.setBaseUrl(baseUrl);
    }

    public void setApiPath(String apiPath) {
        config.setApiPath(apiPath);
    }
}
