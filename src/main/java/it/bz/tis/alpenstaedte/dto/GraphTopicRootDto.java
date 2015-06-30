package it.bz.tis.alpenstaedte.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
public class GraphTopicRootDto {
	private String name;
	private List<ProjectStatusDto> children = new ArrayList<ProjectStatusDto>();
}
