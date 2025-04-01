package backend.academy.scrapper.model;

import java.util.List;

public class StackOverflowResponse {
    private List<StackOverflowItem> items;

    public List<StackOverflowItem> getItems() {
        return items;
    }

    public void setItems(List<StackOverflowItem> items) {
        this.items = items;
    }
}
