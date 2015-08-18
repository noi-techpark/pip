package it.bz.tis.alpenstaedte;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = { "findProjectStatusesByNameEquals" })
public class ProjectStatus {

    private String name;
    private Integer position;
}
