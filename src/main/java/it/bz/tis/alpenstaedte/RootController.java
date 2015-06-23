package it.bz.tis.alpenstaedte;
import it.bz.tis.alpenstaedte.dto.FundingDto;
import it.bz.tis.alpenstaedte.dto.NewIdeaDto;
import it.bz.tis.alpenstaedte.dto.ResponseObject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/")
@Controller
public class RootController {
    @RequestMapping(method = RequestMethod.POST, value = "create")
    public @ResponseBody ResponseEntity<ResponseObject> post(@RequestBody NewIdeaDto dto) {
    	Idea idea = new Idea(dto.getProjectName(),dto.getProjectDesc(),dto.getTopics());
    	idea.persist();
   		for (FundingDto fundingDto:dto.getFundings()){
   			Funding funding =new Funding(fundingDto.getUrl(),fundingDto.getDescription(),idea);
   			funding.persist();
   		}
    		
    	return new ResponseEntity<ResponseObject>(new ResponseObject(idea.getUuid()),HttpStatus.OK);
    }
}
