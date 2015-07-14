package it.bz.tis.alpenstaedte;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(value = {"classpath:/META-INF/spring/applicationContext*.xml"})
public class App extends AbstractJUnit4SpringContextTests{
	
	
	String[] topics = new String[]{"Energy","Mobility","Environment"};
	String[] status = new String[]{"idea","application done","funding granted","concluded"};
	String[] orgs = new String[]{"Chamonix","Lecco","Annecy","Idrija","Bad Aussee","Bolzano","Brig-Glis","Sondrio","Chambéry","Sonthofen","Herisau","Gap","Bad Reichenhall","Maribor","Belluno","Villach"};
	/*@Test
	public void createTopics(){
		for (String stringtopic : topics){
			Topic topic = new Topic();
			topic.setName(stringtopic);
			topic.persist();
		}
	}*/
	@Test
	public void createStatus(){
		for (String currentStatus : status){
			ProjectStatus status= new ProjectStatus();
			status.setName(currentStatus);
			status.persist();
		}
	}
	@Test
	public void createUsers(){
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		PipUser admin = new PipUser();
		
		admin.setEmail("a@b.c");
		admin.setPassword(encoder.encode("hi"));
		admin.setRole("ADMIN");
		Organisazion organisazion = new Organisazion();
		organisazion.setName("Tis innovation park");
		admin.getOrganisazions().add(organisazion);
		admin.persist();
	}
	@Test
	public void createOrgs(){
		for (String o: orgs){
			Organisazion org = new Organisazion();
			org.setName(o);
			org.persist();
		}
	}
	
}
