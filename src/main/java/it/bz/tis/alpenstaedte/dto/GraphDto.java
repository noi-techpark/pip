package it.bz.tis.alpenstaedte.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
public class GraphDto {
	private List<StatusIdeasDto> children = new ArrayList<StatusIdeasDto>();
	private String name = "Projects";
}
