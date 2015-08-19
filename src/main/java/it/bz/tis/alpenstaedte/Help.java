package it.bz.tis.alpenstaedte;
import java.util.Date;
import java.util.UUID;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = { "findHelpsByUuid" })
public class Help {
    private String name;
	private String uuid = UUID.randomUUID().toString();
    private Date created_on = new Date();
}
