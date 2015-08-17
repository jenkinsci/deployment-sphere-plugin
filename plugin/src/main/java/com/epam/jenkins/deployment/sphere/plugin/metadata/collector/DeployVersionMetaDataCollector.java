package com.epam.jenkins.deployment.sphere.plugin.metadata.collector;

import static java.lang.String.format;
import static com.google.common.base.Preconditions.checkState;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

import javax.inject.Inject;

import org.joda.time.DateTime;
import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;
import lombok.extern.java.Log;

import com.epam.jenkins.deployment.sphere.plugin.metadata.Constants;
import com.epam.jenkins.deployment.sphere.plugin.metadata.model.DeploymentMetaData;
import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.dao.DeploymentMetaDataDao;
import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.dao.EnvironmentDao;
import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.domain.Environment;
import com.epam.jenkins.deployment.sphere.plugin.utils.DateFormatUtil;
import com.epam.jenkins.deployment.sphere.plugin.utils.EnvVarsResolver;

@Log
public final class DeployVersionMetaDataCollector implements Collector<DeploymentMetaData> {

    @Inject
    private DeploymentMetaDataDao deploymentMetaDataDao;

    @Inject
    private EnvironmentDao environmentDao;

    public DeployVersionMetaDataCollector() {
        Jenkins.getInstance().getInjector().injectMembers(this);
    }

    @Override
    public DeploymentMetaData collect(final AbstractBuild<?, ?> build, final TaskListener taskListener) {
        // next variables should be resolved from context
        EnvVarsResolver envVarsResolver = new EnvVarsResolver(build, taskListener);

        Environment environment = environmentDao.find(resolveEnvironmentName(envVarsResolver));

        // persist data
        DeploymentMetaData deploymentMetaData = new DeploymentMetaData();
        deploymentMetaData.setApplicationName(resolveApplicationName(envVarsResolver));
        deploymentMetaData.setDeployedAt(DateFormatUtil.formatDate(new DateTime(build.due())));
        deploymentMetaData.setBuildVersion(resolveBuildVersion(envVarsResolver));
        deploymentMetaData.setEnvironmentKey(environment.getKey());
        deploymentMetaDataDao.save(deploymentMetaData);
        return deploymentMetaData;
    }

    private String resolveEnvironmentName(final EnvVarsResolver envVarsResolver) {
        String envName = envVarsResolver.getValue(Constants.ENV_NAME);
        checkState(isNotEmpty(envName), format("Invalid env name: %s", envName));
        return envName;
    }

    private String resolveApplicationName(final EnvVarsResolver envVarsResolver) {
        String applicationName = envVarsResolver.getValue(Constants.BUILD_APP_NAME);
        checkState(isNotEmpty(applicationName), format("Invalid application name: %s", applicationName));
        return applicationName;
    }

    private String resolveBuildVersion(final EnvVarsResolver envVarsResolver) {
        String buildVersion = envVarsResolver.getValue(Constants.BUILD_VERSION);
        checkState(isNotEmpty(buildVersion), format("Invalid build version: %s", buildVersion));
        return buildVersion;
    }

}
