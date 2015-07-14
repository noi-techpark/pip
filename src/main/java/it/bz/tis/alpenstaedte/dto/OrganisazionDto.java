package it.bz.tis.alpenstaedte.dto;

import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
public class OrganisazionDto {
	public OrganisazionDto() {
	}
	public OrganisazionDto(String name) {
		this.name = name;
	}

	private String name;

}
