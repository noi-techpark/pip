package it.bz.tis.alpenstaedte;
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
    private String name;
}
