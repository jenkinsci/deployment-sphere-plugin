package com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.dao;

import static java.lang.String.format;

import java.util.List;
import javax.annotation.Nullable;

import org.skife.jdbi.v2.Handle;
import lombok.extern.java.Log;

import com.epam.jenkins.deployment.sphere.plugin.metadata.model.DeploymentMetaData;
import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.domain.Build;
import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.domain.Deployment;
import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.domain.Environment;
import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.query.DeploymentQuery;
import com.epam.jenkins.deployment.sphere.plugin.utils.DateFormatUtil;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

@Log
public class DeploymentMetaDataDao extends GenericDao {

    public void save(final DeploymentMetaData deploymentMetaData) {
        Build build = new Build();
        build.setApplicationName(deploymentMetaData.getApplicationName());
        build.setBuildVersion(deploymentMetaData.getBuildVersion());

        Environment environment = new Environment();
        environment.setKey(deploymentMetaData.getEnvironmentKey());

        Deployment deployment = getModelMapper().map(deploymentMetaData, Deployment.class);
        deployment.setBuild(build);
        deployment.setEnvironment(environment);
        deployment.setDeployedAt(DateFormatUtil.toDate(deploymentMetaData.getDeployedAt()));

        try (Handle handle = database().open()) {
            DeploymentQuery query = handle.attach(DeploymentQuery.class);
            query.save(deployment);
            log.fine(format("Deployment '%s' was saved", deployment));
        }
    }

    public DeploymentMetaData find(final String applicationName, final String buildVersion, final String environmentKey) {
        try (Handle handle = database().open()) {
            DeploymentQuery query = handle.attach(DeploymentQuery.class);
            Deployment deployment = query.find(applicationName, buildVersion, environmentKey);
            log.fine(format("Here is a deployment found '%s'", deployment));
            return getModelMapper().map(deployment, DeploymentMetaData.class);
        }
    }

    public List<DeploymentMetaData> find(final String applicationName, final String environmentKey) {
        try (Handle handle = database().open()) {
            DeploymentQuery query = handle.attach(DeploymentQuery.class);
            List<Deployment> deployments = query.find(applicationName, environmentKey);
            log.fine(format("Here is a deployment list found '%s'", deployments));
            return Lists.transform(deployments, new Function<Deployment, DeploymentMetaData>() {
                @Nullable
                @Override
                public DeploymentMetaData apply(final Deployment input) {
                    return getModelMapper().map(input, DeploymentMetaData.class);
                }
            });
        }
    }

}
