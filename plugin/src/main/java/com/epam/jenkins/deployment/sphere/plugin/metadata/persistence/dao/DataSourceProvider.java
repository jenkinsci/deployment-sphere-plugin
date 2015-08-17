package com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.dao;

import java.io.File;
import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import jenkins.model.Jenkins;

import com.epam.jenkins.deployment.sphere.plugin.metadata.Constants;
import com.google.inject.Provider;

public class DataSourceProvider implements Provider<DataSource> {

    public DataSource get() {
        return JdbcConnectionPool
                .create("jdbc:h2:" + Jenkins.getInstance().getPluginManager().rootDir.getAbsolutePath() +
                        File.separator + ".." + File.separator + Constants.PLUGIN_DB_FILE_NAME, "username", "password");
    }

}
