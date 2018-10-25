package de.eacg.ecs.plugin;

import de.eacg.ecs.client.Dependency;
import org.apache.ivy.core.module.descriptor.License;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.report.ResolveReport;
import org.apache.tools.ant.Project;

import java.util.HashSet;
import java.util.Set;

/**
 * Takes the project and builds the dependency tree until a given level.
 */
public class DependencyTreeBuilder {
    private static final String PREFIX_MVN = "mvn";
    private static final String IVY_REPORT_KEY = "ivy.resolved.report";

    private Project project;
    private int level;
    private boolean skipDoubleEntries;
    private boolean verbose;
    private Set<String> dependencyMemory;

    /**
     * Initializes the DependencyTreeBuilder (level = 0, skipDoubleEntries = true, verbose = false).
     * @param project Ant-project for which the dependency tree should be built
     */
    public DependencyTreeBuilder(Project project) {
        this(project, 0);
    }

    /**
     * Initializes the DependencyTreeBuilder (skipDoubleEntries = true, verbose = false).
     * @param project Ant-project for which the dependency tree should be built
     * @param level the dept of the dependency tree (0 = all dependencies)
     */
    public DependencyTreeBuilder(Project project, int level) {
        this(project, level, true);
    }

    /**
     * Initializes the DependencyTreeBuilder (verbose = false).
     * @param project Ant-project for which the dependency tree should be built
     * @param level the dept of the dependency tree (0 = all dependencies)
     * @param skipDoubleEntries if true skips dependencies that are already in the tree
     */
    public DependencyTreeBuilder(Project project, int level, boolean skipDoubleEntries) {
        this(project, level, skipDoubleEntries, false);
    }

    /**
     * Initializes the DependencyTreeBuilder.
     * @param project Ant-project for which the dependency tree should be built
     * @param level the dept of the dependency tree (0 = all dependencies)
     * @param skipDoubleEntries if true skips dependencies that are already in the tree
     * @param verbose prints out additional information
     */
    public DependencyTreeBuilder(Project project, int level, boolean skipDoubleEntries, boolean verbose) {
        this.project = project;
        this.level = level;
        this.skipDoubleEntries = skipDoubleEntries;
        this.verbose = verbose;
        this.dependencyMemory = new HashSet<>();
    }

    /**
     * Builds the dependency tree for the given project.
     * @return root dependency of the tree
     */
    public Dependency build() {
        Dependency.Builder builder = new Dependency.Builder();
        ResolveReport ivyReport = project.getReference(IVY_REPORT_KEY);
        ModuleDescriptor ivyModule = ivyReport.getModuleDescriptor();

        builder.setKey(String.format("%s:%s:%s", PREFIX_MVN, ivyModule.getModuleRevisionId().getOrganisation(), ivyModule.getModuleRevisionId().getName()));
        builder.setName(project.getName() != null ? project.getName() : ivyModule.getModuleRevisionId().getName());
        if (ivyModule.getModuleRevisionId().getRevision() != null) {
            builder.addVersion(ivyModule.getModuleRevisionId().getRevision());
        }

        builder.setDescription(project.getDescription() != null ? project.getDescription() : ivyModule.getDescription());
        builder.setHomepageUrl(ivyModule.getHomePage());
        // TODO: unable to read repository url
        for (License l : ivyModule.getLicenses()) {
            builder.addLicense(l.getName(), l.getUrl());
        }

        // TODO: map sub-dependencies

        return builder.buildDependency();
    }
}
