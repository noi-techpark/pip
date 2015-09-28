package it.bz.tis.alpenstaedte;
import it.bz.tis.alpenstaedte.dto.OrganisazionDto;
import it.bz.tis.alpenstaedte.dto.ResponseObject;
import it.bz.tis.alpenstaedte.dto.UserDto;
import it.bz.tis.alpenstaedte.util.DALCastUtil;
import it.bz.tis.alpenstaedte.util.DtoCastUtil;
import it.bz.tis.alpenstaedte.util.MailingUtil;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/user/**")
@Controller
public class UserController {
	
	@Autowired
	private FileSystemResource documentFolder;
	
	@Autowired
	private MailingUtil mailingUtil;

	@Autowired
	private PasswordEncoder encoder;
	
	@Secured(value={"ROLE_ADMIN","ROLE_MANAGER", "ROLE_MANAGER"})
    @RequestMapping(method = RequestMethod.GET,value="list")
    public @ResponseBody ResponseEntity<List<UserDto>> getUsers(Principal principal) {
    	PipUser prince = PipUser.findPipUsersByEmailEquals(principal.getName()).getSingleResult();
    	
    	List<UserDto> list = new ArrayList<UserDto>();
    	List<PipUser> users;
    	if (PipRole.ADMIN.getName().equals(prince.getRole()))
    		users = PipUser.findAllPipUsers("name","asc");
    	else{
    		users = PipUser.findPipUserByOrganisazionAndRole(prince.getOrganisazions().get(0),PipRole.USER.getName());
    	}
    	list = DtoCastUtil.castUser(users);
    	return new ResponseEntity<List<UserDto>>(list,HttpStatus.OK);
    }
	@Secured(value={"ROLE_ADMIN","ROLE_USER", "ROLE_MANAGER"})
    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<UserDto> getUser(Principal principal,@RequestParam(value="uuid",required=false)String uuid) {
    	PipUser user;
    	if(uuid!=null)
    		user = PipUser.findPipUsersByUuidEquals(uuid).getSingleResult();
    	else
    		user = PipUser.findPipUsersByEmailEquals(principal.getName()).getSingleResult();
    	UserDto dto = DtoCastUtil.cast(user);
    	return new ResponseEntity<UserDto>(dto,HttpStatus.OK);
    }
	@Secured(value={"ROLE_ADMIN","ROLE_USER", "ROLE_MANAGER"})
    @RequestMapping(method = RequestMethod.PUT)
    public @ResponseBody ResponseEntity<UserDto> updateUser(@RequestBody UserDto dto, Principal principal,@RequestParam(value="user-id",required=false)String uuid) {
		PipUser user = PipUser.findPipUsersByUuidEquals(uuid).getSingleResult();
		PipUser principalUser = PipUser.findPipUsersByEmailEquals(
				principal.getName()).getSingleResult();
		if (user.getEmail().equals(principal.getName())	|| PipRole.ADMIN.getName().equals(principalUser.getRole())) {
			user.setName(dto.getName());
			user.setSurname(dto.getSurname());
			user.setPreferredTopics(DALCastUtil.cast(dto.getTopics()));
			user.setPhone(dto.getPhone());
			user.setLanguageSkills(dto.getLanguageSkills());
			user.merge();
			return new ResponseEntity<UserDto>(HttpStatus.OK);
		} else
			return new ResponseEntity<UserDto>(HttpStatus.FORBIDDEN);

    }
	@Secured(value={"ROLE_ADMIN","ROLE_MANAGER"})
    @RequestMapping(method = RequestMethod.DELETE)
    public @ResponseBody ResponseEntity<Object> deleteUser(@RequestParam("email")String email,Principal principal) {
    	PipUser user = PipUser.findPipUsersByEmailEquals(email).getSingleResult();
    	PipUser currentUser = PipUser.findPipUsersByEmailEquals(principal.getName()).getSingleResult();
    	if (PipRole.MANAGER.equals(currentUser.getRole()) && !currentUser.organisationMatches(user))
        	return new ResponseEntity<Object>(HttpStatus.FORBIDDEN);
    	if (!PipRole.ADMIN.getName().equals(user.getRole()))
    		user.remove();
    	return new ResponseEntity<Object>(HttpStatus.OK);
    }
	@Secured(value={"ROLE_ADMIN","ROLE_MANAGER"})
    @RequestMapping(method = RequestMethod.GET,value="deactivate")
    public @ResponseBody ResponseEntity<Object> deactivateUser(@RequestParam("email")String email,Principal principal) {
    	PipUser user = PipUser.findPipUsersByEmailEquals(email).getSingleResult();
    	PipUser currentUser = PipUser.findPipUsersByEmailEquals(principal.getName()).getSingleResult();
    	if (PipRole.MANAGER.equals(currentUser.getRole()) && !currentUser.organisationMatches(user))
        	return new ResponseEntity<Object>(HttpStatus.FORBIDDEN);
    	if (!PipRole.ADMIN.getName().equals(user.getRole())){
    		user.setActive(false);
    		user.merge();
    	}
    	return new ResponseEntity<Object>(HttpStatus.OK);
    }
	@Secured(value={"ROLE_ADMIN","ROLE_MANAGER"})
    @RequestMapping(method = RequestMethod.GET,value="activate")
    public @ResponseBody ResponseEntity<Object> activateUser(@RequestParam("email")String email,Principal principal) {
    	PipUser user = PipUser.findPipUsersByEmailEquals(email).getSingleResult();
    	PipUser currentUser = PipUser.findPipUsersByEmailEquals(principal.getName()).getSingleResult();
    	if (PipRole.MANAGER.equals(currentUser.getRole()) && !currentUser.organisationMatches(user))
        	return new ResponseEntity<Object>(HttpStatus.FORBIDDEN);
    	if (!PipRole.ADMIN.getName().equals(user.getRole())){
    		user.setActive(true);
    		user.merge();
    	}
    	return new ResponseEntity<Object>(HttpStatus.OK);
    }
	@Secured(value={"ROLE_ADMIN","ROLE_MANAGER"})
    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody void createUser(@RequestBody UserDto dto,Principal principal) {
    	PipUser user = new PipUser();
    	user.setEmail(dto.getEmail());
    	Set<OrganisazionDto> organizations = dto.getOrganizations();
		if (organizations.isEmpty()){
    		PipUser currentUser = PipUser.findPipUsersByEmailEquals(principal.getName()).getSingleResult();
    		List<Organisazion> organisazions = currentUser.getOrganisazions();
    		if (!organisazions.isEmpty()){
    			user.getOrganisazions().add(organisazions.get(0));
    		}
    	}else{
    		Organisazion organisazion = Organisazion.findOrganisazionsByName(new ArrayList<OrganisazionDto>(organizations).get(0).getName()).getSingleResult();
    		user.getOrganisazions().add(organisazion);
    	}
    		
    	String randomPassword = RandomStringUtils.randomAlphanumeric(6);
		user.setPassword(encoder.encode(randomPassword));
    	user.setRole(PipRole.USER.getName());
    	user.persist();
    	mailingUtil.sendCreationMail(user,randomPassword);
    }
	
	@Secured(value={"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
	@RequestMapping(method = RequestMethod.GET, value = "profile-pic")
    public  @ResponseBody FileSystemResource getFile(@RequestParam(required=false,value="user") String userid, Principal principal,HttpSession session) throws IOException{
		if(documentFolder.exists()){
			String uuid;
			if (userid != null)
				uuid = userid;
			else{
				PipUser user = PipUser.findPipUsersByEmailEquals(principal.getName()).getSingleResult();
				uuid = user.getUuid();
			}
			File folder = new File(documentFolder.getFile(),"user-data/"+uuid);
			File file;
			if (!uuid.isEmpty() && folder.exists() && folder.listFiles().length > 0)
				file = folder.listFiles()[0];
			else{
				file = new ServletContextResource(session.getServletContext(),"/images/profile.jpg").getFile();
			}
			return new FileSystemResource(file);
		}
		return null;
	}
	@Secured(value={"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
	@RequestMapping(method = RequestMethod.POST, value = "upload-profile-pic")
    public @ResponseBody ResponseEntity<ResponseObject> uploadProfilePic(@RequestParam("file")List<MultipartFile> files,Principal principal,@RequestParam(value = "userid",required=false) String userid) {
		if (documentFolder.exists()){
			PipUser user;
			PipUser principalUser = PipUser.findPipUsersByEmailEquals(principal.getName()).getSingleResult();
			if (userid != null){
				user = PipUser.findPipUsersByUuidEquals(userid).getSingleResult();
				if (! PipRole.ADMIN.getName().equals(principalUser.getRole()))
					return new ResponseEntity<ResponseObject>(HttpStatus.FORBIDDEN);
			}
			else
				user = principalUser;
			File directory = new File(documentFolder.getPath()+"/user-data/"+user.getUuid());
			directory.mkdirs();
			for (File file : directory.listFiles()){
				file.delete();
			}
			for (MultipartFile multiPartfile : files){
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
	    	return new ResponseEntity<ResponseObject>(HttpStatus.OK);
		}
    	return new ResponseEntity<ResponseObject>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
	@Secured(value={"ROLE_ADMIN"})
	@RequestMapping(method = RequestMethod.PUT, value = "user/promote")
    public  @ResponseBody void promote(@RequestBody String email) throws IOException{
		PipUser user = PipUser.findPipUsersByEmailEquals(email).getSingleResult();
		user.setRole(PipRole.MANAGER.getName());
		user.merge();
	}
	@Secured(value={"ROLE_ADMIN"})
	@RequestMapping(method = RequestMethod.PUT, value = "user/demote")
    public  @ResponseBody void demote(@RequestBody String email) throws IOException{
		PipUser user = PipUser.findPipUsersByEmailEquals(email).getSingleResult();
		if (user.getRole()!=PipRole.ADMIN.getName()){
			user.setRole(PipRole.USER.getName());
			user.merge();
		}
	}
	@Secured(value={"ROLE_ADMIN","ROLE_MANAGER","ROLE_USER"})
    @RequestMapping(method = RequestMethod.GET,value="organizations")
    public @ResponseBody ResponseEntity<Set<OrganisazionDto>> getOrganisations() {
    	List<Organisazion> organisazions = Organisazion.findAllOrganisazions("name","ASC");
    	Set<OrganisazionDto> dtos = DtoCastUtil.castOrgs(organisazions);
    	return new ResponseEntity<Set<OrganisazionDto>>(dtos,HttpStatus.OK);
    }
	@Secured(value={"ROLE_ADMIN"})
    @RequestMapping(method = RequestMethod.PUT,value="organization")
    public @ResponseBody void updateOrganisation(@RequestBody UserDto userDto) {
		PipUser user = PipUser.findPipUsersByEmailEquals(userDto.getEmail()).getSingleResult();
		OrganisazionDto dto = new ArrayList<OrganisazionDto>(userDto.getOrganizations()).get(0);
		Organisazion organisazion = Organisazion.findOrganisazionsByName(dto.getName()).getSingleResult();
		user.getOrganisazions().clear();
		user.getOrganisazions().add(organisazion);
		user.merge();
    }
	@Secured(value={"ROLE_ADMIN","ROLE_MANAGER","ROLE_USER"})
    @RequestMapping(method = RequestMethod.GET,value="user-by-topics")
    public @ResponseBody ResponseEntity<List<UserDto>> getUserByTopics() {
			List<PipUser> user = PipUser.findAllPipUsers();
			List<UserDto> userDtos = DtoCastUtil.castUser(user);
    	return new ResponseEntity<List<UserDto>>(userDtos,HttpStatus.OK);
    }
	@Secured(value={"ROLE_ADMIN","ROLE_MANAGER","ROLE_USER"})
    @RequestMapping(method = RequestMethod.GET,value="reset-password")
    public @ResponseBody ResponseEntity<Object> resetPassword(Principal principal,@RequestParam("oldpw")String oldPassword,@RequestParam("newpw")String newPassword) {
		PipUser user = PipUser.findPipUsersByEmailEquals(principal.getName()).getSingleResult();
		if (!encoder.matches(oldPassword, user.getPassword()))
			return new ResponseEntity<Object>(HttpStatus.FORBIDDEN);
		user.setPassword(encoder.encode(newPassword));
		user.merge();
		return new ResponseEntity<Object>(HttpStatus.OK);
    }
    @RequestMapping(method = RequestMethod.GET,value="request-new-pw")
    public String requestPassword(@RequestParam("email") String email, ModelMap model) {
		List<PipUser> resultList = PipUser.findPipUsersByEmailEquals(email).getResultList();
		boolean userExists = !resultList.isEmpty();
		if (!userExists)
			model.addAttribute("error", "User already exists");
		else{
			PipUser user = resultList.get(0);
	    	String randomPassword = RandomStringUtils.randomAlphanumeric(6);
			user.setPassword(encoder.encode(randomPassword));
			user.merge();
			mailingUtil.sendCreationMail(user, randomPassword);
		}
		return "redirect:/";
    }
    @Secured(value={"ROLE_ADMIN","ROLE_MANAGER","ROLE_USER"})
    @RequestMapping(method = RequestMethod.GET,value="like")
    public @ResponseBody void toggleLike(Principal principal, @RequestParam("comment") String uuid){
    	Comment comment = Comment.findCommentsByUuid(uuid).getSingleResult();
		PipUser user = PipUser.findPipUsersByEmailEquals(principal.getName()).getSingleResult();
    	if (comment.getLiker().contains(user))
    			comment.getLiker().remove(user);
    	else
    		comment.getLiker().add(user);
    	comment.merge();
    }
    
}
