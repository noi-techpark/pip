package it.bz.tis.alpenstaedte.dto;

import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
public class UserDto {
	private String email;

	public UserDto() {
	}

	public UserDto(@JsonProperty("email") String email) {
		this.email = email;
	}
	
}
