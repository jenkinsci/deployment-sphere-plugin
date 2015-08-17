package com.epam.jenkins.deployment.sphere.plugin.action;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import hudson.EnvVars;
import hudson.model.FreeStyleBuild;

import java.util.HashMap;
import java.util.Map;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

@RunWith(Theories.class)
public class DynamicVariablesStorageActionTest {
	
	@DataPoints
	public static final String [][] testData = new String [][]{
		{"key_1", "key_value_1"},
		{"key_2", "key_value_2"}
	};
	
    @Theory
    public void shouldRepopulateEnvVarsInCaseUsingMapConstructor(String [] testData) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put(testData[0], testData[1]);
		DynamicVariablesStoringAction dynamicStorage = new DynamicVariablesStoringAction(map);
		FreeStyleBuild build = Mockito.mock(FreeStyleBuild.class);
		EnvVars env = new EnvVars();
		dynamicStorage.buildEnvVars(build, env);
		assertThat(env.get(testData[0]), equalTo(testData[1]));
		assertThat(env.size(), equalTo(1));
    }
    

    @Theory
    public void shouldRepopulateEnvVarsInCaseUsingStringConstructor(String [] testData) throws Exception {
		DynamicVariablesStoringAction dynamicStorage = new DynamicVariablesStoringAction(testData[0], testData[1]);
		FreeStyleBuild build = Mockito.mock(FreeStyleBuild.class);
		EnvVars env = new EnvVars();
		dynamicStorage.buildEnvVars(build, env);
		assertThat(env.get(testData[0]), equalTo(testData[1]));
		assertThat(env.size(), equalTo(1));
    }
}