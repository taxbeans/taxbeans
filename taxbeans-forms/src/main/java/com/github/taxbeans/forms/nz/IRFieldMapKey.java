package com.github.taxbeans.forms.nz;

import java.util.Objects;

public class IRFieldMapKey {

	private String field;
	
	private int key;

	public IRFieldMapKey(String field, int key) {
		super();
		this.field = field;
		this.key = key;
	}

	@Override
	public int hashCode() {
		return Objects.hash(field, key);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IRFieldMapKey other = (IRFieldMapKey) obj;
		return Objects.equals(field, other.field) && key == other.key;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	@Override
	public String toString() {
		return "IRFieldMapKey [field=" + field + ", key=" + key + "]";
	}
}
