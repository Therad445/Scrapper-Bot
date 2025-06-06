package backend.academy.scrapper.kafka.command;

import java.net.URI;
import java.util.List;
import java.util.Objects;

public class TrackCommand {
    private Long chatId;
    private URI link;
    private List<String> tags;
    private List<String> filters;

    public TrackCommand() {}

    public TrackCommand(Long chatId, URI link, List<String> tags, List<String> filters) {
        this.chatId = chatId;
        this.link = link;
        this.tags = tags;
        this.filters = filters;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public URI getLink() {
        return link;
    }

    public void setLink(URI link) {
        this.link = link;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getFilters() {
        return filters;
    }

    public void setFilters(List<String> filters) {
        this.filters = filters;
    }

    @Override
    public String toString() {
        return "TrackCommand{" + "chatId="
                + chatId + ", link="
                + link + ", tags="
                + tags + ", filters="
                + filters + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrackCommand)) return false;
        TrackCommand that = (TrackCommand) o;
        return Objects.equals(chatId, that.chatId)
                && Objects.equals(link, that.link)
                && Objects.equals(tags, that.tags)
                && Objects.equals(filters, that.filters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, link, tags, filters);
    }
}
