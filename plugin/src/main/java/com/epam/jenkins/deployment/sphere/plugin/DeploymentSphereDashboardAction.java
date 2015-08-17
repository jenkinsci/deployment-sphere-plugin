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

import com.epam.jenkins.deployment.sphere.plugin.metadata.model.EnvironmentMetaData;
import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.dao.EnvironmentDao;

@ExportedBean(defaultVisibility = 999)
@Extension
public class DeploymentSphereDashboardAction implements RootAction, AccessControlled {


    @Inject
    private EnvironmentDao environmentDao;


    @DataBoundConstructor
    public DeploymentSphereDashboardAction() {
        Jenkins.getInstance().getInjector().injectMembers(this);
    }

    @Override
    public String getIconFileName() {
        return "/plugin/deployment-sphere/icons/main_logo.jpg";
    }

    @Override
    public String getDisplayName() {
        return Messages.plugin_title();
    }


    @Override
    public String getUrlName() {
        return "/" + PluginConstants.PLUGIN_CONTEXT;
    }

    @Exported
    public String getResourceUrl() {
        return Functions.getResourcePath();
    }


    @Exported
    public Collection<EnvironmentMetaData> getEnvironments() {
        return environmentDao.findAll();
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
