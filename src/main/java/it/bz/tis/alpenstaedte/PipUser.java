package it.bz.tis.alpenstaedte;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import org.hibernate.validator.constraints.Email;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders={"findPipUsersByEmailEquals","findPipUsersByUuidEquals"})
@Table(name="alps_user")
public class PipUser {
	private String uuid = UUID.randomUUID().toString();
	@Email
	@Column(unique=true)
	private String email;
	private String password;
	private String role;
	private String name;
	private String surname;
	private String phone;
	
	@ElementCollection
	private Set<String> languageSkills;
	
	@ManyToMany(cascade= CascadeType.PERSIST)
	private List<Organisazion> organisazions = new ArrayList<Organisazion>();
	
	@ManyToMany
	private Set<Topic> preferredTopics = new HashSet<Topic>();

	public static List<PipUser> findPipUserByOrganisazion(
			Organisazion organisazion) {
		TypedQuery<PipUser> query = entityManager().createQuery("Select user from PipUser user where :organisazion in elements(user.organisazions)", PipUser.class);
		query.setParameter("organisazion", organisazion);
		List<PipUser> resultList = query.getResultList();
		return resultList;
	}

	public boolean organisationMatches(PipUser user) {
		return this.getOrganisazions().get(0).getName().equals(user.getOrganisazions().get(0));
	}

	public static List<PipUser> findPipUserByOrganisazionAndRole(
			Organisazion organisazion, String role) {
		TypedQuery<PipUser> query = entityManager().createQuery("Select user from PipUser user where role=:role AND :organisazion in elements(user.organisazions)", PipUser.class);
		query.setParameter("organisazion", organisazion);
		query.setParameter("role", role);
		List<PipUser> resultList = query.getResultList();
		return resultList;
	}
	public static List<PipUser> findPipUserByInterestedTopic(
			Topic topic) {
		TypedQuery<PipUser> query = entityManager().createQuery("Select user from PipUser user where :topic in elements(user.preferredTopics)", PipUser.class);
		query.setParameter("topic", topic);
		List<PipUser> resultList = query.getResultList();
		return resultList;
	}
}