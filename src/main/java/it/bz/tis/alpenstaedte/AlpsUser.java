package it.bz.tis.alpenstaedte;

import org.hibernate.validator.constraints.Email;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders={"findAlpsUsersByEmailEquals"})
public class AlpsUser {
	@Email
	private String email;
	private String password;
	private String role;
}
