package com.epam.jenkins.deployment.sphere.plugin;

import java.io.IOException;
import java.util.logging.Level;
import javax.inject.Inject;
import javax.servlet.ServletException;

import org.kohsuke.stapler.QueryParameter;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.YesNoMaybe;
import lombok.extern.java.Log;

import com.epam.jenkins.deployment.sphere.plugin.config.GlobalConfigHelper;
import com.google.common.base.Strings;

@Log
@Extension(dynamicLoadable = YesNoMaybe.YES)
public class DeployVersionMetaDataCollectorDescriptor extends BuildStepDescriptor<Publisher> {

    @Inject
    private GlobalConfigHelper configHelper;

    public DeployVersionMetaDataCollectorDescriptor() {
        super(DeployVersionMetaDataPublisher.class);
        load();
        PluginInjector.injectMembers(this);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean isApplicable(final Class<? extends AbstractProject> jobType) {
        log.log(Level.FINE, String.format("Current project is of type %s", jobType));
        return true;
    }

    public String getDisplayName() {
        return PluginConstants.DEPLOY_METADATA_COLLECTOR_NAME;
    }

    public FormValidation doCheckDeployedAppName(@QueryParameter String deployedAppName) throws IOException,
            ServletException {
        if (Strings.isNullOrEmpty(deployedAppName)) {
            return FormValidation.error("Please set the application name to deploy");
        }
        return FormValidation.ok();
    }

    public ListBoxModel doFillDeployedAppNameItems() {
        return configHelper.getApplications();
    }
}