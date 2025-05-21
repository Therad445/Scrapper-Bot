package backend.academy.bot.dto;

import java.net.URI;

public class RemoveLinkRequest {
    private URI link;

    public URI getLink() {
        return link;
    }

    public void setLink(URI link) {
        this.link = link;
    }
}
