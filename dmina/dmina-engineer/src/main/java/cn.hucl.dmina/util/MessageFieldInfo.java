package hht.dss.commmodule.util;

import java.io.Serializable;

public class MessageFieldInfo {
	private String name;
	private String type;
	private Serializable value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setValue(Serializable value) {
		this.value = value;
	}

	public Serializable getValue() {
		return value;
	}

}
