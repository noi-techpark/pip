package it.bz.tis.alpenstaedte;
import java.util.UUID;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;
import org.springframework.roo.addon.equals.RooEquals;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooEquals(excludeFields = { "description", "idea", "url" })
@RooJpaActiveRecord(finders = { "findFundingsByIdea", "findFundingsByUuid" })
public class Funding {

    public Funding() {
        this.uuid = UUID.randomUUID().toString();
    }

    public Funding(String url, String description, Idea idea) {
        this.uuid = UUID.randomUUID().toString();
        this.url = url;
        this.description = description;
        this.idea = idea;
    }

    private String uuid;

    private String url;

    @Size(max = 50000)
    private String description;

    @ManyToOne
    private Idea idea;
}
