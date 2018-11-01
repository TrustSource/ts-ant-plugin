package de.eacg.ecs.plugin;

import de.eacg.ecs.client.Dependency;
import de.eacg.ecs.client.License;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.core.resolve.IvyNode;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Unit-test for DependencyTreeBuilder.
 */
public class DependencyTreeBuilderTest {
    private DependencyTreeBuilder instance;
    private Project project;
    private ProjectWrapper projectWrapper;

    @Before
    public void before() {
        project = new Project();
        projectWrapper = new ProjectWrapper(project);
        instance = new DependencyTreeBuilder(projectWrapper);
    }

    @Test(expected = BuildException.class)
    public void testBuildNoIvyReport() {
        GenerateTestdata.fillProject(project, false, 0);
        instance.build();
    }

    @Test
    public void testBuildWithoutDependencies() {
        GenerateTestdata.fillProject(project, true, 0);
        Dependency result = instance.build();

        Assert.assertEquals(String.format("mvn:%s:%s", projectWrapper.getOrganisation(), projectWrapper.getName()), result.getKey());
        Assert.assertEquals(projectWrapper.getName(), result.getName());
        Assert.assertEquals(projectWrapper.getDescription(), result.getDescription());
        Assert.assertEquals(1, result.getVersions().size());
        Assert.assertTrue(result.getVersions().contains(projectWrapper.getRevision()));
        Assert.assertEquals(projectWrapper.getIvyModule().getHomePage(), result.getHomepageUrl());
        Assert.assertNull(result.getRepoUrl());
        Assert.assertEquals(2, result.getLicenses().size());
        Assert.assertEquals(0, result.getDependencies().size());
    }

    @Test
    public void testBuildWithDependencies() {
        GenerateTestdata.fillProject(project, true, 5);
        Dependency result = instance.build();
        Assert.assertEquals(5, result.getDependencies().size());

        for (Dependency d : result.getDependencies()) {
            assertDependency(d);
        }
    }

    private void assertDependency(Dependency actual) {
        int i = Integer.parseInt(actual.getName().substring(actual.getName().length() - 1));
        IvyNode expected = GenerateTestdata.generateDependency(i);
        ModuleRevisionId mriExpected = expected.getId();

        Assert.assertEquals(String.format("mvn:%s:%s", mriExpected.getOrganisation(), mriExpected.getName()), actual.getKey());
        Assert.assertEquals(mriExpected.getName(), actual.getName());
        Assert.assertEquals(1, actual.getVersions().size());
        Assert.assertTrue(actual.getVersions().contains(mriExpected.getRevision()));

        ModuleDescriptor mdExpected = expected.getDescriptor();
        if (mdExpected != null) {
            Assert.assertEquals(mdExpected.getDescription(), actual.getDescription());
            Assert.assertEquals(mdExpected.getHomePage(), actual.getHomepageUrl());
            Assert.assertNull(actual.getRepoUrl());
            Assert.assertEquals(mdExpected.getLicenses().length, actual.getLicenses().size());
            Assert.assertEquals(1, actual.getLicenses().size());
            License l = actual.getLicenses().iterator().next();
            Assert.assertEquals(mdExpected.getLicenses()[0].getName(), l.getName());
            Assert.assertEquals(mdExpected.getLicenses()[0].getUrl(), l.getUrl());
        } else {
            Assert.assertNull(actual.getDescription());
            Assert.assertNull(actual.getHomepageUrl());
            Assert.assertNull(actual.getRepoUrl());
            Assert.assertEquals(0, actual.getLicenses().size());
        }
        Assert.assertEquals(0, actual.getDependencies().size());
    }

    @Test
    public void testSkipDoubleEntries() {
        IvyNode dependency = GenerateTestdata.generateDependency(0);

        // skip double entries
        List<Dependency> result = instance.mapDependencies(Arrays.asList(dependency, dependency), 1);
        Assert.assertEquals(1, result.size());

        // allow double entries
        instance = new DependencyTreeBuilder(projectWrapper, 0, false);
        result = instance.mapDependencies(Arrays.asList(dependency, dependency), 1);
        Assert.assertEquals(2, result.size());
    }
}
