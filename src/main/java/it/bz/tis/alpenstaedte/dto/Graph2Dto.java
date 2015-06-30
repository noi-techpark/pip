package it.bz.tis.alpenstaedte.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
public class Graph2Dto {
	private List<GraphTopicRootDto> children = new ArrayList<GraphTopicRootDto>();
	private String name = "Projects";
}
