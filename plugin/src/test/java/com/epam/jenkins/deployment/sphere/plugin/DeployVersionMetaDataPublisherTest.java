package com.epam.jenkins.deployment.sphere.plugin;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import hudson.model.BuildListener;
import hudson.model.FreeStyleBuild;
import hudson.model.AbstractBuild;
import hudson.tasks.BuildStepMonitor;

import org.junit.Test;

public class DeployVersionMetaDataPublisherTest {

	@Test
	public void shouldReturnBuildStepMonitorEqualsBuild() throws Exception {
		DeployVersionMetaDataPublisher publisher = mock(DeployVersionMetaDataPublisher.class);
		when(publisher.getRequiredMonitorService()).thenCallRealMethod();
		assertThat("Illegal required monitor service ", publisher.getRequiredMonitorService(), is(BuildStepMonitor.BUILD));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowAnExceptionInCasePassedBuildIsNull() throws Exception {
		DeployVersionMetaDataPublisher publisher = mock(DeployVersionMetaDataPublisher.class);
		BuildListener buildListener = mock(BuildListener.class);
		when(publisher.perform((AbstractBuild)null, null, buildListener)).thenCallRealMethod();

        publisher.perform((AbstractBuild)null, null, buildListener);
	}

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowAnExceptionInCasePassedBuildListenerIsNull() throws Exception {
        DeployVersionMetaDataPublisher publisher = mock(DeployVersionMetaDataPublisher.class);
        AbstractBuild build = mock(FreeStyleBuild.class);
        when(publisher.perform(build, null, null)).thenCallRealMethod();

        publisher.perform(build, null, null);
    }

}
