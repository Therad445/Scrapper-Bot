package backend.academy.bot.dto.command;

import java.net.URI;
import java.util.Objects;

public class UntrackCommand implements BotCommandMessage {
    private Long chatId;
    private URI link;

    public UntrackCommand() {
    }

    public UntrackCommand(Long chatId, URI link) {
        this.chatId = chatId;
        this.link = link;
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

    @Override
    public String toString() {
        return "UntrackCommand{" +
            "chatId=" + chatId +
            ", link=" + link +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UntrackCommand)) return false;
        UntrackCommand that = (UntrackCommand) o;
        return Objects.equals(chatId, that.chatId) &&
            Objects.equals(link, that.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, link);
    }
}
