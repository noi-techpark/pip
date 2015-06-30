package it.bz.tis.alpenstaedte;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.TypedQuery;
import javax.validation.constraints.Size;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = { "findIdeasByUuidEquals" })
public class Idea {

    private String name;

    @Size(max = 50000)
    private String description;

    private String uuid;

    @ManyToMany
    private Set<Topic> topics = new HashSet<Topic>();
    
    @ManyToOne
    private ProjectStatus status;
    
    @ElementCollection
    private Set<String> fileNames;
    
    @OneToMany(mappedBy="idea",cascade=CascadeType.ALL)
    private Set<Funding> fundings;

    public Idea() {
        this.uuid = UUID.randomUUID().toString();
    }

    public Idea(String projectName, String projectDesc, Set<Topic> topics, ProjectStatus status) {
        this.uuid = UUID.randomUUID().toString();
        this.name = projectName;
        this.description = projectDesc;
        this.topics = topics;
        this.status = status;
    }

	public static List<Idea> findIdeaByStatusAndTopicsContainsTopic(ProjectStatus status,
			Topic topic) {
		TypedQuery<Idea> query = entityManager().createQuery("select idea from Idea idea where idea.status=:status AND :topic in elements(idea.topics)",Idea.class);
		query.setParameter("status", status);
		query.setParameter("topic", topic);
		return query.getResultList();
		
	}

	public static List<Idea> findIdeaByContainsTopic(Topic topic) {
		TypedQuery<Idea> query = entityManager().createQuery("select idea from Idea idea where :topic in elements(idea.topics)",Idea.class);
		query.setParameter("topic", topic);
		return query.getResultList();
	}
}
