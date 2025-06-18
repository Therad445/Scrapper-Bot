package backend.academy.bot.config;

import backend.academy.bot.dto.LinkResponse;
import backend.academy.bot.state.UserSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, UserSession> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, UserSession> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        Jackson2JsonRedisSerializer<UserSession> valueSerializer = new Jackson2JsonRedisSerializer<>(UserSession.class);
        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisTemplate<String, List<LinkResponse>> linkListRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, List<LinkResponse>> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        ObjectMapper mapper = new ObjectMapper();
        mapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder()
                    .allowIfSubType("java.util")
                    .allowIfSubType(LinkResponse.class)
                        .build(),
                ObjectMapper.DefaultTyping.NON_FINAL);

        @SuppressWarnings("unchecked")
        Jackson2JsonRedisSerializer<List<LinkResponse>> jsonSerializer =
                new Jackson2JsonRedisSerializer<>(mapper, (Class<List<LinkResponse>>) (Class<?>) List.class);

        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }
}
