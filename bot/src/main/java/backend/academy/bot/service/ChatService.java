package backend.academy.bot.service;


import backend.academy.bot.client.ScrapperClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ScrapperClient client;

    public void register(Long chatId) {
        client.registerUser(chatId);
    }
}
