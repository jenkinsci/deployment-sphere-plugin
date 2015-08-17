package com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.dao;

import static java.lang.String.format;

import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.skife.jdbi.v2.Handle;
import lombok.extern.java.Log;

import com.epam.jenkins.deployment.sphere.plugin.metadata.model.BuildMetaData;
import com.epam.jenkins.deployment.sphere.plugin.metadata.model.DeploymentMetaData;
import com.epam.jenkins.deployment.sphere.plugin.metadata.model.EnvironmentMetaData;
import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.domain.Build;
import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.domain.Deployment;
import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.domain.Environment;
import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.query.BuildQuery;
import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.query.DeploymentQuery;
import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.query.EnvironmentQuery;
import com.epam.jenkins.deployment.sphere.plugin.utils.DateFormatUtil;
import com.google.common.collect.Lists;


@Log
public class EnvironmentDao extends GenericDao {

    public void deleteAll() {
        try (Handle handle = database().open()) {
            EnvironmentQuery query = handle.attach(EnvironmentQuery.class);
            query.truncate();
        }
    }

    public void save(final EnvironmentMetaData environmentMetaData) {
        Environment environment = getModelMapper().map(environmentMetaData, Environment.class);

        try (Handle handle = database().open()) {
            EnvironmentQuery query = handle.attach(EnvironmentQuery.class);
            query.save(environment);
            log.fine(format("Environment '%s' was saved", environment));
        }
    }

    public void saveAll(Iterable<EnvironmentMetaData> convertAll) {
        for (EnvironmentMetaData environmentMetaData : convertAll) {
            if (StringUtils.isNotEmpty(environmentMetaData.getKey()) && StringUtils.isNotEmpty(environmentMetaData.getTitle())) {
                save(environmentMetaData);
            }
        }
    }

    public Environment find(final String evnKey) {
        try (Handle handle = database().open()) {
            EnvironmentQuery query = handle.attach(EnvironmentQuery.class);
            return query.find(evnKey);
        }
    }

    public Collection<EnvironmentMetaData> findAll() {

        List<EnvironmentMetaData> environmentMetaDataList = Lists.newArrayList();

        try (Handle handle = database().open()) {
            EnvironmentQuery query = handle.attach(EnvironmentQuery.class);
            List<Environment> environments = query.all();
            for (Environment environment : environments) {
                EnvironmentMetaData environmentMetaData = new EnvironmentMetaData(environment.getKey(), environment.getTitle());
                loadDeployInfo(handle, environment, environmentMetaData);
                environmentMetaDataList.add(environmentMetaData);
            }
            log.fine(format("There are environments in database '%s'", environments.size()));
        }

        return environmentMetaDataList;
    }

    private void loadDeployInfo(final Handle handle, final Environment environment,
                                final EnvironmentMetaData environmentMetaData) {
        final DeploymentQuery deploymentQuery = handle.attach(DeploymentQuery.class);
        List<Deployment> deploymentList = deploymentQuery.find(environment.getKey());
        if (CollectionUtils.isNotEmpty(deploymentList)) {
            for (Deployment deployment : deploymentList) {
                final DeploymentMetaData prodDeploy = new DeploymentMetaData();
                prodDeploy.setBuildVersion(deployment.getBuild().getBuildVersion());
                prodDeploy.setDeployedAt(DateFormatUtil.formatDate(deployment.getDeployedAt()));
                prodDeploy.setApplicationName(deployment.getBuild().getApplicationName());
                prodDeploy.setBuild(loadBuildInfo(handle, deployment));
                environmentMetaData.getDeployments().add(prodDeploy);
            }
        }
    }

    private BuildMetaData loadBuildInfo(final Handle handle, final Deployment deployment) {
        final BuildQuery query = handle.attach(BuildQuery.class);
        Build foundBuild = query.find(deployment.getBuild().getApplicationName(), deployment.getBuild().getBuildVersion());
        BuildMetaData cqBuild = null;
        if (null != foundBuild) {
            cqBuild = new BuildMetaData();
            cqBuild.setApplicationName(foundBuild.getApplicationName());
            cqBuild.setNumber(foundBuild.getBuildNumber());
            cqBuild.setBuildVersion(foundBuild.getBuildVersion());
            cqBuild.setBuiltAt(DateFormatUtil.formatDate(foundBuild.getBuiltAt()));
            cqBuild.setJobName(foundBuild.getApplicationName());
            cqBuild.setBuildUrl(foundBuild.getBuildUrl());
            cqBuild.setMetaData(foundBuild.getMetaData());
        }
        return cqBuild;
    }
}
