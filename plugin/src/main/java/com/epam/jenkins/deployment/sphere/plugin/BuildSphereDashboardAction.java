package com.epam.jenkins.deployment.sphere.plugin;

import java.util.Collection;
import javax.annotation.Nonnull;
import javax.inject.Inject;

import org.acegisecurity.AccessDeniedException;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;
import hudson.Extension;
import hudson.Functions;
import hudson.model.RootAction;
import hudson.security.ACL;
import hudson.security.AccessControlled;
import hudson.security.Permission;
import jenkins.model.Jenkins;

import com.epam.jenkins.deployment.sphere.plugin.metadata.model.ApplicationMetaData;
import com.epam.jenkins.deployment.sphere.plugin.metadata.model.BuildMetaData;
import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.dao.ApplicationDao;
import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.dao.BuildMetaDataDao;

//@ExportedBean(defaultVisibility = 999)
//@Extension
// Disabled for now as it's lightly overhead for the plugin purpose.
public class BuildSphereDashboardAction implements RootAction, AccessControlled {


    public String environment = "";
    @Inject
    private ApplicationDao applicationDao;
    @Inject
    private BuildMetaDataDao buildMetaDataDao;

    @DataBoundConstructor
    public BuildSphereDashboardAction() {
        Jenkins.getInstance().getInjector().injectMembers(this);
    }

    @Override
    public String getIconFileName() {
        return "/plugin/deployment-sphere/icons/main_logo.jpg";
    }

    @Override
    public String getDisplayName() {
        return Messages.build_plugin_title();
    }


    @Override
    public String getUrlName() {
        return "/" + PluginConstants.BUILD_PLUGIN_CONTEXT;
    }

    @Exported
    public String getResourceUrl() {
        return Functions.getResourcePath();
    }


    @Exported
    public Collection<ApplicationMetaData> getApplications() {
        return applicationDao.findAll();
    }

    @Exported
    public Collection<BuildMetaData> getBuilds() {
        return buildMetaDataDao.findAll();
    }

    @Nonnull
    @Override
    public ACL getACL() {
        return Jenkins.getInstance().getACL();
    }

    @Override
    public void checkPermission(@Nonnull final Permission permission) throws AccessDeniedException {
        getACL().checkPermission(permission);

    }

    @Override
    public boolean hasPermission(@Nonnull final Permission permission) {
        return getACL().hasPermission(permission);
    }


}
