package com.epam.jenkins.deployment.sphere.plugin.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class StringUtilsTest {

    @Test
    public void shouldReturnNullWhenNullPassed() {
        assertNull("Null should be returned.", StringUtils.upperCase(null));
    }

    @Test
    public void shouldReturnEmptyStringWhenEmptyStringPassed() {
        String expected = "";

        String actual = StringUtils.upperCase("");

        assertEquals("An empty string should be returned.", expected, actual);
    }

    @Test
    public void shouldReturnUpperCaseWhenLowerCasePassed() {
        String expected = "THE TEST";

        String actual = StringUtils.upperCase("the test");

        assertEquals("String in the upper case should be returned.", expected, actual);
    }

    @Test
    public void shouldReturnUpperCaseWhenMixedCasePassed() {
        String expected = "THE_TEST";

        String actual = StringUtils.upperCase("tHe_TeSt");

        assertEquals("String in the upper case should be returned.", expected, actual);
    }

    @Test
    public void shouldReturnUpperCaseWhenUpperCasePassed() {
        String expected = "THE-TEST";

        String actual = StringUtils.upperCase("THE-TEST");

        assertEquals("String in the upper case should be returned.", expected, actual);
    }

}
