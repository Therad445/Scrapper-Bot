package backend.academy.scrapper.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TrackedLink {

    private String url;
    private String lastUpdate; // Можно хранить кэш, timestamp или версия

    public TrackedLink(String url, String lastUpdate) {
        this.url = url;
        this.lastUpdate = lastUpdate;
    }

}
