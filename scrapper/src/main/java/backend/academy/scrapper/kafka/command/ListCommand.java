package backend.academy.scrapper.kafka.command;

import java.util.Objects;

public class ListCommand {
    private Long chatId;

    public ListCommand() {}

    public ListCommand(Long chatId) {
        this.chatId = chatId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    @Override
    public String toString() {
        return "ListCommand{" + "chatId=" + chatId + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ListCommand)) return false;
        ListCommand that = (ListCommand) o;
        return Objects.equals(chatId, that.chatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId);
    }
}
