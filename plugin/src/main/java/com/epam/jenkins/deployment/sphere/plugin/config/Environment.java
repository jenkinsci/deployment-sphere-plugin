package com.epam.jenkins.deployment.sphere.plugin.config;

import org.kohsuke.stapler.DataBoundConstructor;
import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;

public class Environment implements Describable<Environment> {
    @Extension
    public final static DescriptorImpl DESCRIPTOR = new DescriptorImpl();
    private String title;

    @DataBoundConstructor
    public Environment(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public Descriptor<Environment> getDescriptor() {
        return DESCRIPTOR;
    }

    public static class DescriptorImpl extends Descriptor<Environment> {

        @Override
        public String getDisplayName() {
            return "Environment";
        }
    }
}
