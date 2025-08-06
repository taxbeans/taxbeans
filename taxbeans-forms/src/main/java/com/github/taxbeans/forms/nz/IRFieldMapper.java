package com.github.taxbeans.forms.nz;

import java.util.Map;

public interface IRFieldMapper {
	
	public Map<IRFieldMapKey, String> getPropertyToFieldMap(int year);
}
