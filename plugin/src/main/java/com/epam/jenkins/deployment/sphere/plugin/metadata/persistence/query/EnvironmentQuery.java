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
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.Binder;
import org.skife.jdbi.v2.sqlobject.BindingAnnotation;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.domain.Environment;

@RegisterMapper(EnvironmentQuery.Mapper.class)
public interface EnvironmentQuery {

    @SqlUpdate("TRUNCATE TABLE ENVIRONMENTS")
    void truncate();

    @SqlQuery("SELECT * FROM ENVIRONMENTS WHERE key = :key AND title = :title")
    Environment find(@Bind("key") String applicationName, @Bind("title") String title);

    @SqlQuery("SELECT * FROM ENVIRONMENTS WHERE title = :title")
    Environment find(@Bind("title") String title);

    @SqlUpdate("MERGE INTO ENVIRONMENTS (key, title) values (:key, :title)")
    int save(@BindEnvironment Environment build);

    @SqlQuery("SELECT * FROM ENVIRONMENTS ORDER BY key")
    List<Environment> all();

    @BindingAnnotation(BindEnvironment.BinderFactory.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    @interface BindEnvironment {

        class BinderFactory implements org.skife.jdbi.v2.sqlobject.BinderFactory {
            public Binder build(Annotation annotation) {
                return new Binder<BindEnvironment, Environment>() {
                    @Override
                    public void bind(final SQLStatement<?> q, final BindEnvironment bind, final Environment arg) {
                        q.bind("key", arg.getKey());
                        q.bind("title", arg.getTitle());
                    }
                };
            }
        }
    }

    class Mapper implements ResultSetMapper<Environment> {
        @Override
        public Environment map(final int index, final ResultSet resultSet, final StatementContext ctx) throws
                SQLException {
            Environment build = new Environment();
            build.setTitle(resultSet.getString("title"));
            build.setKey(resultSet.getString("key"));
            return build;
        }
    }
}
