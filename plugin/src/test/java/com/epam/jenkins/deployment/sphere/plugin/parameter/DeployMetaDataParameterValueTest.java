package com.epam.jenkins.deployment.sphere.plugin.parameter;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import hudson.EnvVars;
import hudson.model.FreeStyleBuild;
import hudson.slaves.EnvironmentVariablesNodeProperty;
import lombok.extern.java.Log;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.epam.jenkins.deployment.sphere.plugin.metadata.Constants;

@Log
@RunWith(Theories.class)
public class DeployMetaDataParameterValueTest {
    
	@DataPoints
    public static final String[][] testData = new String [][]{
    	{"qa", "0.1", "app1"}
    };
	
	@Theory
	public void shouldSuccessfulRepopulateBuildEnvVars(String[] testData) throws Exception {
		log.info("Test buildEnvVars");
		FreeStyleBuild build = Mockito.mock(FreeStyleBuild.class);
		DeployMetaDataParameterValue value = new DeployMetaDataParameterValue(Constants.DEPLOY_META_DATA, testData[0], testData[1], testData[2]);
		EnvironmentVariablesNodeProperty prop = new EnvironmentVariablesNodeProperty();
	    EnvVars envVars = prop.getEnvVars();
		value.buildEnvVars(build, envVars);
		assertThat(envVars.get(Constants.ENV_NAME), equalTo(testData[0]));
		assertThat(envVars.get(Constants.BUILD_VERSION), equalTo(testData[1]));
		assertThat(envVars.get(Constants.BUILD_APP_NAME), equalTo(testData[2]));
		log.info("Done");
	}
	
	@Theory
	public void shouldSuccessfulResolveVariableValue(String[] testData) throws Exception{
		log.info("Test createVariableResolver");
		FreeStyleBuild build = Mockito.mock(FreeStyleBuild.class);
		DeployMetaDataParameterValue value = new DeployMetaDataParameterValue(Constants.DEPLOY_META_DATA, testData[0], testData[1], testData[2]);
		assertThat(value.createVariableResolver(build).resolve(Constants.ENV_NAME), equalTo(testData[0]));
		assertThat(value.createVariableResolver(build).resolve(Constants.BUILD_VERSION), equalTo(testData[1]));
		assertThat(value.createVariableResolver(build).resolve(Constants.BUILD_APP_NAME), equalTo(testData[2]));
		log.info("Done");
	}

}
