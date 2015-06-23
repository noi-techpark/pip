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

	private String url;
	private String description;
}
