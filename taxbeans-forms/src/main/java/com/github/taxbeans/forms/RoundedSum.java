package com.github.taxbeans.forms;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RoundedSum {

	String[] value();
	
	String[] negate() default {};
}
