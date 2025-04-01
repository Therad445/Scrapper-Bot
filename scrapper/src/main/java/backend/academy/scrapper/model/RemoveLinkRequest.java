package backend.academy.scrapper.model;

public class RemoveLinkRequest {
    private final String link;

    public RemoveLinkRequest(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }
}
