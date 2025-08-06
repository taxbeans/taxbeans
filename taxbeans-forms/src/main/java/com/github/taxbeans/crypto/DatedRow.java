package com.github.taxbeans.crypto;

import java.time.LocalDateTime;

public class DatedRow implements Comparable<DatedRow> {
	
	private LocalDateTime date;
	
	private String[] columns;

	public DatedRow(LocalDateTime localDate, String[] columns2) {
		date = localDate;
		columns = columns2;
	}

	@Override
	public int compareTo(DatedRow o1) {
		return this.getDate().compareTo(o1.getDate());
	}

	public LocalDateTime getDate() {
		return date;
	}
	
	public void setDate(LocalDateTime localDate) {
		this.date = localDate;
	}

	public String[] getColumns() {
		return columns;
	}

	public void setColumns(String[] columns) {
		this.columns = columns;
	}

}
