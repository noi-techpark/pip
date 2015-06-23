package it.bz.tis.alpenstaedte;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.ElementCollection;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class Idea {
	private String name;
	private String description;
	private String uuid;

	
	@ElementCollection
	private List<String> topics = new ArrayList<String>();
	
	public Idea() {
		this.uuid= UUID.randomUUID().toString();
	}
	public Idea(String projectName, String projectDesc, List<String> topics) {
		this.uuid= UUID.randomUUID().toString();
		this.name = projectName;
		this.description = projectDesc;
		this.topics = topics;
	}
}
