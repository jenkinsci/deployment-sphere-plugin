package com.epam.jenkins.deployment.sphere.plugin;

import hudson.Functions;

import java.io.File;
import java.net.URLConnection;

import org.jvnet.hudson.test.JenkinsRule;

public class PluginJenkinsRule extends JenkinsRule{
	private boolean origDefaultUseCache = true;
     
	@Override
	public void before() throws Throwable {
		if (Functions.isWindows()) {
			URLConnection aConnection = new File(".").toURI().toURL().openConnection();
			origDefaultUseCache = aConnection.getDefaultUseCaches();
			aConnection.setDefaultUseCaches(false);
		}
		super.before();
	}

	@Override
	public void after() throws Exception {
		super.after();
		if (Functions.isWindows()) {
			URLConnection aConnection = new File(".").toURI().toURL().openConnection();
			aConnection.setDefaultUseCaches(origDefaultUseCache);
		}
	}
}
