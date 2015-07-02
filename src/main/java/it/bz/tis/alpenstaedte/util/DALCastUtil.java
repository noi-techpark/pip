package it.bz.tis.alpenstaedte.util;

import it.bz.tis.alpenstaedte.Topic;
import it.bz.tis.alpenstaedte.dto.TopicDto;

import java.util.HashSet;
import java.util.Set;

public class DALCastUtil {

	public static Set<Topic> cast(Set<TopicDto> topics) {
		Set<Topic> dalTopics = new HashSet<Topic>();
		for (TopicDto dto:topics){
			Topic topic = cast(dto);
			dalTopics.add(topic);
		}
		return dalTopics;
	}

	private static Topic cast(TopicDto dto) {
		Topic topic = Topic.findTopicsByNameEquals(dto.getName()).getSingleResult();
		return topic;
	}


}
