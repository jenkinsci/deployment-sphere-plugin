package com.epam.jenkins.deployment.sphere.plugin.metadata.collector;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import hudson.EnvVars;
import hudson.model.TaskListener;
import hudson.model.AbstractBuild;

import java.io.PrintStream;
import java.util.Calendar;
import java.util.TreeMap;

import lombok.extern.java.Log;

import org.junit.Rule;
import org.junit.Test;

import com.epam.jenkins.deployment.sphere.plugin.PluginJenkinsRule;
import com.epam.jenkins.deployment.sphere.plugin.metadata.Constants;
import com.epam.jenkins.deployment.sphere.plugin.metadata.model.BuildMetaData;
import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.dao.BuildMetaDataDao;
import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.dao.BuildMetaDataDaoMock;
import com.epam.jenkins.deployment.sphere.plugin.mock.EmptyChangeLogSet;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

@Log

public class BuildVersionMetaDataCollectorTest {
	
	@Rule
	public PluginJenkinsRule jenkinsRule = new PluginJenkinsRule();

	private static final String JOB_URL = "http://test.com/test";

	private AbstractBuild<?, ?> initBuild(){
		AbstractBuild<?, ?> build = mock(AbstractBuild.class);
		when(build.getNumber()).thenReturn(Integer.parseInt("1"));
		when(build.getId()).thenReturn("buildId");
		doReturn(new EmptyChangeLogSet(build)).when(build).getChangeSet();
		when(build.getDisplayName()).thenReturn("jobName");
		when(build.due()).thenReturn(Calendar.getInstance());
		TreeMap<String, String> treeMap = new TreeMap<String,String>();
		treeMap.put(Constants.BUILD_VERSION, "1.2");
		treeMap.put(Constants.BUILD_APP_NAME, "app-name");
		try {
			when(build.getEnvironment(any(TaskListener.class))).thenReturn(new EnvVars(treeMap));
		} catch (Exception e) {
			log.info("Get environment was failed:" + e.getMessage());
		}
		return build;
	}
	private BuildVersionMetaDataCollector getMetaDataCollectorWithMocks() {
		Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
            	bind(BuildMetaDataDao.class).to(BuildMetaDataDaoMock.class);
            }
        });
		BuildVersionMetaDataCollector metaDataCollector = injector.getInstance(BuildVersionMetaDataCollector.class);
		return metaDataCollector;
	}
	
	@Test
	public void shouldGetUrlFromBuildAntPutInBuildMetaDataUrl() {
		BuildVersionMetaDataCollector metaDataCollector = getMetaDataCollectorWithMocks();
		AbstractBuild<?, ?> build = initBuild();
		
		when(build.getUrl()).thenReturn(JOB_URL);
		
		TaskListener listener = mock(TaskListener.class);
		when(listener.getLogger()).thenReturn(new PrintStream(System.out));
		
		BuildMetaData result = metaDataCollector.collect(build, listener);
		assertThat(result.getBuildUrl(),is(JOB_URL));
	}
	
}
