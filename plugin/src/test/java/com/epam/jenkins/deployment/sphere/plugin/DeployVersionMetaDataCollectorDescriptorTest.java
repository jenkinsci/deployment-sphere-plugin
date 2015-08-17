package com.epam.jenkins.deployment.sphere.plugin;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import hudson.matrix.MatrixProject;
import hudson.model.FreeStyleProject;
import hudson.util.FormValidation.Kind;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class DeployVersionMetaDataCollectorDescriptorTest {

	@DataPoints
	public static TestValidationInput[] validationTestInput = new TestValidationInput[] {
			new TestValidationInput("", Kind.ERROR), 
			new TestValidationInput(null, Kind.ERROR),
			new TestValidationInput("notEmpy", Kind.OK) 
	};
	
	@DataPoints
	public static TestApplicableInput[] applicableTestInput = new TestApplicableInput[] {
			new TestApplicableInput(FreeStyleProject.class, true), 
			new TestApplicableInput(MatrixProject.class, true)
	};
	
	@Theory
	public void shouldAlwaysRerturnIsApplicableEqualsTrue(TestApplicableInput input) throws Exception {
		DeployVersionMetaDataCollectorDescriptor deployCollectorDescriptor = mock(DeployVersionMetaDataCollectorDescriptor.class);
		when(deployCollectorDescriptor.isApplicable(input.getInput())).thenCallRealMethod();
		assertThat(deployCollectorDescriptor.isApplicable(input.getInput()), is(input.getResult()));
	}
	
	
	@Theory
	public void shouldCorrectValidatePassedAppName(TestValidationInput input) throws Exception{
		BuildVersionMetaDataCollectorDescriptor buildCollectorDescriptor = mock(BuildVersionMetaDataCollectorDescriptor.class);
		when(buildCollectorDescriptor.doCheckAppName(anyString())).thenCallRealMethod();
		assertThat(buildCollectorDescriptor.doCheckAppName(input.getInput()).kind, is(input.getResult()));
	}
}
