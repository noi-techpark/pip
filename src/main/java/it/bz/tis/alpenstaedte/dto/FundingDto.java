package it.bz.tis.alpenstaedte.dto;

import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
public class FundingDto {
	
	public FundingDto() {
	}
	
	public FundingDto(@JsonProperty("url")String url,@JsonProperty("desc") String description) {
		super();
		this.url = url;
		this.description = description;
	}
	public FundingDto(String uuid, String url, String description) {
		this.uuid = uuid;
		this.url = url;
		this.description = description;
	}
	private String uuid;
	private String url;
	private String description;
}
