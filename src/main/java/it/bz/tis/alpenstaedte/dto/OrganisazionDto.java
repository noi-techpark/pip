package it.bz.tis.alpenstaedte.dto;

import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
public class OrganisazionDto {
	public OrganisazionDto() {
	}
	public OrganisazionDto(String name,String pic) {
		this.name = name;
		this.pic = pic;
	}

	private String name;
	private String pic;

}
