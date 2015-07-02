package it.bz.tis.alpenstaedte;
import it.bz.tis.alpenstaedte.dto.UserDto;
import it.bz.tis.alpenstaedte.util.DALCastUtil;
import it.bz.tis.alpenstaedte.util.DtoCastUtil;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/user/**")
@Controller
public class UserController {
	
	@Autowired
	private FileSystemResource documentFolder;
	
	@Autowired
	private PasswordEncoder encoder;
	
	@Secured(value={"ROLE_ADMIN"})
    @RequestMapping(method = RequestMethod.GET,value="list")
    public @ResponseBody ResponseEntity<List<UserDto>> getUsers() {
    	List<UserDto> list = new ArrayList<UserDto>();
    	for (AlpsUser user: AlpsUser.findAllAlpsUsers("name","asc")){
    		UserDto dto = new UserDto();
    		dto.setEmail(user.getEmail());
    		list.add(dto);
    	}
    	return new ResponseEntity<List<UserDto>>(list,HttpStatus.OK);
    }
	@Secured(value={"ROLE_ADMIN","ROLE_USER"})
    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<UserDto> getUser(Principal principal) {
    	AlpsUser user = AlpsUser.findAlpsUsersByEmailEquals(principal.getName()).getSingleResult();
    	UserDto dto = new UserDto();
    	dto.setEmail(user.getEmail());
    	dto.setName(user.getName());
    	dto.setSurname(user.getSurname());
    	dto.setPhone(user.getPhone());
    	dto.setTopics(DtoCastUtil.cast(user.getPreferredTopics()));
    	return new ResponseEntity<UserDto>(dto,HttpStatus.OK);
    }
	@Secured(value={"ROLE_ADMIN","ROLE_USER"})
    @RequestMapping(method = RequestMethod.PUT)
    public @ResponseBody ResponseEntity<UserDto> updateUser(@RequestBody UserDto dto, Principal principal) {
    	AlpsUser user = AlpsUser.findAlpsUsersByEmailEquals(principal.getName()).getSingleResult();
    	if (!user.getEmail().equals(principal.getName()))
    		return new ResponseEntity<UserDto>(HttpStatus.FORBIDDEN);
    	user.setName(dto.getName());
    	user.setSurname(dto.getSurname());
    	user.setPreferredTopics(DALCastUtil.cast(dto.getTopics()));
    	user.setPhone(dto.getPhone());
    	user.merge();
    	return new ResponseEntity<UserDto>(HttpStatus.OK);
    }
	@Secured(value={"ROLE_ADMIN"})
    @RequestMapping(method = RequestMethod.DELETE)
    public @ResponseBody ResponseEntity<Object> deleteUser(@RequestParam("email")String email) {
    	AlpsUser user = AlpsUser.findAlpsUsersByEmailEquals(email).getSingleResult();
    	user.remove();
    	return new ResponseEntity<Object>(HttpStatus.OK);
    }
	@Secured(value={"ROLE_ADMIN"})
    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody void createUser(@RequestBody UserDto dto) {
    	AlpsUser user = new AlpsUser();
    	user.setEmail(dto.getEmail());
    	String randomPassword = RandomStringUtils.randomAlphanumeric(5);
		user.setPassword(encoder.encode(randomPassword));
    	user.setRole("USER");
    	user.persist();
    }
}
