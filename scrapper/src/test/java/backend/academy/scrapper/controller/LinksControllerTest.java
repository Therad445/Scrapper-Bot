package backend.academy.scrapper.controller;

import backend.academy.scrapper.model.AddLinkRequest;
import backend.academy.scrapper.model.LinkResponse;
import backend.academy.scrapper.model.ListLinksResponse;
import backend.academy.scrapper.model.RemoveLinkRequest;
import backend.academy.scrapper.service.LinkService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashSet;
import java.util.Set;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class LinksControllerTest {

    @Mock
    private LinkService linkService;

    @InjectMocks
    private LinksController linksController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(linksController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldReturnListLinksResponseWhenGetLinksCalled() throws Exception {
        // Arrange
        Long chatId = 123L;
        Set<String> tags = new HashSet<>();
        tags.add("tag1");
        Set<String> filters = new HashSet<>();
        filters.add("filter1");

        LinkResponse linkResponse = new LinkResponse(chatId, "http://example.com", tags, filters);
        ListLinksResponse listLinksResponse = new ListLinksResponse(List.of(linkResponse), 1);  // Здесь size = 1
        when(linkService.get(chatId)).thenReturn(listLinksResponse);

        // Act & Assert
        mockMvc.perform(get("/links")
                .header("Tg-chat-id", chatId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size").value(1))  // Ожидаем поле size
            .andExpect(jsonPath("$.links[0].link").value("http://example.com"))
            .andExpect(jsonPath("$.links[0].tags[0]").value("tag1"))
            .andExpect(jsonPath("$.links[0].filters[0]").value("filter1"));

        verify(linkService, times(1)).get(chatId);
    }

}
