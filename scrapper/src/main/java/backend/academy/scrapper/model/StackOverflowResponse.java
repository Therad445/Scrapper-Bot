package backend.academy.scrapper.model;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
public class StackOverflowResponse {
    private List<StackOverflowItem> items;

}
