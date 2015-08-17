package com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.query;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Binder;
import org.skife.jdbi.v2.sqlobject.BindingAnnotation;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.epam.jenkins.deployment.sphere.plugin.metadata.model.ApplicationMetaData;

@RegisterMapper(ApplicationQuery.Mapper.class)
public interface ApplicationQuery {

    @SqlUpdate("MERGE INTO APPLICATIONS (applicationName) values (:applicationName)")
    int save(@BindApplication ApplicationMetaData applicationMetaData);

    @SqlQuery("SELECT * FROM APPLICATIONS ORDER BY applicationName")
    List<ApplicationMetaData> findAll();

    @BindingAnnotation(BindApplication.BinderFactory.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    @interface BindApplication {
        class BinderFactory implements org.skife.jdbi.v2.sqlobject.BinderFactory {
            public Binder build(Annotation annotation) {
                return new Binder<BindApplication, ApplicationMetaData>() {
                    @Override
                    public void bind(final SQLStatement<?> q, final BindApplication bind, final ApplicationMetaData arg) {
                        q.bind("applicationName", arg.getApplicationName());
                    }
                };
            }
        }
    }

    class Mapper implements ResultSetMapper<ApplicationMetaData> {
        @Override
        public ApplicationMetaData map(final int index, final ResultSet resultSet, final StatementContext ctx) throws SQLException {
            ApplicationMetaData build = new ApplicationMetaData();
            build.setApplicationName(resultSet.getString("applicationName"));
            return build;
        }
    }
}
