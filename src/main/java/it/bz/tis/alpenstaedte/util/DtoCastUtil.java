package it.bz.tis.alpenstaedte.util;

import it.bz.tis.alpenstaedte.Topic;
import it.bz.tis.alpenstaedte.dto.TopicDto;

import java.util.HashSet;
import java.util.Set;

public class DtoCastUtil {

	public static Set<TopicDto> cast(Set<Topic> preferredTopics) {
		Set<TopicDto> topics = new HashSet<TopicDto>();
		for (Topic topic:preferredTopics){
			TopicDto dto = cast(topic);
			topics.add(dto);
		}
		return topics;
	}

	private static TopicDto cast(Topic topic) {
		TopicDto dto = new TopicDto();
		dto.setName(topic.getName());
		dto.setColor(topic.getColor());
		dto.setUuid(topic.getUuid());
		return dto;
	}

}
