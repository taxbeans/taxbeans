package com.github.taxbeans.forms.nz;

import org.junit.Assert;
import org.junit.Test;

public class PartnershipDistributionFormTest {

	@Test
	public void test() {
		Assert.assertEquals("0",
				IR7PFieldMapper.getFieldName(
						IR7PFields.irdNumber, 2010));
		Assert.assertEquals("2",
				IR7PFieldMapper.getFieldName(
						IR7PFields.irdNumber, 2013));
	}
}
