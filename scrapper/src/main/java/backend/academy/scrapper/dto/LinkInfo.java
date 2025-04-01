package backend.academy.scrapper.dto;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class LinkInfo {
    private final String link;
    private final Set<String> tags;
    private final Set<String> filters;
    private UpdateInfo updateInfo;

    public LinkInfo(String link, Set<String> tags, Set<String> filters) {
        this.link = link;
        this.tags = tags == null ? new HashSet<>() : new HashSet<>(tags);
        this.filters = filters == null ? new HashSet<>() : new HashSet<>(filters);
        this.updateInfo = new UpdateInfo();
    }

    public String getLink() {
        return link;
    }

    public Set<String> getTags() {
        return tags;
    }

    public Set<String> getFilters() {
        return filters;
    }

    public UpdateInfo getUpdateInfo() {
        return updateInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LinkInfo linkInfo = (LinkInfo) o;
        return Objects.equals(link, linkInfo.link)
                && Objects.equals(tags, linkInfo.tags)
                && Objects.equals(filters, linkInfo.filters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(link, tags, filters);
    }
}
