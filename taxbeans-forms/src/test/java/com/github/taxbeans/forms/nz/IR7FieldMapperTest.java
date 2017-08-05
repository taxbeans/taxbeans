package com.github.taxbeans.forms.nz;

import org.junit.Assert;
import org.junit.Test;

public class IR7FieldMapperTest {

	@Test
	public void test() {
		Assert.assertEquals("ird 1",
				IR7FieldMapper.getFieldName(IR7Fields.irdNumber, 2010));
		Assert.assertEquals("1 ird",
				IR7FieldMapper.getFieldName(IR7Fields.irdNumber, 2013));
	}
}
