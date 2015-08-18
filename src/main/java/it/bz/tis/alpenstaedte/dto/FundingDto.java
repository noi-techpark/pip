package it.bz.tis.alpenstaedte.dto;

import it.bz.tis.alpenstaedte.util.DtoCastUtil;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
public class FundingDto {
	
	public FundingDto() {
	}
	
	public FundingDto(@JsonProperty("url")String url,@JsonProperty("desc") String description,@JsonProperty("cofinance") Byte cofinance,@JsonProperty("deadline") String deadline) {
		super();
		this.url = url;
		this.description = description;
		this.deadline = deadline;
		this.cofinance = cofinance;
	}
	public FundingDto(String uuid, String url, String description, Date deadline, Byte cofinance) {
		this.uuid = uuid;
		this.url = url;
		this.description = description;
		this.cofinance = cofinance;
		if (deadline!=null)
		this.deadline = DtoCastUtil.formatter.format(deadline);
	}
	private String uuid;
	private String url;
	private String description;
	private String deadline;
	private Byte cofinance;
}
