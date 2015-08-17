package com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.query;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.Binder;
import org.skife.jdbi.v2.sqlobject.BindingAnnotation;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import lombok.extern.java.Log;

import com.epam.jenkins.deployment.sphere.plugin.metadata.model.MetaData;
import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.domain.Build;

@RegisterMapper(BuildQuery.Mapper.class)
public interface BuildQuery {

    @SqlQuery("SELECT * FROM BUILDS WHERE application_name = :application_name AND build_version = :build_version")
    Build find(@Bind("application_name") String applicationName, @Bind("build_version") String buildVersion);

    @SqlQuery("SELECT * FROM BUILDS WHERE build_version = :build_version")
    Build find(@Bind("build_version") String buildVersion);

    @SqlQuery("SELECT * FROM BUILDS WHERE application_name = :application_name ORDER BY built_at ASC")
    List<Build> findByApp(@Bind("application_name") String applicationName);

    @SqlUpdate("INSERT INTO BUILDS (application_name, build_version, build_number, built_at, build_url, build_metadata) values (:application_name, :build_version, :build_number, :built_at, :build_url, :build_metadata)")
    int save(@BindBuild Build build);

    @SqlQuery("SELECT * FROM BUILDS ORDER BY built_at ASC")
    List<Build> all();

    @BindingAnnotation(BindBuild.BinderFactory.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    @interface BindBuild {

        @Log
        class BinderFactory implements org.skife.jdbi.v2.sqlobject.BinderFactory {
            public Binder build(Annotation annotation) {
                return new Binder<BindBuild, Build>() {
                    @Override
                    public void bind(final SQLStatement<?> q, final BindBuild bind, final Build arg) {
                        q.bind("application_name", arg.getApplicationName());
                        q.bind("build_version", arg.getBuildVersion());
                        q.bind("build_number", arg.getBuildNumber());
                        q.bind("build_url", arg.getBuildUrl());
                        q.bind("build_metadata", toJson(arg.getMetaData()));
                        if (null != arg.getBuiltAt()) {
                            q.bind("built_at", arg.getBuiltAt().getMillis());
                        }
                    }
                };
            }

            private String toJson(MetaData metaData) {
                String jsonMetaData = "";
                try {
                    jsonMetaData = new ObjectMapper().writeValueAsString(metaData);
                } catch (IOException e) {
                    log.warning("Failed serializing MetaData to Json" + e.getMessage());
                }
                return jsonMetaData;
            }
        }
    }

    @Log
    class Mapper implements ResultSetMapper<Build> {
        @Override
        public Build map(final int index, final ResultSet resultSet, final StatementContext ctx) throws SQLException {
            Build build = new Build();
            build.setApplicationName(resultSet.getString("application_name"));
            build.setBuildVersion(resultSet.getString("build_version"));
            build.setBuildUrl(resultSet.getString("build_url"));
            build.setBuildNumber(resultSet.getLong("build_number"));
            build.setBuiltAt(new DateTime(resultSet.getLong("built_at")));
            Clob clob = resultSet.getClob("build_metadata");
            build.setMetaData(fromJson(clob));
            return build;
        }

        private MetaData fromJson(Clob clob) throws SQLException {
            String jsonString = clob.getSubString(1, (int) clob.length());
            MetaData metaData = null;
            try {
                metaData = new ObjectMapper().readValue(jsonString, MetaData.class);
            } catch (IOException e) {
                log.warning("Failed deserializing Json to MetaData" + e.getMessage());
            }
            return metaData;
        }
    }

}