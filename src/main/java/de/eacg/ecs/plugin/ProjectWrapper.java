package de.eacg.ecs.plugin;

import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.core.report.ResolveReport;
import org.apache.tools.ant.Project;

/**
 * Wraps an Ant-project and provides the information needed by the plugin.
 */
public class ProjectWrapper {
    private static final String IVY_REPORT_KEY = "ivy.resolved.report";

    private Project project;
    private ResolveReport ivyReport;

    /**
     * Sets up the ProjectWrapper.
     *
     * @param project Project to wrap
     */
    public ProjectWrapper(Project project) {
        this.project = project;
    }

    /**
     * Gets the Ivy-report from the project. Lazy-getter.
     *
     * @return Ivy-report
     */
    public ResolveReport getIvyReport() {
        if (ivyReport == null) {
            ivyReport = project.getReference(IVY_REPORT_KEY);
        }
        return ivyReport;
    }

    /**
     * Gets the Ivy-module for the project, if it exists.
     *
     * @return Ivy-module
     */
    public ModuleDescriptor getIvyModule() {
        return getIvyReport() != null ? getIvyReport().getModuleDescriptor() : null;
    }

    /**
     * Gets the Ivy-ModuleRevisionId for the project, if it exists.
     *
     * @return Ivy-ModuleRevisionId
     */
    public ModuleRevisionId getIvyModuleRevisionId() {
        return getIvyModule() != null ? getIvyModule().getModuleRevisionId() : null;
    }

    /**
     * Gets the first accessible name from the project information or the Ivy-module.
     *
     * @return project name
     */
    public String getName() {
        return project.getName() != null ? project.getName() : (getIvyModuleRevisionId() != null ? getIvyModuleRevisionId().getName() : null);
    }

    /**
     * Gets the organisation from the Ivy-module.
     *
     * @return organisation
     */
    public String getOrganisation() {
        return getIvyModuleRevisionId() != null ? getIvyModuleRevisionId().getOrganisation() : null;
    }

    /**
     * Gets the revision from the Ivy-module.
     *
     * @return revision
     */
    public String getRevision() {
        return getIvyModuleRevisionId() != null ? getIvyModuleRevisionId().getRevision() : null;
    }

    /**
     * Gets the first accessible description from the project information or the Ivy-module.
     *
     * @return project description
     */
    public String getDescription() {
        return project.getDescription() != null ? project.getDescription() : (getIvyModule() != null ? getIvyModule().getDescription() : null);
    }
}
