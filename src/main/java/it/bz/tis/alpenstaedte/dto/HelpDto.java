package it.bz.tis.alpenstaedte.dto;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
public class HelpDto {
	
	private String uuid;
	private String name;
	private Date created_on;
	
	public HelpDto() {
	}
	@JsonCreator
	public HelpDto(@JsonProperty("name") String name) {
		this.name = name;
	}
}
