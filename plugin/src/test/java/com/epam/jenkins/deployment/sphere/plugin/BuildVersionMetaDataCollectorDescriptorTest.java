package com.epam.jenkins.deployment.sphere.plugin;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import hudson.matrix.MatrixProject;
import hudson.model.FreeStyleProject;
import hudson.util.FormValidation.Kind;


@RunWith(Theories.class)
public class BuildVersionMetaDataCollectorDescriptorTest {

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
		BuildVersionMetaDataCollectorDescriptor buildCollectorDescriptorMock = mock(BuildVersionMetaDataCollectorDescriptor.class);
		when(buildCollectorDescriptorMock.isApplicable(input.getInput())).thenCallRealMethod();
		assertThat(buildCollectorDescriptorMock.isApplicable(input.getInput()), is(input.getResult()));
	}

	
	@Theory
	public void shouldCorrectValidatePassedVersionPattern(TestValidationInput input) throws Exception{
		BuildVersionMetaDataCollectorDescriptor buildCollectorDescriptor = mock(BuildVersionMetaDataCollectorDescriptor.class);
		when(buildCollectorDescriptor.doCheckVersionPattern(anyString())).thenCallRealMethod();
		assertThat(buildCollectorDescriptor.doCheckVersionPattern(input.getInput()).kind, is(input.getResult()));	
	}

}
