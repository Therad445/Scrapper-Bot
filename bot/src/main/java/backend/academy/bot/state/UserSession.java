package backend.academy.bot.state;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSession {
    private String pendingUrl;
    private String pendingTags;
    private String pendingFilters;
    private BotState state = BotState.NONE;

    public void reset() {
        pendingUrl = null;
        pendingTags = null;
        pendingFilters = null;
        state = BotState.NONE;
    }
}
