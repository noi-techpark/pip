package it.bz.tis.alpenstaedte.dto;

import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
public class ResponseObject {
	public ResponseObject() {
	}
	public ResponseObject(String uuid) {
		this.data=uuid;
	}

	private Object data;

}
