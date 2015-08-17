package com.epam.jenkins.deployment.sphere.plugin.listener;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import hudson.model.FreeStyleProject;
import hudson.model.ParameterDefinition;
import hudson.model.ParametersDefinitionProperty;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;

import com.epam.jenkins.deployment.sphere.plugin.PluginConstants;
import com.epam.jenkins.deployment.sphere.plugin.PluginJenkinsRule;
import com.epam.jenkins.deployment.sphere.plugin.DeployVersionMetaDataPublisher;
import com.epam.jenkins.deployment.sphere.plugin.metadata.Constants;
import com.epam.jenkins.deployment.sphere.plugin.parameter.DeployMetaDataParameterDefinition;

public class DeployVersionMetaDataListenerTest {

	@Rule
	public PluginJenkinsRule jenkinsRule = new PluginJenkinsRule();
	
	@Test
	public void shouldCreateConfigParameterOnCreateProject() throws Exception {
		assertThat(checkProjectConfigurationWithPlugin(true, 
				createFreeStyleProject("Deploy Job 1", PluginConstants.APP_NAME), PluginConstants.APP_NAME), is(true));
	}

	@Test
	public void shouldCreateConfigParameterOnUpdateProject() throws Exception {
		assertThat(checkProjectConfigurationWithPlugin(false, 
				createFreeStyleProject("Deploy Job 2", PluginConstants.APP_NAME), PluginConstants.APP_NAME), is(true));
	}
	
	@Test
	public void shouldUpdateConfigParameterOnUpdateProject() throws Exception {
		FreeStyleProject project = createFreeStyleProject("Deploy Job 3", PluginConstants.APP_NAME);
		DeployVersionMetaDataListener listener = new DeployVersionMetaDataListener();
		listener.onCreated(project);
		project.save();
		project.getPublishersList().clear();
		project.getPublishersList().add(new DeployVersionMetaDataPublisher(PluginConstants.APP_NAME + "-updated"));
		assertThat(checkProjectConfigurationWithPlugin(false, project, PluginConstants.APP_NAME + "-updated"), is(true));
	}

	private boolean checkProjectConfigurationWithPlugin(boolean isCreate, FreeStyleProject project, String appName)
			throws Exception {
		DeployVersionMetaDataListener listener = new DeployVersionMetaDataListener();
		if (isCreate) {
			listener.onCreated(project);
		} else {
			listener.onUpdated(project);
		}
		ParametersDefinitionProperty paramDefProp = (ParametersDefinitionProperty) project.getProperty(ParametersDefinitionProperty.class);
		if (null != paramDefProp) {
			for (ParameterDefinition paramDefinition : paramDefProp.getParameterDefinitions()) {
				DeployMetaDataParameterDefinition depoyParamDefinition = (DeployMetaDataParameterDefinition)paramDefinition;
				if (depoyParamDefinition.getName().equals(Constants.DEPLOY_META_DATA)) {
					return appName.equals(depoyParamDefinition.getApplicationName());
				}
			}
		}
		return false;
	}
	
	private FreeStyleProject createFreeStyleProject(String projectName, String appName) throws IOException{
		FreeStyleProject project = jenkinsRule.createFreeStyleProject(projectName);
		project.getPublishersList().add(new DeployVersionMetaDataPublisher(appName));
		project.save();	
		return project;
	}
}
