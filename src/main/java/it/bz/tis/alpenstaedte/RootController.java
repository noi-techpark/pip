package it.bz.tis.alpenstaedte;
import it.bz.tis.alpenstaedte.dto.FundingDto;
import it.bz.tis.alpenstaedte.dto.Graph2Dto;
import it.bz.tis.alpenstaedte.dto.GraphDto;
import it.bz.tis.alpenstaedte.dto.GraphTopicDto;
import it.bz.tis.alpenstaedte.dto.GraphTopicRootDto;
import it.bz.tis.alpenstaedte.dto.IdeaDto;
import it.bz.tis.alpenstaedte.dto.NewIdeaDto;
import it.bz.tis.alpenstaedte.dto.ProjectStatusDto;
import it.bz.tis.alpenstaedte.dto.ReducedIdeaDto;
import it.bz.tis.alpenstaedte.dto.ResponseObject;
import it.bz.tis.alpenstaedte.dto.StatusIdeasDto;
import it.bz.tis.alpenstaedte.dto.TopicDto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/")
@Controller
public class RootController {
	
	private ObjectMapper jsonMapper = new ObjectMapper();
	@Autowired
	private FileSystemResource documentFolder;
	
    @RequestMapping(method = RequestMethod.POST, value = "create")
    public @ResponseBody ResponseEntity<ResponseObject> create(@RequestBody NewIdeaDto dto) {
    	Set<Topic> topics = new HashSet<Topic>();
    	for(TopicDto topicDto :dto.getTopics()){
    		Topic topic = Topic.findTopicsByNameEquals(topicDto.getName()).getSingleResult();
   			topics.add(topic);
    	}
    	ProjectStatus status = ProjectStatus.findProjectStatusesByNameEquals("idea").getSingleResult();
    	Idea idea = new Idea(dto.getProjectName(),dto.getProjectDesc(),topics,status);
    	Set<Funding> fundings = new HashSet<Funding>();
    	for (FundingDto fundingDto:dto.getFundings()){
			Funding funding = new Funding();
			funding.setIdea(idea);
    		funding.setUrl(fundingDto.getUrl());
    		funding.setDescription(fundingDto.getDescription());
    		funding.persist();
    	}
    	idea.setFundings(fundings);
    	idea.persist();

    	return new ResponseEntity<ResponseObject>(new ResponseObject(idea.getUuid()),HttpStatus.OK);
    }
    @RequestMapping(method = RequestMethod.DELETE, value = "delete/{uuid}")
    public @ResponseBody ResponseEntity<ResponseObject> delete(@PathVariable("uuid") String uuid) throws IOException {
    	Idea idea = Idea.findIdeasByUuidEquals(uuid).getSingleResult();
    	idea.remove();
    	if (documentFolder.exists()){
    		File ideaDirectory = new File(documentFolder.getPath()+"/"+uuid);
    		if (ideaDirectory.isDirectory()){
    			FileUtils.deleteDirectory(ideaDirectory);
    		}
    	}
    	return new ResponseEntity<ResponseObject>(HttpStatus.OK);
    }

