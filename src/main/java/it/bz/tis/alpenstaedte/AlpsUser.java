package it.bz.tis.alpenstaedte;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.ManyToMany;

import org.hibernate.validator.constraints.Email;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders={"findAlpsUsersByEmailEquals"})
public class AlpsUser {
	private String uuid = UUID.randomUUID().toString();
	@Email
	private String email;
	private String password;
	private String role;
	private String name;
	private String surname;
	private String phone;
	
	@ManyToMany
	private List<Organisazion> organisazions = new ArrayList<Organisazion>();
	
	@ManyToMany
	private Set<Topic> preferredTopics = new HashSet<Topic>();
}
