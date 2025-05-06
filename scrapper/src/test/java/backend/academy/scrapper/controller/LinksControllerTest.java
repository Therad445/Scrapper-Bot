package backend.academy.scrapper.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.scrapper.model.AddLinkRequest;
import backend.academy.scrapper.model.LinkResponse;
import backend.academy.scrapper.model.ListLinksResponse;
import backend.academy.scrapper.model.RemoveLinkRequest;
import backend.academy.scrapper.service.LinkService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

@WebMvcTest(LinksController.class)
@Import(LinksControllerTest.MockConfig.class)
class LinksControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LinkService linkService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(linkService);
    }

    @Test
    @DisplayName("GET /links - valid")
    void getLinks_shouldReturnList() throws Exception {
        List<LinkResponse> links = List.of(new LinkResponse(1L, "https://example.com", Set.of("a"), Set.of("f")));
        when(linkService.getLinks(123L)).thenReturn(new ListLinksResponse(links, links.size()));

        mockMvc.perform(get("/links").header("Tg-chat-id", 123))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.links[0].link").value("https://example.com"));
    }

    @Test
    @DisplayName("POST /links - add new link")
    void addLink_shouldReturnLink() throws Exception {
        AddLinkRequest request = new AddLinkRequest("https://foo.bar", Set.of("dev"), Set.of("user:bob"));
        LinkResponse response = new LinkResponse(1L, request.link(), request.tags(), request.filters());

        when(linkService.addLink(123L, request)).thenReturn(response);

        mockMvc.perform(post("/links")
                        .header("Tg-chat-id", 123)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.link").value("https://foo.bar"))
                .andExpect(jsonPath("$.tags[0]").value("dev"));
    }

    @Test
    @DisplayName("DELETE /links - remove link")
    void deleteLink_shouldReturnLink() throws Exception {
        RemoveLinkRequest request = new RemoveLinkRequest("https://foo.bar");
        LinkResponse response = new LinkResponse(1L, request.link(), Set.of(), Set.of());

        when(linkService.removeLinks(123L, request)).thenReturn(response);

        mockMvc.perform(delete("/links")
                        .header("Tg-chat-id", 123)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.link").value("https://foo.bar"));
    }

    @Test
    @DisplayName("POST /links/{id}/tags - add tag")
    void addTag_shouldCallService() throws Exception {
        mockMvc.perform(post("/links/10/tags").header("Tg-chat-id", 321).param("tag", "java"))
                .andExpect(status().isOk());

        verify(linkService).addTag(321L, 10L, "java");
    }

    @Test
    @DisplayName("DELETE /links/{id}/tags/{tag} - delete tag")
    void deleteTag_shouldCallService() throws Exception {
        mockMvc.perform(delete("/links/20/tags/spring").header("Tg-chat-id", 555))
                .andExpect(status().isOk());

        verify(linkService).deleteTag(555L, 20L, "spring");
    }

    @Test
    @DisplayName("POST /links/{id}/filters - add filter")
    void addFilter_shouldCallService() throws Exception {
        mockMvc.perform(post("/links/5/filters").header("Tg-chat-id", 777).param("filter", "user:foo"))
                .andExpect(status().isOk());

        verify(linkService).addFilter(777L, 5L, "user:foo");
    }

    @Test
    @DisplayName("DELETE /links/{id}/filters/{filter} - delete filter")
    void deleteFilter_shouldCallService() throws Exception {
        mockMvc.perform(delete("/links/8/filters/user:bar").header("Tg-chat-id", 999))
                .andExpect(status().isOk());

        verify(linkService).removeFilter(999L, 8L, "user:bar");
    }

    @TestConfiguration
    static class MockConfig {

        @Bean
        public LinkService linkService() {
            return mock(LinkService.class);
        }

        @Bean
        public PlatformTransactionManager transactionManager() {
            return new AbstractPlatformTransactionManager() {
                @Override
                protected Object doGetTransaction() {
                    return new Object();
                }

                @Override
                protected void doBegin(Object transaction, TransactionDefinition definition) {}

                @Override
                protected void doCommit(DefaultTransactionStatus status) {}

                @Override
                protected void doRollback(DefaultTransactionStatus status) {}
            };
        }
    }
}
