package it.bz.tis.alpenstaedte;
import java.util.Date;

import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class Comment {
	
	@Size(max=50000)
	private String text;
	@ManyToOne
	private AlpsUser owner;
	
	@ManyToOne
	private Idea idea;
	private Date created_on;
	private Date updated_on;
	
	public Comment() {
		Date date = new Date();
		this.created_on = date;
		this.updated_on = date;
	}
}
