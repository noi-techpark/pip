package it.bz.tis.alpenstaedte;
import java.util.List;

import static org.junit.Assert.*;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(value = {"classpath:/META-INF/spring/applicationContext*.xml"})
public class QueryTests extends AbstractJUnit4SpringContextTests{
	
	@Test
	public void testIdeaQuery(){
		Topic topic = Topic.findTopicsByNameEquals("Energy").getSingleResult();
		ProjectStatus status = ProjectStatus.findProjectStatusesByNameEquals("funding granted").getSingleResult();
		List<Idea> ideas = Idea.findIdeaByStatusAndTopicsContainsTopic(status, topic);
		assertNotNull(topic);
		assertNotNull(status);
		assertNotNull(ideas);
		assertTrue(ideas.size()>0);
		for (Idea idea :ideas){
			System.out.println(idea.getName());
		}
	}

	
}
