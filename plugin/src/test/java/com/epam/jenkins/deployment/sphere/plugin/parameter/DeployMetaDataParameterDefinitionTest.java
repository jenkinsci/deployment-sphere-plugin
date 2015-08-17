package com.epam.jenkins.deployment.sphere.plugin.parameter;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import lombok.extern.java.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.mockito.Matchers;

import com.epam.jenkins.deployment.sphere.plugin.PluginJenkinsRule;
import com.epam.jenkins.deployment.sphere.plugin.TestInput;
import com.epam.jenkins.deployment.sphere.plugin.metadata.model.BuildMetaData;
import com.epam.jenkins.deployment.sphere.plugin.metadata.model.EnvironmentMetaData;
import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.dao.BuildMetaDataDao;
import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.dao.EnvironmentDao;

@Log
@RunWith(Theories.class)
public class DeployMetaDataParameterDefinitionTest {


	@Rule
	public PluginJenkinsRule jenkinsRule = new PluginJenkinsRule();
	
	private static final String APP_NAME = "appName";
	private static Collection<BuildMetaData> buildMetaDataList = new ArrayList<BuildMetaData>();
	private static Collection<EnvironmentMetaData> environmentMetaDataList = new ArrayList<EnvironmentMetaData>();
	
	@Before
	public void setUp() {
		buildMetaDataList.clear();
		environmentMetaDataList.clear();
		BuildMetaData buildMetaData1 = new BuildMetaData();
		buildMetaData1.setApplicationName(APP_NAME);
		buildMetaData1.setBuildVersion("1.00");
		BuildMetaData buildMetaData2 = new BuildMetaData();
		buildMetaData2.setApplicationName(APP_NAME);
		buildMetaData2.setBuildVersion("2.00");
		BuildMetaData buildMetaData3 = new BuildMetaData();
		buildMetaData3.setApplicationName(APP_NAME);
		buildMetaData3.setBuildVersion("2.03");
		
		buildMetaDataList.add(buildMetaData1);
		buildMetaDataList.add(buildMetaData2);
		buildMetaDataList.add(buildMetaData3);
		
		EnvironmentMetaData environmentMetaData1 = new EnvironmentMetaData("env1", "title1");
		EnvironmentMetaData environmentMetaData2 = new EnvironmentMetaData("env2", "title2");
		environmentMetaDataList.add(environmentMetaData1);
		environmentMetaDataList.add(environmentMetaData2);
	}
	
	@DataPoints
    public static final BuildDefinitionTestInput[] testData = new BuildDefinitionTestInput[] {
    	new BuildDefinitionTestInput(buildMetaDataList
    			, Arrays.asList(new String[]{"1.00", "2.00", "2.03"} ))
    };
	
	@DataPoints
	public static final EnvironmentDefinitionTestInput[] testData2 = new EnvironmentDefinitionTestInput[] {
    	new EnvironmentDefinitionTestInput(environmentMetaDataList
    			, Arrays.asList(new String[]{"title1", "title2"}))
    };

	@Theory
	public void shouldCorrectlyGetBuildVersions(BuildDefinitionTestInput input){
		BuildMetaDataDao buildMetaDataDaoMock = mock(BuildMetaDataDao.class);
		DeployMetaDataParameterDefinition definition = mock(DeployMetaDataParameterDefinition.class);
		when(buildMetaDataDaoMock.findByAppName(Matchers.anyString())).thenReturn(input.getInput());
		when(definition.getBuildMetaDataDao()).thenReturn(buildMetaDataDaoMock);
		when(definition.getBuildVersions()).thenCallRealMethod();
	 	assertThat(definition.getBuildVersions(), 
	 			org.hamcrest.Matchers.contains(input.getResult().toArray(new String[input.getResult().size()])));
	}
	
	
	@Theory
	public void shouldCorrectlyGetEnvironmentKeys(EnvironmentDefinitionTestInput input){
		EnvironmentDao environmentDaoMock = mock(EnvironmentDao.class);
		DeployMetaDataParameterDefinition definition = mock(DeployMetaDataParameterDefinition.class);
		when(environmentDaoMock.findAll()).thenReturn(input.getInput());
		when(definition.getEnvironmentDao()).thenReturn(environmentDaoMock);
		when(definition.getEnvironmentKeys()).thenCallRealMethod();
	 	assertThat(definition.getEnvironmentKeys(), 
	 			org.hamcrest.Matchers.contains(input.getResult().toArray(new String[input.getResult().size()])));
	}
	
	public static class BuildDefinitionTestInput extends TestInput<Collection<BuildMetaData>, Collection<String>> {

		public BuildDefinitionTestInput(Collection<BuildMetaData> input,
				Collection<String> result) {
			super(input, result);
		}
	}
	
	public static class EnvironmentDefinitionTestInput extends TestInput<Collection<EnvironmentMetaData>, Collection<String>> {

		public EnvironmentDefinitionTestInput(Collection<EnvironmentMetaData> input,
				Collection<String> result) {
			super(input, result);
		}

	}

}
