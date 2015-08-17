package com.epam.jenkins.deployment.sphere.plugin.utils;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.Test;

public class DateFormatUtilTest {  
	private static final String SAMPLE_DATE = "10:20:30 December 03, 1999";
	private static final String WRONG_DATE = "10 March 2015 12:00:00";
	private static final DateTime SAMPLE_DATE_TIME = new DateTime (1999, 12, 3, 10, 20, 30);

	@Test
	public void shouldTestFormatDate() throws Exception {
		assertThat(DateFormatUtil.formatDate(SAMPLE_DATE_TIME).equals(SAMPLE_DATE), is(true));
		assertThat(DateFormatUtil.formatDate(SAMPLE_DATE_TIME).equals(WRONG_DATE), is(false));
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCheckTwoVariantsOfToDateConversionWithFormat() throws Exception {
		assertThat(DateFormatUtil.toDate(SAMPLE_DATE).equals(SAMPLE_DATE_TIME), is(true));
		DateFormatUtil.toDate(WRONG_DATE).toString();
	}

}
