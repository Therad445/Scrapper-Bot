package backend.academy.bot.dto;

import lombok.Getter;
import lombok.Setter;


public class RemoveLinkRequest {
    private String link;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
