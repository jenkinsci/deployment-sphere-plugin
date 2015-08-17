package com.epam.jenkins.deployment.sphere.plugin.config;

import org.kohsuke.stapler.DataBoundConstructor;
import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;

public class Application implements Describable<Application> {

    @Extension
    public final static DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    private String applicationName;

    @DataBoundConstructor
    public Application(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    @Override
    public Descriptor<Application> getDescriptor() {
        return DESCRIPTOR;
    }

    public static class DescriptorImpl extends Descriptor<Application> {

        @Override
        public String getDisplayName() {
            return "Application";
        }
    }
}
