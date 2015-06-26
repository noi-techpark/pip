package it.bz.tis.alpenstaedte.dto;

import java.util.List;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
public class CurrentFilesDto {
	private List<String> alreadySavedFiles;
	
	public CurrentFilesDto() {
	}

	@JsonCreator
	public CurrentFilesDto(@JsonProperty("alreadySavedFiles")List<String> alreadySavedFiles) {
		super();
		this.alreadySavedFiles = alreadySavedFiles;
	}
}
