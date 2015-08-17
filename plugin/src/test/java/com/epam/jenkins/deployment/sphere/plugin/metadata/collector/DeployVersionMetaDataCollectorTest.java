package com.epam.jenkins.deployment.sphere.plugin.metadata.collector;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import lombok.extern.java.Log;

import com.epam.jenkins.deployment.sphere.plugin.PluginJenkinsRule;
import com.epam.jenkins.deployment.sphere.plugin.metadata.Constants;
import com.epam.jenkins.deployment.sphere.plugin.metadata.model.DeploymentMetaData;
import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.dao.DeploymentMetaDataDao;
import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.dao.EnvironmentDao;
import com.epam.jenkins.deployment.sphere.plugin.metadata.persistence.domain.Environment;
import com.epam.jenkins.deployment.sphere.plugin.utils.DateFormatUtil;

@Log
public class DeployVersionMetaDataCollectorTest {

    private static final Calendar DEPLOYMENT_DATE = new GregorianCalendar();

    @Rule
    public PluginJenkinsRule jenkinsRule = new PluginJenkinsRule();

    @Mock
    private DeploymentMetaDataDao deploymentMetaDataDao;

    @Mock
    private EnvironmentDao environmentDao;

    @InjectMocks
    private DeployVersionMetaDataCollector metaDataCollector;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Environment env = new Environment();
        env.setKey("env_key");
        when(environmentDao.find("env-name")).thenReturn(env);
    }

    @Test
    public void shouldGetDateFromBuildAntPutInDeploymentMetaData() throws IOException, InterruptedException {
        AbstractBuild<?, ?> build = initBuild();
        TaskListener listener = mock(TaskListener.class);
        when(listener.getLogger()).thenReturn(new PrintStream(System.out));

        DeploymentMetaData result = metaDataCollector.collect(build, listener);
        String asExpected = DateFormatUtil.formatDate(new DateTime(DEPLOYMENT_DATE));
        assertThat(result.getDeployedAt(), is(asExpected));
    }

    private AbstractBuild<?, ?> initBuild() throws IOException, InterruptedException {
        AbstractBuild<?, ?> build = mock(AbstractBuild.class);
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        when(build.due()).thenReturn(DEPLOYMENT_DATE);
        treeMap.put(Constants.BUILD_VERSION, "1.2");
        treeMap.put(Constants.BUILD_APP_NAME, "app-name");
        treeMap.put(Constants.ENV_NAME, "env-name");
        when(build.getEnvironment(any(TaskListener.class))).thenReturn(new EnvVars(treeMap));
        return build;
    }

}
