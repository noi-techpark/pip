package it.bz.tis.alpenstaedte.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
public class TopicDto {
	private String name;
	private List<ReducedIdeaDto> children = new ArrayList<ReducedIdeaDto>();
}
