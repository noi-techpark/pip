package it.bz.tis.alpenstaedte.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
public class CommentDto {
	private String text;
	private UserDto author;
	private Date created_on;
	private boolean banned;
	private String uuid;
	private List<String> liker = new ArrayList<String>();
}
