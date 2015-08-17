package com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.query;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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

import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.domain.Build;
import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.domain.Deployment;
import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.domain.Environment;

@RegisterMapper(DeploymentQuery.Mapper.class)
public interface DeploymentQuery {

    @SqlQuery("SELECT * FROM DEPLOYMENTS WHERE application_name = :application_name AND build_version = :build_version AND environment_key = :environment_key")
    Deployment find(@Bind("application_name") String applicationName, @Bind("build_version") String buildVersion, @Bind("environment_key") String environmentKey);

    @SqlUpdate("INSERT INTO DEPLOYMENTS (application_name, build_version, environment_key, deployed_at) values (:application_name, :build_version, :environment_key, :deployed_at)")
    int save(@BindDeployment Deployment build);

    @SqlQuery("SELECT * FROM DEPLOYMENTS")
    List<Deployment> all();

    @SqlQuery("SELECT * FROM DEPLOYMENTS WHERE application_name = :application_name AND environment_key = :environment_key ORDER BY deployed_at DESC")
    List<Deployment> find(@Bind("application_name") String applicationName, @Bind("environment_key") String environmentKey);

    @SqlQuery("SELECT * FROM DEPLOYMENTS AS data WHERE deployed_at = (SELECT MAX(deployed_at) FROM DEPLOYMENTS WHERE application_name = data.application_name AND environment_key = data.environment_key) AND environment_key = :environment_key ORDER BY deployed_at DESC")
    List<Deployment> find(@Bind("environment_key") String environmentKey);

    @BindingAnnotation(BindDeployment.BinderFactory.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    @interface BindDeployment {

        class BinderFactory implements org.skife.jdbi.v2.sqlobject.BinderFactory {
            public Binder build(Annotation annotation) {
                return new Binder<BindDeployment, Deployment>() {
                    @Override
                    public void bind(final SQLStatement<?> q, final BindDeployment bind, final Deployment arg) {
                        q.bind("application_name", arg.getBuild().getApplicationName());
                        q.bind("build_version", arg.getBuild().getBuildVersion());
                        q.bind("environment_key", arg.getEnvironment().getKey());
                        if (null != arg.getDeployedAt()) {
                            q.bind("deployed_at", arg.getDeployedAt().getMillis());
                        }
                    }
                };
            }
        }
    }

    class Mapper implements ResultSetMapper<Deployment> {
        @Override
        public Deployment map(final int index, final ResultSet resultSet, final StatementContext ctx) throws SQLException {
            Deployment deployment = new Deployment();

            Build build = new Build();
            build.setApplicationName(resultSet.getString("application_name"));
            build.setBuildVersion(resultSet.getString("build_version"));
            build.setBuiltAt(new DateTime(resultSet.getLong("deployed_at")));
            deployment.setBuild(build);

            Environment environment = new Environment();
            environment.setKey(resultSet.getString("environment_key"));
            deployment.setEnvironment(environment);

            deployment.setKey(resultSet.getString("key"));
            deployment.setDeployedAt(new DateTime(resultSet.getLong("deployed_at")));
            return deployment;
        }
    }

}
