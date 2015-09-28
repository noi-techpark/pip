package it.bz.tis.alpenstaedte;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private List<PipUser> liker = new ArrayList<PipUser>();

    @Column(unique = true)
    private String uuid = UUID.randomUUID().toString();
}
