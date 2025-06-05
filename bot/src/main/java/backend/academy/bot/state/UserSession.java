package backend.academy.bot.state;

import java.io.Serial;
import java.io.Serializable;

public class UserSession implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String pendingUrl;
    private String pendingTags;
    private String pendingFilters;
    private BotState state = BotState.NONE;

    public UserSession() {
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

    public BotState getState() {
        return state;
    }

    public void setState(BotState state) {
        this.state = state;
    }

    public void reset() {
        this.pendingUrl = null;
        this.pendingTags = null;
        this.pendingFilters = null;
        this.state = BotState.NONE;
    }
}
