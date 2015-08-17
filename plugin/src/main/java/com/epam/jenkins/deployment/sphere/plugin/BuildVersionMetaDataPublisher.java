package com.epam.jenkins.deployment.sphere.plugin;

import static java.lang.String.format;
import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import lombok.extern.java.Log;

import com.epam.jenkins.deployment.sphere.plugin.action.DynamicVariablesStoringAction;
import com.epam.jenkins.deployment.sphere.plugin.metadata.Constants;
import com.epam.jenkins.deployment.sphere.plugin.metadata.collector.BuildVersionMetaDataCollector;
import com.epam.jenkins.deployment.sphere.plugin.metadata.model.BuildMetaData;
import com.google.common.base.Strings;

@Log
public class BuildVersionMetaDataPublisher extends Notifier {

    @DataBoundSetter
    private String versionPattern;
    @DataBoundSetter
    private String appName;

    @DataBoundConstructor
    public BuildVersionMetaDataPublisher() {
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    public String getVersionPattern() {
        return versionPattern;
    }

    public String getAppName() {
        return appName;
    }

    @Override
    public boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener)
            throws InterruptedException, IOException {
        log.log(Level.FINE, "Is about to collect build metadata");

        checkArgument(null != listener, "Listener is null, something was wrong.");
        checkArgument(null != build, "Current build is null, something was wrong.");

        BuildMetaData buildMetaData = new BuildVersionMetaDataCollector().collect(build, listener);

        listener.getLogger().append("[deployment-sphere] Collecting build metadata\n").append(buildMetaData.toString());
        log.log(Level.FINEST, format("Next build metadata was collected %s", buildMetaData));

        return true;
    }

    @Override
    public boolean prebuild(final AbstractBuild<?, ?> build, final BuildListener listener) {
        final String pattern = getVersionPattern();
        checkArgument(!Strings.isNullOrEmpty(pattern), "Build Pattern version value must be provided");

        PrintStream logger = listener.getLogger();
        final String thisClassName = getClass().getName();

        logger.append(String.format("[%s]Build type: %s\n", thisClassName, build.getClass().getName()));
        logger.append(String.format("[%s]Build instance: %s\n", thisClassName, build));

        logger.append(String.format("[%s]Build Listener type: %s\n", thisClassName, listener.getClass().getName()));
        logger.append(String.format("[%s]Build Listener instance: %s\n", thisClassName, listener));

        final String buildVersion = pattern.replace("{v}", String.valueOf(build.getNumber()));

        Map<String, String> envVars = new HashMap<>();
        envVars.put(Constants.BUILD_VERSION, buildVersion);
        envVars.put(Constants.BUILD_APP_NAME, this.appName);

        build.addAction(new DynamicVariablesStoringAction(envVars));

        return true;
    }
}
