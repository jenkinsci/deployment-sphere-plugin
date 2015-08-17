package com.epam.jenkins.deployment.sphere.plugin.parameter;

import java.util.Collection;
import javax.inject.Inject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.Exported;
import net.sf.json.JSONObject;
import hudson.Extension;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
import jenkins.model.Jenkins;
import lombok.extern.java.Log;

import com.epam.jenkins.deployment.sphere.plugin.PluginConstants;
import com.epam.jenkins.deployment.sphere.plugin.metadata.model.BuildMetaData;
import com.epam.jenkins.deployment.sphere.plugin.metadata.model.EnvironmentMetaData;
import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.dao.BuildMetaDataDao;
import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.dao.EnvironmentDao;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;

@Log
public class DeployMetaDataParameterDefinition extends ParameterDefinition {

    @Inject
    private BuildMetaDataDao buildMetaDataDao;

    @Inject
    private EnvironmentDao environmentDao;

    private String environmentKey;
    private String buildVersion;
    private String applicationName;

    public DeployMetaDataParameterDefinition(String name, String description, String applicationName) {
        this(name, description, "", "", applicationName);
    }

    @DataBoundConstructor
    public DeployMetaDataParameterDefinition(String name, String description, String environmentKey, String buildVersion, String applicationName) {
        super(name, description);
        this.environmentKey = environmentKey;
        this.buildVersion = buildVersion;
        this.applicationName = applicationName;
    }

    @Override
    public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
        DeployMetaDataParameterValue value = req.bindJSON(DeployMetaDataParameterValue.class, jo);
        return value;
    }

    @Override
    public ParameterValue createValue(StaplerRequest req) {
        return new DeployMetaDataParameterValue(getName(), this.environmentKey, this.buildVersion, this.applicationName, "Deploy meta data parameter");
    }

    public Collection<String> getBuildVersions() {
        Jenkins.getInstance().getInjector().injectMembers(this);
        Collection<BuildMetaData> builds = getBuildMetaDataDao().findByAppName(getApplicationName());
        Function<BuildMetaData, String> function = new Function<BuildMetaData, String>() {
            @Override
            public String apply(BuildMetaData data) {
                return data.getBuildVersion();
            }
        };
        return Collections2.transform(builds, function);
    }

    public Collection<String> getEnvironmentKeys() {
        Jenkins.getInstance().getInjector().injectMembers(this);
        Collection<EnvironmentMetaData> envs = getEnvironmentDao().findAll();
        Function<EnvironmentMetaData, String> function = new Function<EnvironmentMetaData, String>() {
            @Override
            public String apply(EnvironmentMetaData data) {
                return data.getTitle();
            }
        };
        return Collections2.transform(envs, function);
    }

    public BuildMetaDataDao getBuildMetaDataDao() {
        return buildMetaDataDao;
    }

    public EnvironmentDao getEnvironmentDao() {
        return environmentDao;
    }

    @Exported
    public String getApplicationName() {
        return applicationName;
    }

    @Extension
    public static final class DeployMetaDataParameterDescriptorImpl extends ParameterDescriptor {

        @Override
        public String getDisplayName() {
            return PluginConstants.DEPLOY_JOB_PARAMETER;
        }
    }
}