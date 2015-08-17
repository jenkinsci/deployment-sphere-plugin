package com.epam.jenkins.deployment.sphere.plugin;

import hudson.util.FormValidation.Kind;

public class TestValidationInput extends TestInput<String, Kind> {

	public TestValidationInput(String input, Kind result) {
		super(input, result);
	}

}
