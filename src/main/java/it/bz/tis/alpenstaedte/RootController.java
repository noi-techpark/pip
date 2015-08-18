package it.bz.tis.alpenstaedte;
import it.bz.tis.alpenstaedte.dto.CommentDto;
import it.bz.tis.alpenstaedte.dto.FundingDto;
import it.bz.tis.alpenstaedte.dto.GraphTopicDto;
import it.bz.tis.alpenstaedte.dto.GraphTopicRootDto;
import it.bz.tis.alpenstaedte.dto.IdeaDto;
import it.bz.tis.alpenstaedte.dto.NewIdeaDto;
import it.bz.tis.alpenstaedte.dto.ProjectStatusDto;
import it.bz.tis.alpenstaedte.dto.ReducedIdeaDto;
import it.bz.tis.alpenstaedte.dto.ResponseObject;
import it.bz.tis.alpenstaedte.dto.StatusIdeasDto;
import it.bz.tis.alpenstaedte.dto.TopicDto;
import it.bz.tis.alpenstaedte.util.DtoCastUtil;
import it.bz.tis.alpenstaedte.util.MailingUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Principal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.security.access.annotation.Secured;
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
	
	@Autowired
	private MailingUtil mailingUtil;
	@Secured(value={"ROLE_USER", "ROLE_ADMIN","ROLE_MANAGER"})
	@RequestMapping(value="principal")
	public @ResponseBody ResponseEntity<Principal> getPrincipal(Principal principal){
		return new ResponseEntity<Principal>(principal, HttpStatus.OK);
	}
	@Secured(value={"ROLE_USER", "ROLE_ADMIN","ROLE_MANAGER"})
	@RequestMapping(value="idea/is-owner")
	public @ResponseBody ResponseEntity<Boolean> isOwner(Principal principal,@RequestParam("uuid") String uuid){
    	PipUser currentUser = PipUser.findPipUsersByEmailEquals(principal.getName()).getSingleResult();
    	Idea idea = Idea.findIdeasByUuidEquals(uuid).getSingleResult();
    	boolean isOwner = (currentUser.getUuid().equals(idea.getOwner().getUuid()));
		return new ResponseEntity<Boolean>(isOwner, HttpStatus.OK);
	}
	
	@Secured(value={"ROLE_USER", "ROLE_ADMIN","ROLE_MANAGER"})
    @RequestMapping(method = RequestMethod.POST, value = "create")
    public @ResponseBody ResponseEntity<ResponseObject> create(@RequestBody NewIdeaDto dto,Principal principal) {
    	Set<Topic> topics = new HashSet<Topic>();
    	for(TopicDto topicDto :dto.getTopics()){
    		Topic topic = Topic.findTopicsByNameEquals(topicDto.getName()).getSingleResult();
   			topics.add(topic);
    	}
    	ProjectStatus status = ProjectStatus.findProjectStatusesByNameEquals("idea").getSingleResult();
    	PipUser currentUser = PipUser.findPipUsersByEmailEquals(principal.getName()).getSingleResult();
    	Idea idea = new Idea(dto.getProjectName(),dto.getProjectDesc(),topics,status);
    	Set<Funding> fundings = new HashSet<Funding>();
    	for (FundingDto fundingDto:dto.getFundings()){
			Funding funding = new Funding();
			funding.setIdea(idea);
    		funding.setUrl(fundingDto.getUrl());
    		funding.setDescription(fundingDto.getDescription());
    		funding.setCofinance(fundingDto.getCofinance());
    		try {
				funding.setDeadline(DtoCastUtil.formatter.parse(fundingDto.getDeadline()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
    	}
		idea.setMeasures(dto.getMeasures());
		idea.setObjectives(dto.getObjectives());
		idea.setOutputs(dto.getOutputs());
		idea.setTarget(dto.getTarget());
		idea.setBudget(dto.getBudget());
    	idea.setFundings(fundings);
    	idea.setOwner(currentUser);
    	idea.getFollower().add(currentUser);
    	idea.getInterestedOrganisations().add(currentUser.getOrganisazions().get(0));
    	idea.persist();
		Set<PipUser> users = PipUser.getUserByInterestedTopics(idea);
		users.remove(currentUser);
		String[] mails = PipUser.getMailsFromUsers(users);
    	mailingUtil.sendCreationMail(idea,mails);
    	return new ResponseEntity<ResponseObject>(new ResponseObject(idea.getUuid()),HttpStatus.OK);
    }

	@Secured(value={"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    @RequestMapping(method = RequestMethod.DELETE, value = "delete/{uuid}")
    public @ResponseBody ResponseEntity<ResponseObject> delete(@PathVariable("uuid") String uuid,Principal principal) throws IOException {
    	Idea idea = Idea.findIdeasByUuidEquals(uuid).getSingleResult();
    	PipUser currentUser = PipUser.findPipUsersByEmailEquals(principal.getName()).getSingleResult();
    	if (!idea.getOwner().equals(currentUser) && !PipRole.ADMIN.equals(currentUser.getRole()))
    		return new ResponseEntity<ResponseObject>(HttpStatus.FORBIDDEN);
    	idea.remove();
    	if (documentFolder.exists()){
    		File ideaDirectory = new File(documentFolder.getPath()+"/"+uuid);
    		if (ideaDirectory.isDirectory()){
    			FileUtils.deleteDirectory(ideaDirectory);
    		}
    	}
    	return new ResponseEntity<ResponseObject>(HttpStatus.OK);
    }
	@Secured(value={"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
	@RequestMapping(method = RequestMethod.POST, value = "update")
	public @ResponseBody ResponseEntity<ResponseObject> update(@RequestBody IdeaDto dto,Principal principal) {
		Set<Topic> topics = new HashSet<Topic>();
		for(TopicDto topicDto:dto.getTopics()){
			Topic topic = Topic.findTopicsByNameEquals(topicDto.getName()).getSingleResult();
			topics.add(topic);
		}
		ProjectStatus status = ProjectStatus.findProjectStatusesByNameEquals(dto.getStatus()).getSingleResult();
		Idea idea = Idea.findIdeasByUuidEquals(dto.getUuid()).getSingleResult();
		PipUser currentUser = PipUser.findPipUsersByEmailEquals(principal.getName()).getSingleResult();
		if (idea.getOwner().equals(currentUser) || PipRole.ADMIN.getName().equals(currentUser.getRole())){
			idea.setName(dto.getProjectName());
			idea.setDescription(dto.getProjectDesc());
			idea.setMeasures(dto.getMeasures());
			idea.setObjectives(dto.getObjectives());
			idea.setOutputs(dto.getOutputs());
			idea.setTarget(dto.getTarget());
			idea.setBudget(dto.getBudget());
			idea.setStatus(status);
			idea.setTopics(topics);
			idea.setUpdated_on(new Date());
			idea.setFileNames(dto.getFileNames());
			for (Funding funding:idea.getFundings()){
				boolean toDelete = true;
				for  (FundingDto fDto : dto.getFundings()){
					if (fDto.getUuid()!=null && fDto.getUuid().equals(funding.getUuid()))
						toDelete = false;
				}
				if (toDelete){
					idea.getFundings().remove(funding);
					funding.remove();
				}
			}
			for (FundingDto fDto : dto.getFundings()){
				Funding f;
				if (fDto.getUuid() != null){
					f = Funding.findFundingsByUuid(fDto.getUuid()).getSingleResult();
					f.setUrl(fDto.getUrl());
					f.setDescription(fDto.getDescription());
					f.setCofinance(fDto.getCofinance());
					try {
						f.setDeadline(DtoCastUtil.formatter.parse(fDto.getDeadline()));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					f.merge();
				}
				else{
					f = new Funding(fDto.getUrl(),fDto.getDescription(),idea,fDto.getCofinance(),fDto.getDeadline());
					f.persist();
					idea.getFundings().add(f);
				}
			}
			idea.merge();
			Set<PipUser> users = PipUser.getUserByOwnerAndCommenterAndOrganisazion(idea);
			users.remove(currentUser);
			String[] mails = PipUser.getMailsFromUsers(users);
			mailingUtil.sendUpdateMail(idea,mails);
			return new ResponseEntity<ResponseObject>(new ResponseObject(idea.getUuid()),HttpStatus.OK);
		}else
			return new ResponseEntity<ResponseObject>(HttpStatus.FORBIDDEN);
    }
	@Secured(value={"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    @RequestMapping(method = RequestMethod.GET, value = "myideas")
    public @ResponseBody ResponseEntity<List<NewIdeaDto>> getMyIdeas(Principal principal) {
    	PipUser currentUser = PipUser.findPipUsersByEmailEquals(principal.getName()).getSingleResult();
    	List<Idea> ideas = Idea.findIdeasByOwner(currentUser,"name","ASC").getResultList();
		List<NewIdeaDto> list = DtoCastUtil.castIdeaList(ideas);
    	return new ResponseEntity<List<NewIdeaDto>>(list,HttpStatus.OK);
    }
	@Secured(value={"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    @RequestMapping(method = RequestMethod.GET, value = "myfavorites")
    public @ResponseBody ResponseEntity<List<NewIdeaDto>> getMyFavorites(Principal principal) {
    	PipUser currentUser = PipUser.findPipUsersByEmailEquals(principal.getName()).getSingleResult();
    	List<Idea> ideas = Idea.findIdeasFollowed(currentUser);
		List<NewIdeaDto> list = DtoCastUtil.castIdeaList(ideas);
    	return new ResponseEntity<List<NewIdeaDto>>(list,HttpStatus.OK);
    }
	@Secured(value={"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    @RequestMapping(method = RequestMethod.GET, value = "ideas")
    public @ResponseBody ResponseEntity<List<NewIdeaDto>> getIdeas() {
    	List<NewIdeaDto> list = DtoCastUtil.castIdeaList(Idea.findAllIdeas());
    	return new ResponseEntity<List<NewIdeaDto>>(list,HttpStatus.OK);
    }
	@Secured(value={"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    @RequestMapping(method = RequestMethod.GET, value = "idea/{uuid}/follow")
    public @ResponseBody void followIdea(@PathVariable("uuid")String uuid, Principal principal) {
    	Idea idea = Idea.findIdeasByUuidEquals(uuid).getSingleResult();
		PipUser currentUser = PipUser.findPipUsersByEmailEquals(principal.getName()).getSingleResult();
    	Organisazion organisazion = currentUser.getOrganisazions().get(0);
    	idea.getInterestedOrganisations().add(organisazion);
    	idea.getFollower().add(currentUser);
    	idea.merge();
    }
	@Secured(value={"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    @RequestMapping(method = RequestMethod.GET, value = "idea/{uuid}/unfollow")
    public @ResponseBody void unfollowIdea(@PathVariable("uuid")String uuid, Principal principal) {
    	Idea idea = Idea.findIdeasByUuidEquals(uuid).getSingleResult();
		PipUser currentUser = PipUser.findPipUsersByEmailEquals(principal.getName()).getSingleResult();
    	Organisazion organisazion = currentUser.getOrganisazions().get(0);
    	idea.getFollower().remove(currentUser);
    	boolean orgFollows = false;
    	for(PipUser user : idea.getFollower()){
    		if (!user.getOrganisazions().get(0).equals(organisazion))
    			continue;
    		orgFollows = true;
    	}
    	if (!orgFollows){
        	idea.getInterestedOrganisations().remove(organisazion);
    	}
    	idea.merge();
    }
	@Secured(value={"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    @RequestMapping(method = RequestMethod.GET, value = "graph-data")
    public @ResponseBody ResponseEntity <GraphTopicRootDto> getGraphData() {
    	List<ProjectStatus> statuses = ProjectStatus.findAllProjectStatuses();
    	GraphTopicRootDto root = new GraphTopicRootDto();

    	for (ProjectStatus status:statuses){
    		ProjectStatusDto statusDto = new ProjectStatusDto();
    		statusDto.setName(status.getName());
    		List<Idea> ideas = Idea.findIdeasByStatus(status,"name","ASC").getResultList();
    		for(Idea idea:ideas){
				ReducedIdeaDto dto = new ReducedIdeaDto();
				dto.setName(idea.getName());
				dto.setUuid(idea.getUuid());
				statusDto.getChildren().add(dto);
			}
    		if (!statusDto.getChildren().isEmpty())
    			root.getChildren().add(statusDto);
    	}
    	return new ResponseEntity<GraphTopicRootDto>(root,HttpStatus.OK);
    }
	@Secured(value={"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    @RequestMapping(method = RequestMethod.GET, value = "graph-data-topics")
    public @ResponseBody ResponseEntity <StatusIdeasDto> getGraphDataByTopics() {
    	List<Topic> topics = Topic.findAllTopics();
    	StatusIdeasDto graph = new StatusIdeasDto();
    	for (Topic topic:topics){
    		GraphTopicDto topicDto = new GraphTopicDto();
			topicDto.setName(topic.getName());
			List<Idea> ideas = Idea.findIdeaByContainsTopic(topic);
			for(Idea idea:ideas){
				ReducedIdeaDto dto = new ReducedIdeaDto();
				dto.setName(idea.getName());
				dto.setUuid(idea.getUuid());
				topicDto.getChildren().add(dto);
			}
			if (!topicDto.getChildren().isEmpty())
    			graph.getChildren().add(topicDto);	
    	}
    	return new ResponseEntity<StatusIdeasDto>(graph,HttpStatus.OK);
    }
	@Secured(value={"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    @RequestMapping(method = RequestMethod.GET, value = "idea")
    public @ResponseBody ResponseEntity<IdeaDto> getIdea(@RequestParam("uuid") String uuid) {
   		Idea idea = Idea.findIdeasByUuidEquals(uuid).getSingleResult();
   		List<FundingDto> fundingsDto = DtoCastUtil.castFundings(idea.getFundings());
   		Set<TopicDto> topics = DtoCastUtil.cast(idea.getTopics());
   		List<CommentDto> comments = DtoCastUtil.cast(idea.getComments());
   		IdeaDto dto = new IdeaDto();
   		dto.setUuid(idea.getUuid());
   		dto.setProjectName(idea.getName());
   		dto.setProjectDesc(idea.getDescription());
   		dto.setStatus(idea.getStatus().getName());
   		dto.setTopics(topics);
   		dto.setFundings(fundingsDto);
   		dto.setComments(comments);
   		dto.setAuthor(DtoCastUtil.cast(idea.getOwner()));
   		dto.setInterestedOrganisazions(DtoCastUtil.castOrgs(new ArrayList<Organisazion>(idea.getInterestedOrganisations())));
   		dto.setFollowers(DtoCastUtil.castUser(new ArrayList<PipUser>(idea.getFollower())));
   		dto.getFileNames().addAll(idea.getFileNames());
   		dto.setCreated_on(idea.getCreated_on());
   		dto.setBudget(idea.getBudget());
   		dto.setObjectives(idea.getObjectives());
   		dto.setOutputs(idea.getOutputs());
   		dto.setMeasures(idea.getMeasures());
   		dto.setTarget(idea.getTarget());
    	return new ResponseEntity<IdeaDto>(dto,HttpStatus.OK);
    }

	@Secured(value={"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
	@RequestMapping(method = RequestMethod.POST, value = "upload")
    public @ResponseBody ResponseEntity<ResponseObject> uploadFiles(@RequestParam("file")List<MultipartFile> files,@RequestParam("uuid")String uuid,@RequestParam(value="alreadySavedFiles",required=false)String currentFiles) throws JsonParseException, JsonMappingException, IOException {
		if (documentFolder.exists()){
			Idea idea = Idea.findIdeasByUuidEquals(uuid).getSingleResult();
			File directory = new File(documentFolder.getPath()+"/attachements/"+uuid);
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

	@Secured(value={"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
	@RequestMapping(method = RequestMethod.GET, value = "files/{uuid}/{file}/{format}")
    public  void getFile(@PathVariable("uuid") String uuid,@PathVariable("file") String fileString,@PathVariable("format") String format,HttpServletResponse response) throws IOException{
		if(documentFolder.exists()){
			File file = new File(documentFolder.getFile(),"attachements/"+uuid+"/"+fileString+"."+format);
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
	@Secured(value={"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
	@RequestMapping(method = RequestMethod.GET, value = "statuses")
    public @ResponseBody ResponseEntity<List<String>> getStatuses() {
    	List<String> list = new ArrayList<String>();
    	for (ProjectStatus status: ProjectStatus.findAllProjectStatuses("position","asc"))
    		list.add(status.getName());
    	return new ResponseEntity<List<String>>(list,HttpStatus.OK);
    }
	@Secured(value={"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
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
	@Secured(value={"ROLE_ADMIN"})
    @RequestMapping(method = RequestMethod.POST, value = "topics")
    public @ResponseBody void createTopic(@RequestBody TopicDto dto) {
    	Topic topic = new Topic();
    	topic.setName(dto.getName());
    	topic.setColor(dto.getColor());
    	topic.persist();
    }
	@Secured(value={"ROLE_ADMIN"})
    @RequestMapping(method = RequestMethod.PUT, value = "topics")
    public @ResponseBody void updateTopic(@RequestBody TopicDto dto) {
    	Topic topic = Topic.findTopicsByUuidEquals(dto.getUuid()).getSingleResult();
    	topic.setName(dto.getName());
    	topic.setColor(dto.getColor());
    	topic.merge();
    }
	@Secured(value={"ROLE_ADMIN"})
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
	@Secured(value={"ROLE_ADMIN","ROLE_USER", "ROLE_MANAGER"})
    @RequestMapping(method = RequestMethod.POST, value = "idea/comment/{uuid}")
    public @ResponseBody ResponseEntity<CommentDto> comment(@RequestBody String commentString,@PathVariable("uuid")String ideaId,Principal principal) {
    	PipUser currentUser = PipUser.findPipUsersByEmailEquals(principal.getName()).getSingleResult();
		Idea idea = Idea.findIdeasByUuidEquals(ideaId).getSingleResult();
		Comment comment = new Comment();
		comment.setText(commentString);
		comment.setOwner(currentUser);
		comment.setIdea(idea);
		comment.persist();
		Set<PipUser> users = PipUser.getUserByOwnerAndCommenterAndOrganisazion(idea);
		users.remove(currentUser);
		String[] mails = PipUser.getMailsFromUsers(users);
		mailingUtil.sendCommentMail(comment,mails);
		return new ResponseEntity<CommentDto>(DtoCastUtil.cast(comment),HttpStatus.OK);
    }
	@Secured(value={"ROLE_ADMIN"})
    @RequestMapping(method = RequestMethod.GET, value = "idea/comment/{uuid}/block")
    public @ResponseBody void blockComment(@PathVariable("uuid") String uuid) {
		Comment comment = Comment.findCommentsByUuid(uuid).getSingleResult();
		comment.setBanned(true);
		comment.merge();
    }
	@Secured(value={"ROLE_ADMIN"})
    @RequestMapping(method = RequestMethod.GET, value = "idea/comment/{uuid}/unblock")
    public @ResponseBody void unblockComment(@PathVariable("uuid") String uuid) {
		Comment comment = Comment.findCommentsByUuid(uuid).getSingleResult();
		comment.setBanned(false);
		comment.merge();
    }
	@Secured(value={"ROLE_ADMIN"})
    @RequestMapping(method = RequestMethod.DELETE, value = "idea/comment/{uuid}")
    public @ResponseBody void deleteComment(@PathVariable("uuid") String uuid) {
		Comment comment = Comment.findCommentsByUuid(uuid).getSingleResult();
		comment.remove();
    }
	
	
}
