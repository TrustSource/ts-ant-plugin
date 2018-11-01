package de.eacg.ecs.plugin;

import de.eacg.ecs.client.JsonProperties;
import org.apache.tools.ant.BuildException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit-test for EcsDependencyScanTask.
 */
public class EcsDependencyScanTaskTest {
    private EcsDependencyScanTask instance;

    @Before
    public void before() {
        instance = new EcsDependencyScanTask();
    }

    @Test(expected = BuildException.class)
    public void testValidateConfigFails() {
        instance.validateConfig();
    }

    @Test
    public void testValidateConfig() {
        GenerateTestdata.fillConfig(instance, true).validateConfig();
        instance.setCredentials(null);
        GenerateTestdata.fillConfig(instance, false).validateConfig();
    }

    @Test(expected = BuildException.class)
    public void testCreateApiConfigFails() {
        instance.createApiClientConfig();
    }

    @Test
    public void testCreateApiConfig() {
        PluginConfig expected = GenerateTestdata.generateConfig(false);
        JsonProperties result = GenerateTestdata.fillConfig(instance, false).createApiClientConfig();

        Assert.assertEquals(expected.getBaseUrl(), result.getProperty("baseUrl"));
        Assert.assertEquals(expected.getApiPath(), result.getProperty("apiPath"));
        Assert.assertEquals(expected.getUserName(), result.getProperty("userName"));
        Assert.assertEquals(expected.getApiKey(), result.getProperty("apiKey"));
    }
}
