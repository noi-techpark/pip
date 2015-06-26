package it.bz.tis.alpenstaedte.dto;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
public class IdeaDto {
	private String uuid;
	private String projectName;
	private String projectDesc;
	private Map<String,Boolean> topics;
	private List<FundingDto> fundings;
	private String status;
	private Set<String> fileNames = new HashSet<String>();
	
	public IdeaDto() {
	}
	@JsonCreator
	public IdeaDto(@JsonProperty("projectName") String projectName,@JsonProperty("projectDesc") String projectDesc,
			@JsonProperty("topics") Map<String,Boolean> topics,@JsonProperty("fundings") List<FundingDto> fundings, @JsonProperty("status") String status, @JsonProperty("fileNames") Set<String> fileNames) {
		super();
		this.projectName = projectName;
		this.projectDesc = projectDesc;
		this.topics = topics;
		this.fundings = fundings;
		this.status = status;
		this.fileNames = fileNames;
	}
}
