package com.company;

public class DataFormatException extends Exception {
	private static final long serialVersionUID = 42L; // for Serializable interface
	public DataFormatException(String msg) {
		super(msg);
	}
	public DataFormatException(Exception ex) {
		super(ex);
	}
	public DataFormatException(String msg, Exception ex) {
		super(msg, ex);
	}
}