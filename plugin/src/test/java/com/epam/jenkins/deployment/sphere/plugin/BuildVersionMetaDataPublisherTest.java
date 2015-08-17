package com.epam.jenkins.deployment.sphere.plugin;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import hudson.model.BuildListener;
import hudson.model.FreeStyleBuild;
import hudson.model.AbstractBuild;
import hudson.tasks.BuildStepMonitor;

import java.io.PrintStream;

import org.junit.Test;

public class BuildVersionMetaDataPublisherTest {

	@Test
	public void shouldReturnMonitorServiceEqualsBuild() throws Exception {
		BuildVersionMetaDataPublisher publisher = new BuildVersionMetaDataPublisher();
		assertThat("Illegal build monitor step value", publisher.getRequiredMonitorService(), is(BuildStepMonitor.BUILD));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowAnExceptionInCasePassedBuildIsNull() throws Exception {
        BuildVersionMetaDataPublisher publisher = new BuildVersionMetaDataPublisher();
		BuildListener buildListener = mock(BuildListener.class);
        publisher.perform((AbstractBuild) null, null, buildListener);
	}

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowAnExceptionInCasePassedBuildListenerIsNull() throws Exception {
        BuildVersionMetaDataPublisher publisher = new BuildVersionMetaDataPublisher();
        AbstractBuild build = mock(FreeStyleBuild.class);
        publisher.perform(build, null, null);
    }

	@Test
	public void shouldSuccessfulPreBuild() throws Exception {
		BuildVersionMetaDataPublisher publisher = mock(BuildVersionMetaDataPublisher.class);
		AbstractBuild build = mock(AbstractBuild.class);
		BuildListener listener = mock(BuildListener.class);
		PrintStream printStream = mock(PrintStream.class);
		
		when(listener.getLogger()).thenReturn(printStream);
		when(publisher.getVersionPattern()).thenReturn("x.x.{v}");
		when(publisher.prebuild(build, listener)).thenCallRealMethod();
		
		assertThat(publisher.prebuild(build, listener), is(true));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowAnExceptionInCaseVersionIsNullOnPreBuild() throws Exception {
		BuildVersionMetaDataPublisher publisher = mock(BuildVersionMetaDataPublisher.class);
		AbstractBuild build = mock(AbstractBuild.class);
		BuildListener listener = mock(BuildListener.class);
		PrintStream printStream = mock(PrintStream.class);
		
		when(listener.getLogger()).thenReturn(printStream);
		when(publisher.getVersionPattern()).thenReturn("");
		when(publisher.prebuild(build, listener)).thenCallRealMethod();
		
		publisher.prebuild(build, listener);
	}

}
