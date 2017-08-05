package com.github.taxbeans.forms.nz;

import org.junit.Assert;
import org.junit.Test;

public class PartnershipDistributionFormTest {

	@Test
	public void test() {
		Assert.assertEquals("0",
				PartnershipDistributionFormFieldMapper.getFieldName(
						PartnershipDistributionFormFieldEnum.irdNumber, 2010));
		Assert.assertEquals("2",
				PartnershipDistributionFormFieldMapper.getFieldName(
						PartnershipDistributionFormFieldEnum.irdNumber, 2013));
	}
}
