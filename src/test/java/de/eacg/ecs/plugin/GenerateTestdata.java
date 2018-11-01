package de.eacg.ecs.plugin;

import org.apache.ivy.core.module.descriptor.Artifact;
import org.apache.ivy.core.module.descriptor.DefaultModuleDescriptor;
import org.apache.ivy.core.module.descriptor.License;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.core.report.ResolveReport;
import org.apache.ivy.core.resolve.IvyNode;
import org.apache.ivy.core.resolve.ResolveData;
import org.apache.ivy.core.resolve.ResolveEngine;
import org.apache.tools.ant.Project;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Contains static methods to generate testdata.
 */
public final class GenerateTestdata {
    public static final String IVY_REPORT_KEY = "ivy.resolved.report";
    private static final Date STATIC_DATE = new Date();
    private static final ResolveData MOCK_RESOLVE_DATA = new ResolveData(new ResolveEngine(null, null, null), null);

    private GenerateTestdata() {
    }

    public static Project generateProject(boolean withIvyReport, int amountDependencies) {
        return fillProject(new Project(), withIvyReport, amountDependencies);
    }

    public static Project fillProject(Project project, boolean withIvyReport, int amountDependencies) {
        project.setName("my-project-ant");
        project.setDescription("This is the best ant project ever");
        if (withIvyReport) {
            project.addReference(IVY_REPORT_KEY, generateIvyReport(amountDependencies));
        }
        return project;
    }

    public static ResolveReport generateIvyReport(int amountDependencies) {
        ResolveReport report = new ResolveReport(generateProjectDescriptor(2));
        report.setDependencies(generateDependencies(amountDependencies), (Artifact artifact) -> true);
        return report;
    }

    private static ModuleDescriptor generateProjectDescriptor(int amountLicenses) {
        DefaultModuleDescriptor md = new DefaultModuleDescriptor(ModuleRevisionId.newInstance("my.group.ivy", "my-project-ivy", "5.0.0"), "", STATIC_DATE);
        md.setDescription("This is the ivy description");
        md.setHomePage("http://example.com");
        for (License l : generateLicenses(amountLicenses)) {
            md.addLicense(l);
        }
        return md;
    }

    public static List<License> generateLicenses(int amount) {
        List<License> result = new LinkedList<>();
        for (int i = 0; i < amount; i++) {
            result.add(generateLicense(i));
        }
        return result;
    }

    public static License generateLicense(int i) {
        return new License("MIT" + i, "http://example.com/mit" + i);
    }

    public static ModuleDescriptor generateModuleDescriptor(int amountLicenses, int i) {
        DefaultModuleDescriptor md = new DefaultModuleDescriptor(ModuleRevisionId.newInstance("my.group" + i, "my-artifact" + i, "2.0." + i), "", STATIC_DATE);
        md.setDescription("This is the ivy description" + i);
        md.setHomePage("http://example.com" + i);
        for (License l : generateLicenses(amountLicenses)) {
            md.addLicense(l);
        }
        return md;
    }

    public static List<IvyNode> generateDependencies(int amount) {
        List<IvyNode> result = new LinkedList<>();

        IvyNode projectNode = new IvyNode(MOCK_RESOLVE_DATA, generateProjectDescriptor(2));

        result.add(projectNode);
        for (int i = 0; i < amount; i++) {
            IvyNode depNode = generateDependency(i);
            depNode.addCaller("", projectNode, "", "", new String[0], null);
            result.add(depNode);
        }
        return result;
    }

    public static IvyNode generateDependency(int i) {
        IvyNode result = new IvyNode(MOCK_RESOLVE_DATA, generateModuleDescriptor(1, i));
        return result;
    }

    public static PluginConfig generateConfig(boolean useExternalCredentials) {
        return fillConfig(new PluginConfig(), useExternalCredentials);
    }

    public static PluginConfig fillConfig(PluginConfig config, boolean useExternalCredentials) {
        config.setProjectName("My Project");
        if (useExternalCredentials) {
            config.setCredentials("~/ecs-credentials.json");
        }
        else {
            config.setUserName("test@example.com");
            config.setApiKey("1234");
        }
        return config;

    }

    public static EcsDependencyScanTask fillConfig(EcsDependencyScanTask task, boolean useExternalCredentials) {
        PluginConfig config = generateConfig(useExternalCredentials);

        task.setProjectName(config.getProjectName());
        task.setCredentials(config.getCredentials());
        task.setApiKey(config.getApiKey());
        task.setApiPath(config.getApiPath());
        task.setBaseUrl(config.getBaseUrl());
        task.setSkip(config.isSkip());
        task.setSkipTransfer(config.isSkipTransfer());
        task.setUserName(config.getUserName());
        task.setVerbose(config.isVerbose());
        task.setProxyPass(config.getProxyPass());
        task.setProxyPort(config.getProxyPort());
        task.setProxyUrl(config.getProxyUrl());
        task.setProxyUser(config.getProxyUser());
        return task;
    }
}
