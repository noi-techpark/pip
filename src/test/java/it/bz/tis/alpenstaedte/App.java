package it.bz.tis.alpenstaedte;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(value = {"classpath:/META-INF/spring/applicationContext*.xml"})
public class App extends AbstractJUnit4SpringContextTests{
	
	
	String[] topics = new String[]{"Energy","Mobility","Environment"};
	String[] status = new String[]{"idea","application done","funding granted","concluded"};
	private static final Map<String, String> orgs;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("Alpine Town of the Year Association", "alps.jpg");
        aMap.put("Chamonix", "Chamonix.png");
        aMap.put("Lecco", "lecco.png");
        aMap.put("Annecy", "Annecy.png");
        aMap.put("Idrija", "Idrija.png");
        aMap.put("Bad Aussee", "Badaussee.jpg");
        aMap.put("Bolzano", "Bolzano.png");
        aMap.put("Brig-Glis", "Brig-Glis.gif");
        aMap.put("Sondrio", "Sondrio.png");
        aMap.put("Chambéry", "Chambéry.png");
        aMap.put("Sonthofen", "Sonthofen.png");
        aMap.put("Herisau", "Herisau.jpg");
        aMap.put("Gap", "Gap.png");
        aMap.put("Bad Reichenhall", "Bad_Reichenhall.png");
        aMap.put("Maribor", "Maribor.gif");
        aMap.put("Belluno", "Belluno.gif");
        aMap.put("Villach", "Villach.png");
        aMap.put("TIS innovation park South Tyrol", "tis.png");
        orgs = Collections.unmodifiableMap(aMap);
    }
	/*@Test
	public void createTopics(){
		for (String stringtopic : topics){
			Topic topic = new Topic();
			topic.setName(stringtopic);
			topic.persist();
		}q
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
		
		admin.setEmail("patrick.bertolla@tis.bz.it");
		admin.setPassword(encoder.encode("hi"));
		admin.setRole("ADMIN");
		Organisazion organisazion = new Organisazion();
		organisazion.setName("TIS innovation park South Tyrol");
		admin.getOrganisazions().add(organisazion);
		admin.persist();
	}
	@Test
	public void createOrgs(){
		for (Map.Entry<String, String> entry: orgs.entrySet()){
			Organisazion org = new Organisazion();
			org.setName(entry.getKey());
			org.persist();
		}
	}
	@Test
	public void updateOrgs(){
		for (Map.Entry<String, String> entry: orgs.entrySet()){
			System.out.println(entry.getKey());
			Organisazion org;
			List<Organisazion> orgs = Organisazion.findOrganisazionsByName(entry.getKey()).getResultList();
			if (orgs.isEmpty()){
				org = new Organisazion();
				org.setName(entry.getKey());
			}
			else{
				org=orgs.get(0);
			}
			org.setPic(entry.getValue());;
			org.merge();
		}
	}
	
}
