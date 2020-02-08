package com.github.taxbeans.forms.nz;

import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.taxbeans.forms.RightAlign;
import com.github.taxbeans.forms.Skip;
import com.github.taxbeans.forms.common.FormDestination;

public class IR4Form2019 implements FormDestination {

	@Skip
	private String destinationDirectory;

	@RightAlign(9)
	private String irdNumber;
	
	final Logger logger = LoggerFactory.getLogger(IR4Form2019.class);

	@Skip
	private String personalisedNaming;

	private int year = 2019;
	
	public String calculateMinusSign(Money value) {
		return value.signum() < 0 ? "-" : "";
	}

	public String getDestinationDirectory() {
		return destinationDirectory;
	}

	public String getIrdNumber() {
		return irdNumber;
	}
	
	public String getPersonalisedNaming() {
		return personalisedNaming;
	}

	public int getYear() {
		return year;
	}

	public void setDestinationDirectory(String destinationDirectory) {
		this.destinationDirectory = destinationDirectory;
	}

	public void setIrdNumber(String irdNumber) {
		this.irdNumber = irdNumber;
	}

	public void setPersonalisedNaming(String personalisedNaming) {
		this.personalisedNaming = personalisedNaming;
	}

	public void setYear(int year) {
		this.year = year;
	}
}
