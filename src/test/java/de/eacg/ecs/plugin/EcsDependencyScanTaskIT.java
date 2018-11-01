package de.eacg.ecs.plugin;

import de.eacg.ecs.client.Dependency;
import de.eacg.ecs.client.RestClient;
import de.eacg.ecs.client.Scan;
import org.apache.tools.ant.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * Integration-test for the EcsDependencyScanTask with mocked transfer.
 */
public class EcsDependencyScanTaskIT {
    private EcsDependencyScanTask instance;
    private Project project;
    private RestClient restClient;

    @Before
    public void before() {
        instance = new EcsDependencyScanTask();
        GenerateTestdata.fillConfig(instance, true);
        project = new Project();
        instance.setProject(project);
        restClient = Mockito.mock(RestClient.class);
        instance.setApiClient(restClient);
    }

    @Test
    public void testEcsDependencyScanTask() throws Exception {
        GenerateTestdata.fillProject(project, true, 3);

        instance.execute();

        ArgumentCaptor<Scan> captor = ArgumentCaptor.forClass(Scan.class);
        Mockito.verify(restClient).transferScan(captor.capture());
        Scan capturedScan = captor.getValue();

        PluginConfig expectedConfig = GenerateTestdata.generateConfig(false);
        ProjectWrapper expectedProject = new ProjectWrapper(project);

        Assert.assertEquals(expectedConfig.getProjectName(), capturedScan.getProject());
        Assert.assertEquals(expectedProject.getName(), capturedScan.getModule());
        Assert.assertEquals(String.format("%s:%s", expectedProject.getOrganisation(), expectedProject.getName()), capturedScan.getModuleId());
        Assert.assertEquals(1, capturedScan.getDependencies().size());

        Dependency rootDependency = capturedScan.getDependencies().get(0);
        Assert.assertEquals(String.format("mvn:%s:%s", expectedProject.getOrganisation(), expectedProject.getName()), rootDependency.getKey());

        Assert.assertEquals(expectedProject.getName(), rootDependency.getName());
        Assert.assertEquals(expectedProject.getDescription(), rootDependency.getDescription());
        Assert.assertEquals(1, rootDependency.getVersions().size());
        Assert.assertTrue(rootDependency.getVersions().contains(expectedProject.getRevision()));
        Assert.assertEquals(expectedProject.getIvyModule().getHomePage(), rootDependency.getHomepageUrl());
        Assert.assertNull(rootDependency.getRepoUrl());
        Assert.assertEquals(2, rootDependency.getLicenses().size());
        Assert.assertEquals(3, rootDependency.getDependencies().size());
    }
}
