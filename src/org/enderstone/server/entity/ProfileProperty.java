package org.enderstone.server.entity;

public class ProfileProperty {

	private String name;
	private String value;
	private String signature;

	public ProfileProperty(String name, String value, String signature) {
		this.name = name;
		this.value = value;
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

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
}
