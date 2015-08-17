package com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.dao;

import static java.lang.String.format;

import java.util.Collection;
import java.util.List;

import org.skife.jdbi.v2.Handle;
import lombok.extern.java.Log;

import com.epam.jenkins.deployment.sphere.plugin.metadata.model.BuildMetaData;
import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.domain.Build;
import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.query.BuildQuery;
import com.epam.jenkins.deployment.sphere.plugin.utils.DateFormatUtil;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

@Log
public class BuildMetaDataDao extends GenericDao {

    public void save(final BuildMetaData buildMetaData) {
        Build mappedBuild = getModelMapper().map(buildMetaData, Build.class);
        mappedBuild.setBuiltAt(DateFormatUtil.toDate(buildMetaData.getBuiltAt()));

        try (Handle handle = database().open()) {
            BuildQuery query = handle.attach(BuildQuery.class);
            query.save(mappedBuild);
            log.fine(format("Build '%s' was saved", mappedBuild));
        }
    }

    public BuildMetaData find(final String applicationName, final String buildVersion) {
        try (Handle handle = database().open()) {
            BuildQuery query = handle.attach(BuildQuery.class);
            Build foundBuild = query.find(applicationName, buildVersion);
            log.fine(format("Here is a build found '%s' for version '%s'", foundBuild, buildVersion));
            return getModelMapper().map(foundBuild, BuildMetaData.class);
        }
    }

    public Collection<BuildMetaData> findAll() {
        try (Handle handle = database().open()) {
            BuildQuery query = handle.attach(BuildQuery.class);
            List<Build> foundBuilds = query.all();
            log.fine("Here are all builds");

            return Lists.transform(foundBuilds, new Function<Build, BuildMetaData>() {
                @Override
                public BuildMetaData apply(final Build build) {
                    return getModelMapper().map(build, BuildMetaData.class);
                }
            });
        }
    }


    public Collection<BuildMetaData> findByAppName(final String applicationName) {
        try (Handle handle = database().open()) {
            BuildQuery query = handle.attach(BuildQuery.class);
            List<Build> foundBuilds = query.findByApp(applicationName);
            log.fine(format("Here are a builds found '%s' for application '%s'", foundBuilds.size(), applicationName));
            return Lists.transform(foundBuilds, new Function<Build, BuildMetaData>() {
                @Override
                public BuildMetaData apply(final Build build) {
                    return getModelMapper().map(build, BuildMetaData.class);
                }
            });
        }
    }
}
