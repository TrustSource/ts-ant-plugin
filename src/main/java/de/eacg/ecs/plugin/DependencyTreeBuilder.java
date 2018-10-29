package de.eacg.ecs.plugin;

import de.eacg.ecs.client.Dependency;
import org.apache.ivy.core.module.descriptor.License;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.core.resolve.IvyNode;
import org.apache.ivy.core.resolve.IvyNodeCallers;

import java.util.*;

/**
 * Takes the projectWrapper and builds the dependency tree until a given level.
 */
public class DependencyTreeBuilder {
    private static final String PREFIX_MVN = "mvn";
    private static final String DEP_KEY_FORMAT = PREFIX_MVN + ":%s:%s";

    private ProjectWrapper projectWrapper;
    private int level;
    private boolean skipDoubleEntries;
    private boolean verbose;
    private Set<ModuleRevisionId> dependencyMemory;
    private Map<ModuleRevisionId, List<IvyNode>> dependencies;

    /**
     * Initializes the DependencyTreeBuilder (level = 0, skipDoubleEntries = true, verbose = false).
     *
     * @param projectWrapper Ant-projectWrapper for which the dependency tree should be built
     */
    public DependencyTreeBuilder(ProjectWrapper projectWrapper) {
        this(projectWrapper, 0);
    }

    /**
     * Initializes the DependencyTreeBuilder (skipDoubleEntries = true, verbose = false).
     *
     * @param projectWrapper Ant-projectWrapper for which the dependency tree should be built
     * @param level          the dept of the dependency tree (0 = all dependencies)
     */
    public DependencyTreeBuilder(ProjectWrapper projectWrapper, int level) {
        this(projectWrapper, level, true);
    }

    /**
     * Initializes the DependencyTreeBuilder (verbose = false).
     *
     * @param projectWrapper    Ant-projectWrapper for which the dependency tree should be built
     * @param level             the dept of the dependency tree (0 = all dependencies)
     * @param skipDoubleEntries if true skips dependencies that are already in the tree
     */
    public DependencyTreeBuilder(ProjectWrapper projectWrapper, int level, boolean skipDoubleEntries) {
        this(projectWrapper, level, skipDoubleEntries, false);
    }

    /**
     * Initializes the DependencyTreeBuilder.
     *
     * @param projectWrapper    Ant-projectWrapper for which the dependency tree should be built
     * @param level             the dept of the dependency tree (0 = all dependencies)
     * @param skipDoubleEntries if true skips dependencies that are already in the tree
     * @param verbose           prints out additional information
     */
    public DependencyTreeBuilder(ProjectWrapper projectWrapper, int level, boolean skipDoubleEntries, boolean verbose) {
        this.projectWrapper = projectWrapper;
        this.level = level;
        this.skipDoubleEntries = skipDoubleEntries;
        this.verbose = verbose;
        this.dependencyMemory = new HashSet<>();
        this.dependencies = new HashMap<>();
    }

    /**
     * Builds the dependency tree for the given projectWrapper.
     *
     * @return root dependency of the tree
     */
    public Dependency build() {
        dependencyMemory.clear();

        for (IvyNode dependency : projectWrapper.getIvyReport().getDependencies()) {
            populateDependencyTree(dependency);
        }

        return buildRootDependency();
    }

    protected void populateDependencyTree(IvyNode dependency) {
        registerNodeIfNecessary(dependency.getId());
        for (IvyNodeCallers.Caller caller : dependency.getAllCallers()) {
            addDependency(caller.getModuleRevisionId(), dependency);
        }
    }

    private void registerNodeIfNecessary(final ModuleRevisionId moduleRevisionId) {
        if (!dependencies.containsKey(moduleRevisionId)) {
            dependencies.put(moduleRevisionId, new ArrayList<IvyNode>());
        }
    }

    private void addDependency(final ModuleRevisionId moduleRevisionId, final IvyNode dependency) {
        registerNodeIfNecessary(moduleRevisionId);
        dependencies.get(moduleRevisionId).add(dependency);
    }


    protected Dependency buildRootDependency() {
        Dependency.Builder builder = new Dependency.Builder();
        builder.setKey(String.format(DEP_KEY_FORMAT, projectWrapper.getOrganisation(), projectWrapper.getName()));
        builder.setName(projectWrapper.getName());
        if (projectWrapper.getRevision() != null) {
            builder.addVersion(projectWrapper.getRevision());
        }

        builder.setDescription(projectWrapper.getDescription());
        builder.setHomepageUrl(projectWrapper.getIvyModule().getHomePage());
        for (License l : projectWrapper.getIvyModule().getLicenses()) {
            builder.addLicense(l.getName(), l.getUrl());
        }

        List<IvyNode> dependencyList = dependencies.get(projectWrapper.getIvyModuleRevisionId());
        for (Dependency dependency : mapDependencies(dependencyList, 1)) {
            builder.addDependency(dependency);
        }

        return builder.buildDependency();
    }

    protected List<Dependency> mapDependencies(List<IvyNode> nodes, int currentLevel) {
        if (nodes == null || (level != 0 && level <= currentLevel)) {
            return Collections.emptyList();
        }

        List<Dependency> result = new LinkedList<>();
        for (IvyNode node : nodes) {
            ModuleRevisionId mri = node.getId();
            if (skipDoubleEntries && dependencyMemory.contains(mri)) {
                continue;
            }

            Dependency.Builder builder = new Dependency.Builder();
            builder.setKey(String.format(DEP_KEY_FORMAT, mri.getOrganisation(), mri.getName(), mri.getRevision()));
            builder.setName(mri.getName());
            if (mri.getRevision() != null) {
                builder.addVersion(mri.getRevision());
            }

            ModuleDescriptor nodeDesc = node.getDescriptor();
            if (nodeDesc != null) {
                builder.setDescription(nodeDesc.getDescription());
                builder.setHomepageUrl(nodeDesc.getHomePage());
                for (License l : nodeDesc.getLicenses()) {
                    builder.addLicense(l.getName(), l.getUrl());
                }
            }

            List<IvyNode> dependencyList = dependencies.get(mri);
            for (Dependency dependency : mapDependencies(dependencyList, currentLevel + 1)) {
                builder.addDependency(dependency);
            }

            dependencyMemory.add(mri);
            result.add(builder.buildDependency());
        }

        return result;
    }
}
