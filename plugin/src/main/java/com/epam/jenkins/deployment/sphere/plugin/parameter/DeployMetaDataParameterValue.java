package com.epam.jenkins.deployment.sphere.plugin.parameter;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.export.Exported;
import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.ParameterValue;
import hudson.util.VariableResolver;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.epam.jenkins.deployment.sphere.plugin.metadata.Constants;

@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = false)
public class DeployMetaDataParameterValue extends ParameterValue {
    @Exported(visibility = 4)
    private String environmentKey;
    private String buildVersion;
    private String applicationName;

    @DataBoundConstructor
    public DeployMetaDataParameterValue(String name, String environmentKey, String buildVersion, String applicationName) {
        this(name, environmentKey, buildVersion, applicationName, null);
    }

    public DeployMetaDataParameterValue(String name, String environmentKey, String buildVersion, String applicationName, String description) {
        super(name, description);
        this.environmentKey = environmentKey;
        this.buildVersion = buildVersion;
        this.applicationName = applicationName;
    }

    @Override
    public void buildEnvVars(AbstractBuild<?, ?> build, EnvVars env) {
        env.put(Constants.ENV_NAME, environmentKey);
        env.put(Constants.BUILD_VERSION, buildVersion);
        env.put(Constants.BUILD_APP_NAME, applicationName);
    }

    @Override
    public VariableResolver<String> createVariableResolver(
            AbstractBuild<?, ?> build) {
        return new VariableResolver<String>() {
            public String resolve(String name) {
                return resolveVariableValue(name);
            }
        };
    }

    @Exported
    public String getEnvironmentKey() {
        return environmentKey;
    }

    @Exported
    public String getBuildVersion() {
        return buildVersion;
    }

    @Exported
    public String getApplicationName() {
        return applicationName;
    }

    private String resolveVariableValue(String property) {
        if (Constants.ENV_NAME.equals(property))
            return environmentKey;
        if (Constants.BUILD_VERSION.equals(property))
            return buildVersion;
        if (Constants.BUILD_APP_NAME.equals(property))
            return applicationName;
        return null;
    }

}