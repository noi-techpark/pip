package it.bz.tis.alpenstaedte.dto;

import java.util.List;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
public class NewIdeaDto {
	

	public NewIdeaDto() {
	}
	
	@JsonCreator
	public NewIdeaDto(@JsonProperty("projectName") String projectName,@JsonProperty("projectDesc") String projectDesc,
			@JsonProperty("topics") Set<TopicDto> topics,@JsonProperty("fundings") List<FundingDto> fundings) {
		super();
		this.projectName = projectName;
		this.projectDesc = projectDesc;
		this.topics = topics;
		this.fundings = fundings;
	}
	private String uuid;
	private String projectName;
	private String projectDesc;
	private Set<TopicDto> topics;
	private List<FundingDto> fundings;
}
