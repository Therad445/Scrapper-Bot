package backend.academy.scrapper.service;

import backend.academy.scrapper.repository.ChatRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ChatService {
    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public void register(Long chatId) {
        if (chatRepository.exists(chatId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Чат уже зарегистрирован");
        }
        chatRepository.register(chatId);
    }

    public void delete(Long chatId) {
        if (!chatRepository.exists(chatId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Чат не существует");
        }
        chatRepository.delete(chatId);
    }
}
