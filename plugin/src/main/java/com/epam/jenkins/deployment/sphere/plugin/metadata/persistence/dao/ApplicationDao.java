package com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.dao;

import static java.lang.String.format;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.skife.jdbi.v2.Handle;
import lombok.extern.java.Log;

import com.epam.jenkins.deployment.sphere.plugin.metadata.model.ApplicationMetaData;
import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.query.ApplicationQuery;
import com.google.common.collect.Lists;

@Log
public class ApplicationDao extends GenericDao {
    public void save(final ApplicationMetaData applicationMetaData) {
        try (Handle handle = database().open()) {

            ApplicationQuery query = handle.attach(ApplicationQuery.class);
            query.save(applicationMetaData);
            log.fine(format("Application '%s' was saved", applicationMetaData));
        }
    }

    public void saveAll(Iterable<ApplicationMetaData> applications) {
        for (ApplicationMetaData applicationMetaData : applications) {
            if (StringUtils.isNotEmpty(applicationMetaData.getApplicationName())) {
                save(applicationMetaData);
            }
        }
    }

    public Collection<ApplicationMetaData> findAll() {
        List<ApplicationMetaData> applicationMetaDataList = Lists.newArrayList();
        try (Handle handle = database().open()) {
            ApplicationQuery query = handle.attach(ApplicationQuery.class);
            List<ApplicationMetaData> dataList = query.findAll();
            applicationMetaDataList.addAll(dataList);
            log.fine(format("There are application in database '%s'", applicationMetaDataList.size()));
        }
        return applicationMetaDataList;
    }

}
