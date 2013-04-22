package net.onrc.onos.registry.controller;

public class RegistryException extends Exception {

	private static final long serialVersionUID = -8276300722010217913L;
	
	/*
	public RegistryException() {
		// TODO Auto-generated constructor stub
	}

	public RegistryException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
	*/
	
	public RegistryException(String message) {
		super(message);
	}
	
	public RegistryException(String message, Throwable cause) {
		super(message, cause);
	}

}
