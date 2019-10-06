package com.github.taxbeans.forms.nz;

public class IR7Form2018Test {

	public static void main(String[] args) {
		// MJHL IR7
		IR7Form2018 bean = new IR7Form2018();
		bean.setYearEnded(2018);
		bean.setIrdNumber("888-888-888");
		bean.setFullname("Example Partnership");
		bean.setPartnershipNameLine1("Example Partnership");
		bean.setPartnershipNameLine2("Example Partnership Line 2");
		bean.setPartnershipTradingNameLine1("Example Partnership");
		bean.setPartnershipTradingNameLine2("Example Partnership Line 2");
		bean.setPostalAddressLine1("100 Queen Street");
		bean.setPostalAddressLine2("Auckland CBD");
		bean.setPhysicalAddressLine1("100 Queen Street");
		bean.setPhysicalAddressLine2("Auckland CBD");
		bean.setBicCode("Bic101");
		bean.setDaytimePhoneNumberPrefix("021");
		bean.setDaytimePhoneNumberSuffix("8888888");
		bean.setFirstReturnRadio(false);
		bean.setSchedularPaymentsRadio(false);
		bean.setPartnershipCeasedRadio(false);		
		bean.publishDraft();
	}

}
