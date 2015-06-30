package it.bz.tis.alpenstaedte;
import java.util.Date;
import java.util.UUID;

import org.springframework.roo.addon.equals.RooEquals;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = { "findTopicsByNameEquals","findTopicsByUuidEquals" })
@RooEquals(excludeFields={"color","uuid","createDate"})
public class Topic {

	private String uuid;
    private String name;
    private String color;
    private Date createDate;
    
    public Topic() {
		this.createDate = new Date();
		this.uuid = UUID.randomUUID().toString();
	}
}
