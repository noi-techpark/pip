package it.bz.tis.alpenstaedte.util;

import it.bz.tis.alpenstaedte.Funding;
import it.bz.tis.alpenstaedte.Organisazion;
import it.bz.tis.alpenstaedte.PipUser;
import it.bz.tis.alpenstaedte.Comment;
import it.bz.tis.alpenstaedte.Topic;
import it.bz.tis.alpenstaedte.dto.CommentDto;
import it.bz.tis.alpenstaedte.dto.FundingDto;
import it.bz.tis.alpenstaedte.dto.OrganisazionDto;
import it.bz.tis.alpenstaedte.dto.TopicDto;
import it.bz.tis.alpenstaedte.dto.UserDto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

	public static TopicDto cast(Topic topic) {
		TopicDto dto = new TopicDto();
		dto.setName(topic.getName());
		dto.setColor(topic.getColor());
		dto.setUuid(topic.getUuid());
		return dto;
	}

	public static List<CommentDto> cast(List<Comment> comments) {
		List<CommentDto> dtos = new ArrayList<CommentDto>();
		for (Comment comment:comments){
			CommentDto commentDto = cast(comment);
			dtos.add(commentDto);
		}
		return dtos;
	}

	public static CommentDto cast(Comment comment) {
		CommentDto dto = new CommentDto();
		dto.setText(comment.getText());
		dto.setCreated_on(comment.getCreated_on());
		UserDto userDto = DtoCastUtil.cast(comment.getOwner()); 
		dto.setAuthor(userDto);
		return dto;
	}

	private static UserDto cast(PipUser user) {
		UserDto dto = new UserDto();
		
		dto.setName(user.getName());
		dto.setSurname(user.getSurname());
		dto.setUuid(user.getUuid());
		if (user.getName()== null && user.getSurname() == null)
			dto.setName("Anonymous");
		return dto;
	}


	public static Set<OrganisazionDto> castOrgs(
			List<Organisazion> organisazions) {
		Set<OrganisazionDto> dtos = new HashSet<OrganisazionDto>();
		for (Organisazion org: organisazions) {
			OrganisazionDto dto = cast(org);
			dtos.add(dto);
		}
		return dtos;
	}

	public static OrganisazionDto cast(Organisazion org) {
		return new OrganisazionDto(org.getName());
	}

	public static List<FundingDto> castFundings(List<Funding> possibleFundings) {
		List<FundingDto> fundings = new ArrayList<FundingDto>();
		for (Funding funding : possibleFundings){
			FundingDto fundingDto = cast(funding);
			fundings.add(fundingDto);
		}
		return fundings;
	}

	public static FundingDto cast(Funding funding) {
		return new FundingDto(funding.getUuid(),funding.getUrl(), funding.getDescription());
	}

}
