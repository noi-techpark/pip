package it.bz.tis.alpenstaedte;
import javax.persistence.ManyToOne;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class Funding {
	public Funding() {
	}
	public Funding(String url, String description, Idea idea) {
		this.url = url;
		this.description = description;
		this.idea = idea;
	}
	private String url;
	private String description;
	@ManyToOne
	private Idea idea;
}
