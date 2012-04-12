package com.alta189.simplesave.test;

import com.alta189.simplesave.Field;
import com.alta189.simplesave.Id;
import com.alta189.simplesave.Table;

@Table(name = "Test")
public class TestTable {

	@Id
	private int id;

	@Field
	private String data;

	@Field
	private double d;

}