	@RequestMapping(method = RequestMethod.POST, value = "update")
    public @ResponseBody ResponseEntity<ResponseObject> update(@RequestBody IdeaDto dto) {
		Set<Topic> topics = new HashSet<Topic>();
    	for(TopicDto topicDto:dto.getTopics()){
   			Topic topic = Topic.findTopicsByNameEquals(topicDto.getName()).getSingleResult();
 				topics.add(topic);
    	}
    	ProjectStatus status = ProjectStatus.findProjectStatusesByNameEquals(dto.getStatus()).getSingleResult();
    	Idea idea = Idea.findIdeasByUuidEquals(dto.getUuid()).getSingleResult();
    	idea.setName(dto.getProjectName());
    	idea.setDescription(dto.getProjectDesc());
    	idea.setStatus(status);
    	idea.setTopics(topics);
    	idea.setFileNames(dto.getFileNames());
    	Set<Funding> fundings = new HashSet<Funding>();
    	for (FundingDto fDto : dto.getFundings()){
    		Funding f;
    		if (fDto.getUuid()!=null){
    			f=Funding.findFundingsByUuid(fDto.getUuid()).getSingleResult();
    			f.setUrl(fDto.getUrl());
    			f.setDescription(fDto.getDescription());
        		f.merge();
    		}
    		else{
    			f = new Funding(fDto.getUrl(),fDto.getDescription(),idea);
    			f.persist();
    		}
    		fundings.add(f);
    	}
    	for (Funding f:idea.getFundings()){
    		if (!fundings.contains(f))
    			f.remove();
    	}
    	idea.setFundings(fundings);
    	idea.merge();
    	return new ResponseEntity<ResponseObject>(new ResponseObject(idea.getUuid()),HttpStatus.OK);
    }
    @RequestMapping(method = RequestMethod.GET, value = "ideas")
    public @ResponseBody ResponseEntity<List<NewIdeaDto>> getIdeas() {
    	List<NewIdeaDto> list = new ArrayList<NewIdeaDto>();
    	for (Idea idea: Idea.findAllIdeas("name","ASC")){
    		NewIdeaDto dto = new NewIdeaDto(idea.getName(), idea.getDescription(), null, null);
    		dto.setUuid(idea.getUuid());
    		list.add(dto);
    	}
    	return new ResponseEntity<List<NewIdeaDto>>(list,HttpStatus.OK);
    }
    @RequestMapping(method = RequestMethod.GET, value = "graph-data")
    public @ResponseBody ResponseEntity <GraphDto> getGraphData() {
    	List<ProjectStatus> statuses = ProjectStatus.findAllProjectStatuses();
    	List<Topic> topics = Topic.findAllTopics();
    	GraphDto graph = new GraphDto();
    	for (ProjectStatus status:statuses){
    		StatusIdeasDto statusDto = new StatusIdeasDto();
    		for (Topic topic:topics){
    			GraphTopicDto topicDto = new GraphTopicDto();
        		List<Idea> ideas = Idea.findIdeaByStatusAndTopicsContainsTopic(status,topic);
        		topicDto.setName(topic.getName());
        		for(Idea idea:ideas){
        			ReducedIdeaDto dto = new ReducedIdeaDto();
        			dto.setName(idea.getName());
        			dto.setUuid(idea.getUuid());
        			topicDto.getChildren().add(dto);
        		}
        		if(!topicDto.getChildren().isEmpty())
        			statusDto.getChildren().add(topicDto);
    		}
    		statusDto.setName(status.getName());
    		graph.getChildren().add(statusDto);
    	}
    	return new ResponseEntity<GraphDto>(graph,HttpStatus.OK);
    }
    @RequestMapping(method = RequestMethod.GET, value = "graph-data-topics")
    public @ResponseBody ResponseEntity <Graph2Dto> getGraphDataByTopics() {
    	List<ProjectStatus> statuses = ProjectStatus.findAllProjectStatuses();
    	List<Topic> topics = Topic.findAllTopics();
    	Graph2Dto graph = new Graph2Dto();
    	for (Topic topic:topics){
			GraphTopicRootDto topicDto = new GraphTopicRootDto();
			topicDto.setName(topic.getName());
    		for (ProjectStatus status:statuses){
    			ProjectStatusDto statusDto = new ProjectStatusDto();
    			statusDto.setName(status.getName());
    			List<Idea> ideas = Idea.findIdeaByStatusAndTopicsContainsTopic(status,topic);
    			for(Idea idea:ideas){
    				ReducedIdeaDto dto = new ReducedIdeaDto();
    				dto.setName(idea.getName());
    				dto.setUuid(idea.getUuid());
    				statusDto.getChildren().add(dto);
    			}
    			if(!statusDto.getChildren().isEmpty())
    				topicDto.getChildren().add(statusDto);
    		}
    		graph.getChildren().add(topicDto);
    	}
    	return new ResponseEntity<Graph2Dto>(graph,HttpStatus.OK);
    }
    @RequestMapping(method = RequestMethod.GET, value = "idea")
    public @ResponseBody ResponseEntity<IdeaDto> getIdea(@RequestParam("uuid") String uuid) {
   		Idea idea = Idea.findIdeasByUuidEquals(uuid).getSingleResult();
   		List<Funding> possibleFundings = Funding.findFundingsByIdea(idea).getResultList();
   		List<FundingDto> fundingsDto = castToDto(possibleFundings);
   		Set<TopicDto> topics = castToDtos(idea.getTopics());
   		IdeaDto dto = new IdeaDto();
   		dto.setUuid(idea.getUuid());
   		dto.setProjectName(idea.getName());
   		dto.setProjectDesc(idea.getDescription());
   		dto.setStatus(idea.getStatus().getName());
   		dto.setTopics(topics);
   		dto.setFundings(fundingsDto);
   		dto.getFileNames().addAll(idea.getFileNames());
    	return new ResponseEntity<IdeaDto>(dto,HttpStatus.OK);
    }
    private Set<TopicDto> castToDtos(Set<Topic> topics) {
    	Set<TopicDto> dtos = new HashSet<TopicDto>();
		for (Topic topic: topics){
			TopicDto dto = new TopicDto(topic.getUuid(), topic.getName(), topic.getColor());
			dtos.add(dto);
		}
		return dtos;
	}
	private List<FundingDto> castToDto(List<Funding> possibleFundings) {
		List<FundingDto> fundings = new ArrayList<FundingDto>();
		for (Funding funding : possibleFundings){
			fundings.add(new FundingDto(funding.getUuid(),funding.getUrl(), funding.getDescription()));
		}
		return fundings;
	}
	@RequestMapping(method = RequestMethod.POST, value = "upload")
    public @ResponseBody ResponseEntity<ResponseObject> uploadFiles(@RequestParam("file")List<MultipartFile> files,@RequestParam("uuid")String uuid,@RequestParam(value="alreadySavedFiles",required=false)String currentFiles) throws JsonParseException, JsonMappingException, IOException {
		if (documentFolder.exists()){
			Idea idea = Idea.findIdeasByUuidEquals(uuid).getSingleResult();
			File directory = new File(documentFolder.getPath()+"/"+uuid);
			directory.mkdirs();
			List<String> current = new ArrayList<String>();
			if (currentFiles != null){
				current = jsonMapper.readValue(currentFiles,ArrayList.class);
				deleteRemovedFiles(uuid,current,directory);
			}
			Set<String> fileNames = new HashSet<String>();
			for (MultipartFile multiPartfile : files){
				fileNames.add(multiPartfile.getOriginalFilename());
				File file = new File(directory,multiPartfile.getOriginalFilename());
				try {
					multiPartfile.transferTo(file);
				} catch (IllegalStateException e) {
					e.printStackTrace();
			    	return new ResponseEntity<ResponseObject>(HttpStatus.INTERNAL_SERVER_ERROR);
				} catch (IOException e) {
					e.printStackTrace();
			    	return new ResponseEntity<ResponseObject>(HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			fileNames.addAll(current);
			idea.setFileNames(fileNames);
			idea.merge();
		}
    	return new ResponseEntity<ResponseObject>(HttpStatus.OK);
    }
	@RequestMapping(method = RequestMethod.GET, value = "files/{uuid}/{file}/{format}")
    public  void getFile(@PathVariable("uuid") String uuid,@PathVariable("file") String fileString,@PathVariable("format") String format,HttpServletResponse response) throws IOException{
		if(documentFolder.exists()){
			File file = new File(documentFolder.getFile(),uuid+"/"+fileString+"."+format);
			response.setHeader("Content-Disposition", "attachment; filename=" + "\"" + file.getName()+ "\"");
			IOUtils.copy(new FileInputStream(file), response.getOutputStream());
		}
	}

    private void deleteRemovedFiles(String uuid, List<String> currentFiles, File directory) {
    	for (File file:directory.listFiles()){
    		if (!currentFiles.contains(file.getName())){
    			file.delete();
    		};
    	};
	}
	@RequestMapping(method = RequestMethod.GET, value = "statuses")
    public @ResponseBody ResponseEntity<List<String>> getStatuses() {
    	List<String> list = new ArrayList<String>();
    	for (ProjectStatus status: ProjectStatus.findAllProjectStatuses())
    		list.add(status.getName());
    	return new ResponseEntity<List<String>>(list,HttpStatus.OK);
    }
    @RequestMapping(method = RequestMethod.GET, value = "topics")
    public @ResponseBody ResponseEntity<List<TopicDto>> getTopics() {
    	List<TopicDto> list = new ArrayList<TopicDto>();
    	for (Topic topic: Topic.findAllTopics("name","asc")){
    		TopicDto dto = new TopicDto();
    		dto.setName(topic.getName());
    		dto.setColor(topic.getColor());
    		dto.setUuid(topic.getUuid());
    		list.add(dto);
    	}
    	return new ResponseEntity<List<TopicDto>>(list,HttpStatus.OK);
    }
    @RequestMapping(method = RequestMethod.POST, value = "topics")
    public @ResponseBody void createTopic(@RequestBody TopicDto dto) {
    	Topic topic = new Topic();
    	topic.setName(dto.getName());
    	topic.setColor(dto.getColor());
    	topic.persist();
    }
    @RequestMapping(method = RequestMethod.PUT, value = "topics")
    public @ResponseBody void updateTopic(@RequestBody TopicDto dto) {
    	Topic topic = Topic.findTopicsByUuidEquals(dto.getUuid()).getSingleResult();
    	topic.setName(dto.getName());
    	topic.setColor(dto.getColor());
    	topic.merge();
    }
    @RequestMapping(method = RequestMethod.DELETE, value = "topics")
    public @ResponseBody ResponseEntity<Object> deleteTopic(@RequestParam("uuid")String uuid) {
    	Topic topic = Topic.findTopicsByUuidEquals(uuid).getSingleResult();
    	List<Idea> ideas = Idea.findIdeaByContainsTopic(topic);
    	if (ideas.size()>0){
    		return new ResponseEntity<Object>(HttpStatus.CONFLICT);
    	}
    	topic.remove();
    	return new ResponseEntity<Object>(HttpStatus.OK);
    }
}
