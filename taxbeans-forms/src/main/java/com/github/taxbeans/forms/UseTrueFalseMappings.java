package com.github.taxbeans.forms;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface UseTrueFalseMappings {

	String fieldName() default "";

	String trueValue() default "";
	
	String falseValue() default "";

}

