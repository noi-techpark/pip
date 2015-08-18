package it.bz.tis.alpenstaedte.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
public class IdeaDto {
	private String uuid;
	private String projectName;
	private String projectDesc;
	private String objectives;
	private String outputs;
	private String measures;
	private String target;
	private Set<TopicDto> topics;
	private List<FundingDto> fundings;
	private String status;
	private Set<String> fileNames = new HashSet<String>();
	private List<CommentDto> comments = new ArrayList<CommentDto>();
	private Set<OrganisazionDto> interestedOrganisazions = new HashSet<OrganisazionDto>();
	private UserDto author;
	private List<UserDto> followers = new ArrayList<UserDto>() ;
	private Date created_on;
	private Integer budget;
	public IdeaDto() {
	}
	@JsonCreator
	public IdeaDto(@JsonProperty("uuid") String uuid,
			@JsonProperty("projectName") String projectName,
			@JsonProperty("projectDesc") String projectDesc,
			@JsonProperty("topics") Set<TopicDto> topics,
			@JsonProperty("fundings") List<FundingDto> fundings,
			@JsonProperty("status") String status,
			@JsonProperty("fileNames") Set<String> fileNames,
			@JsonProperty("budget") Integer budget,
			@JsonProperty("objectives") String objectives,
			@JsonProperty("outputs") String outputs,
			@JsonProperty("measures") String measures,
			@JsonProperty("target") String target) {
		this.uuid = uuid;
		this.projectName = projectName;
		this.projectDesc = projectDesc;
		this.topics = topics;
		this.fundings = fundings;
		this.status = status;
		this.fileNames = fileNames;
		this.budget = budget;
		this.objectives = objectives;
		this.outputs = outputs;
		this.measures = measures;
		this.target = target;
	}
}
