package it.bz.tis.alpenstaedte;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = { "findCommentsByUuid" })
public class Comment {

    @Size(max = 50000)
    private String text;

    @ManyToOne
    private PipUser owner;

    @ManyToOne
    private Idea idea;

    private Date created_on = new Date();

    private Date updated_on = new Date();

    private boolean banned = false;
    
    @ManyToMany
    private Set<PipUser> liker = new HashSet<PipUser>();

    @Column(unique = true)
    private String uuid = UUID.randomUUID().toString();
}
