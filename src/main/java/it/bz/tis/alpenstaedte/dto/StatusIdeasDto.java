package it.bz.tis.alpenstaedte.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
public class StatusIdeasDto {
	private String name;
	private List<TopicDto> children = new ArrayList<TopicDto>();
}
