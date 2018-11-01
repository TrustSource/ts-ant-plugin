package de.eacg.ecs.plugin;

import org.apache.tools.ant.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit-tests for ProjectWrapper.
 */
public class ProjectWrapperTest {
    private static String IVY = "ivy";
    private static String ANT = "ant";

    private Project project;
    private ProjectWrapper instance;

    @Before
    public void before() {
        project = new Project();
        instance = new ProjectWrapper(project);
    }

    @Test
    public void testWithoutIvyReport() {
        GenerateTestdata.fillProject(project, false, 0);

        Assert.assertTrue(instance.getName().contains(ANT));
        Assert.assertNull(instance.getOrganisation());
        Assert.assertTrue(instance.getDescription().contains(ANT));
        Assert.assertNull(instance.getRevision());
        Assert.assertNull(instance.getIvyReport());
        Assert.assertNull(instance.getIvyModule());
        Assert.assertNull(instance.getIvyModuleRevisionId());
    }

    @Test
    public void testWithoutAntValues() {
        project.addReference(GenerateTestdata.IVY_REPORT_KEY, GenerateTestdata.generateIvyReport(0));

        Assert.assertTrue(instance.getName().contains(IVY));
        Assert.assertNotNull(instance.getOrganisation());
        Assert.assertTrue(instance.getDescription().contains(IVY));
        Assert.assertNotNull(instance.getRevision());
        Assert.assertNotNull(instance.getIvyReport());
        Assert.assertNotNull(instance.getIvyModule());
        Assert.assertNotNull(instance.getIvyModuleRevisionId());
    }

    @Test
    public void testWithEverythingFilled() {
        GenerateTestdata.fillProject(project, true, 0);

        Assert.assertTrue(instance.getName().contains(ANT));
        Assert.assertNotNull(instance.getOrganisation());
        Assert.assertTrue(instance.getDescription().contains(ANT));
        Assert.assertNotNull(instance.getRevision());
        Assert.assertNotNull(instance.getIvyReport());
        Assert.assertNotNull(instance.getIvyModule());
        Assert.assertNotNull(instance.getIvyModuleRevisionId());
    }
}
