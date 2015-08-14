package it.bz.tis.alpenstaedte;
import javax.persistence.Column;

import org.springframework.roo.addon.equals.RooEquals;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders="findOrganisazionsByName")
@RooEquals(excludeFields={"id"})
public class Organisazion {

    /**
     */
	@Column(unique = true)
    private String name;
    private String pic;
}
