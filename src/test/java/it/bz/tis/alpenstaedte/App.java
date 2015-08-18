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
	private static final Map<Integer, String> status;
    static {
        Map<Integer, String> aMap = new HashMap<Integer, String>();
        aMap.put(1, "idea");
        aMap.put(2, "drafting");
        aMap.put(3, "application done");
        aMap.put(4, "funding not granted");
        aMap.put(5, "funding granted");
        aMap.put(6, "concluded");
        status = Collections.unmodifiableMap(aMap);
    }
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
        aMap.put("Chambéry", "Chambéry.png");
        aMap.put("Sonthofen", "Sonthofen.png");
        aMap.put("Herisau", "Herisau.jpg");
        aMap.put("Gap", "Gap.png");
        aMap.put("Bad Reichenhall", "Bad_Reichenhall.png");
        aMap.put("Belluno", "Belluno.gif");
        aMap.put("Villach", "Villach.png");
        aMap.put("TIS innovation park South Tyrol", "tis.png");
        aMap.put("Tolmin", "Tolmin.gif");
        aMap.put("Trento", "Trient.jpg");

        orgs = Collections.unmodifiableMap(aMap);
    }
	@Test
	public void updateStatus(){
		for (Map.Entry<Integer, String> entry: status.entrySet()){
			List<ProjectStatus> statuse = ProjectStatus.findProjectStatusesByNameEquals(entry.getValue()).getResultList();
			ProjectStatus p;
			if (statuse.isEmpty())
				p = new ProjectStatus();
			else
				p = statuse.get(0);
			p.setName(entry.getValue());
			p.setPosition(entry.getKey());
			p.merge();
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
