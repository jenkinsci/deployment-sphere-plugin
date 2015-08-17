package com.epam.jenkins.deployment.sphere.plugin;

import java.util.logging.Level;

import jenkins.model.Jenkins;
import lombok.extern.java.Log;

@Log
public class PluginInjector {
    public static void injectMembers(Object target) {
        Jenkins instance = Jenkins.getInstance();
        if (instance == null) {
            log.log(Level.SEVERE, "Jenkins Instance cannot be loaded");
        } else {
            try {
                instance.getInjector().injectMembers(target);
            } catch (Throwable ex) {
                log.log(Level.WARNING,
                        "JIRA Plugin, which should provide connection to JIRA, is not installed, so JIRA metadata cannot be obtained.");
            }
        }
    }
}
