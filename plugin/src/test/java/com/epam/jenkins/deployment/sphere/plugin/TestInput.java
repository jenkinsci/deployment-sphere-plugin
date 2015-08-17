package com.epam.jenkins.deployment.sphere.plugin;

import lombok.Data;

@Data
public class TestInput<T, R> {
	private T input;
	private R result;

	public TestInput(T input, R result) {
		this.input = input;
		this.result = result;
	}
}
