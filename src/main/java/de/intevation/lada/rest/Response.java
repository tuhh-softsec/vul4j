package de.intevation.lada.rest;

import java.util.ArrayList;
import java.util.List;

import de.intevation.lada.model.LProbe;

@SuppressWarnings("serial")
public class Response implements java.io.Serializable {
	
	private Boolean success;
	private String message;
	private List<LProbe> data;
	private String errors;
	private String warnings;
	
	public Response(Boolean success, String message, LProbe data) {
		super();
		this.success = success;
		this.message = message;
		this.data = new ArrayList<LProbe>();
		this.data.add(data);
	}
	public Response(boolean success, String message, List<LProbe> data) {
		this.success = success;
		this.message = message;
		this.data = data;
	}
	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<LProbe> getData() {
		return data;
	}
	public void setData(List<LProbe> data) {
		this.data = data;
	}
	public String getErrors() {
		return errors;
	}
	public void setErrors(String errors) {
		this.errors = errors;
	}
	public String getWarnings() {
		return warnings;
	}
	public void setWarnings(String warnings) {
		this.warnings = warnings;
	}
}
