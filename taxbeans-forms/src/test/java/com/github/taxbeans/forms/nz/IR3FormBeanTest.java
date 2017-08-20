package com.github.taxbeans.forms.nz;

import org.junit.Test;

public class IR3FormBeanTest {
	
	@Test
	public void test() {
		IR3FormBean bean = new IR3FormBean();
		bean.setIrdNumber("55555555");
		bean.setSalutation(Salutation.mr);
		bean.publishDraft();
	}

}
