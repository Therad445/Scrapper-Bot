package backend.academy.bot.state;

import lombok.Getter;
import lombok.Setter;


public class UserSession {
    private BotState state = BotState.NONE;
    private String pendingUrl;
    private String pendingTags;
    private String pendingFilters;

    public BotState getState() {
        return state;
    }

    public void setState(BotState state) {
        this.state = state;
    }

    public String getPendingUrl() {
        return pendingUrl;
    }

    public void setPendingUrl(String pendingUrl) {
        this.pendingUrl = pendingUrl;
    }

    public String getPendingTags() {
        return pendingTags;
    }

    public void setPendingTags(String pendingTags) {
        this.pendingTags = pendingTags;
    }

    public String getPendingFilters() {
        return pendingFilters;
    }

    public void setPendingFilters(String pendingFilters) {
        this.pendingFilters = pendingFilters;
    }
}
