package com.github.taxbeans.forms.nz;

import org.junit.Assert;
import org.junit.Test;


public class IR10FieldMapperTest {

	@Test
	public void test() {
		Assert.assertEquals("IRD 1",
				IR10FieldMapper.getFieldName(IR10Fields.irdNumber, 2010));
		Assert.assertEquals("IRD number",
				IR10FieldMapper.getFieldName(IR10Fields.irdNumber, 2013));
	}
}
