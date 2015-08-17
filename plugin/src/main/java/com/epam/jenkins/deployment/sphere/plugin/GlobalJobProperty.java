package com.epam.jenkins.deployment.sphere.plugin;

import java.util.Collection;
import java.util.List;
import javax.inject.Inject;

import org.kohsuke.stapler.StaplerRequest;
import net.sf.json.JSONObject;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;

import com.epam.jenkins.deployment.sphere.plugin.config.Application;
import com.epam.jenkins.deployment.sphere.plugin.config.Environment;
import com.epam.jenkins.deployment.sphere.plugin.metadata.model.ApplicationMetaData;
import com.epam.jenkins.deployment.sphere.plugin.metadata.model.EnvironmentMetaData;
import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.dao.ApplicationDao;
import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.dao.EnvironmentDao;
import com.google.common.base.Converter;
import com.google.common.collect.Lists;

public class GlobalJobProperty extends JobProperty<AbstractProject<?, ?>> {

    @Extension
    public static final class CustomViewsTabBarDescriptor extends JobPropertyDescriptor {

        private static final String DESCRIPTOR_DISPLAY_NAME = "Deployment Sphere";

        @Inject
        private EnvironmentDao environmentDao;

        @Inject
        private ApplicationDao applicationDao;

        private Converter<Application, ApplicationMetaData> applicationConverter = new Converter<Application, ApplicationMetaData>() {
            @Override
            protected ApplicationMetaData doForward(Application application) {
                return new ApplicationMetaData(application.getApplicationName());
            }

            @Override
            protected Application doBackward(ApplicationMetaData metaData) {
                return new Application(metaData.getApplicationName());
            }

        };

        private Converter<Environment, EnvironmentMetaData> environmentConverter = new Converter<Environment, EnvironmentMetaData>() {
            @Override
            protected EnvironmentMetaData doForward(Environment environment) {
                return new EnvironmentMetaData(environment.getTitle(), environment.getTitle());
            }

            @Override
            protected Environment doBackward(EnvironmentMetaData metaData) {
                return new Environment(metaData.getKey());
            }
        };

        public CustomViewsTabBarDescriptor() {
            super(GlobalJobProperty.class);
            PluginInjector.injectMembers(this);
            load();
        }


        public List<Application> getApplications() {
            Collection<ApplicationMetaData> metaDatas = applicationDao.findAll();
            return Lists.newArrayList(applicationConverter.reverse().convertAll(metaDatas));
        }


        public List<Environment> getEnvironments() {
            Collection<EnvironmentMetaData> metaDatas = environmentDao.findAll();
            return Lists.newArrayList(environmentConverter.reverse().convertAll(metaDatas));
        }

        @Override
        public String getDisplayName() {
            return DESCRIPTOR_DISPLAY_NAME;
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            List<Application> applications = req.bindJSONToList(Application.class, formData.get("applications"));
            applicationDao.saveAll(applicationConverter.convertAll(applications));

            List<Environment> environments = req.bindJSONToList(Environment.class, formData.get("environments"));
            environmentDao.saveAll(environmentConverter.convertAll(environments));

            save();
            return false;
        }
    }
}
