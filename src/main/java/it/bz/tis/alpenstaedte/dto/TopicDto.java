package it.bz.tis.alpenstaedte.dto;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
public class TopicDto {
	private String name;
	private String color;
	private String uuid;
	public TopicDto() {
	}
	@JsonCreator
	public TopicDto(@JsonProperty("uuid")String uuid, @JsonProperty("name") String name,@JsonProperty("color") String color) {
		this.uuid = uuid;
		this.name = name;
		this.color = color;
	}
	
}
