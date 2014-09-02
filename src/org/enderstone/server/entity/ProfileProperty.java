package org.enderstone.server.entity;

public class ProfileProperty {

	private String name;
	private String value;
	private boolean isSigned;
	private String signature;

	public ProfileProperty(String name, String value, boolean isSigned, String signature) {
		this.name = name;
		this.value = value;
		this.isSigned = isSigned;
		this.signature = signature;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isSigned() {
		return isSigned;
	}

	public void setSigned(boolean isSigned) {
		this.isSigned = isSigned;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

}
