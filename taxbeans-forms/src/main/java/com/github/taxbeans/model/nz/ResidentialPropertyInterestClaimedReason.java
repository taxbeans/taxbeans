package com.github.taxbeans.model.nz;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import com.github.taxbeans.forms.RelativeFieldName;

public enum ResidentialPropertyInterestClaimedReason {
	@RelativeFieldName("1")
	maoriExemptCompany,
	@RelativeFieldName("3")
	schedule15Exclusion,
	@RelativeFieldName("5")
	earlyLoanDate,
	@RelativeFieldName("2")
	newBuildException,
	@RelativeFieldName("4")
	developmentBusinessExemption,
	@RelativeFieldName("6")
	emergencyHousing;
}
