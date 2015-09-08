package it.bz.tis.alpenstaedte;
import java.util.ArrayList;
import java.util.Date;
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
@RooJpaActiveRecord(finders = { "findIdeasByUuidEquals","findIdeasByOwner","findIdeasByStatus" })
public class Idea {

    private String name;

    @Size(max = 5000)
    private String description;
    
    @Size(max = 3000)
	private String objectives;
    
    @Size(max = 3000)
    private String outputs;
    
    @Size(max = 3000)
	private String measures;
    
    @Size(max = 3000)
	private String target;
    
    private Integer budget;
    
    private String uuid;

    @ManyToMany
    private Set<Topic> topics = new HashSet<Topic>();
    
    @ManyToOne
    private ProjectStatus status;
    
    @ElementCollection
    private Set<String> fileNames;
    
    @OneToMany(mappedBy="idea",cascade=CascadeType.ALL,orphanRemoval=true)
    private Set<Funding> fundings = new HashSet<Funding>();

    @ManyToOne
    private PipUser owner;
    
    @OneToMany(cascade = CascadeType.ALL,mappedBy="idea")
    private List<Comment> comments = new ArrayList<Comment>();
    private Date created_on = new Date();
    private Date updated_on = new Date();
    
    @ManyToMany
    private Set<Organisazion> interestedOrganisations = new HashSet<Organisazion>();
    
    @ManyToMany
    private Set<PipUser> follower = new HashSet<PipUser>();
    
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

	public static List<Idea> findIdeasFollowed(PipUser currentUser) {
		TypedQuery<Idea> query = entityManager().createQuery("select idea from Idea idea where :user in elements(idea.follower) and idea.owner != :user",Idea.class);
		query.setParameter("user",currentUser);
		return query.getResultList();
	}
}
