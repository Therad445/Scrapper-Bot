package backend.academy.scrapper.model;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class LinkUpdateTest {

    // Arrange
    @Test
    void testLinkUpdate_ValidObject() {
        Set<Long> tgChatIds = new HashSet<>();
        tgChatIds.add(12345L);
        LinkUpdate linkUpdate = new LinkUpdate(1L, "https://example.com", "Description", tgChatIds);

        // Act
        Long id = linkUpdate.getId();
        String url = linkUpdate.getUrl();
        String description = linkUpdate.getDescription();
        Set<Long> chatIds = linkUpdate.getTgChatIds();

        // Assert
        assertEquals(1L, id);
        assertEquals("https://example.com", url);
        assertEquals("Description", description);
        assertEquals(tgChatIds, chatIds);
    }

    // Arrange
    @Test
    void testLinkUpdate_InvalidUrl() {
        Set<Long> tgChatIds = new HashSet<>();
        tgChatIds.add(12345L);
        LinkUpdate linkUpdate = new LinkUpdate(1L, "invalid-url", "Description", tgChatIds);

        // Act
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<LinkUpdate>> violations = validator.validate(linkUpdate);

        // Assert
        assertFalse(violations.isEmpty(), "Validation should fail due to invalid URL.");
    }

    // Arrange
    @Test
    void testLinkUpdate_NullId() {
        Set<Long> tgChatIds = new HashSet<>();
        tgChatIds.add(12345L);
        LinkUpdate linkUpdate = new LinkUpdate(null, "https://example.com", "Description", tgChatIds);

        // Act
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<LinkUpdate>> violations = validator.validate(linkUpdate);

        // Assert
        assertFalse(violations.isEmpty(), "Validation should fail due to null id.");
    }

    // Arrange
    @Test
    void testLinkUpdate_NullDescription() {
        Set<Long> tgChatIds = new HashSet<>();
        tgChatIds.add(12345L);
        LinkUpdate linkUpdate = new LinkUpdate(1L, "https://example.com", null, tgChatIds);

        // Act
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<LinkUpdate>> violations = validator.validate(linkUpdate);

        // Assert
        assertFalse(violations.isEmpty(), "Validation should fail due to null description.");
    }

    // Arrange
    @Test
    void testLinkUpdate_EmptyTgChatIds() {
        LinkUpdate linkUpdate = new LinkUpdate(1L, "https://example.com", "Description", new HashSet<>());

        // Act
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<LinkUpdate>> violations = validator.validate(linkUpdate);

        // Assert
        assertFalse(violations.isEmpty(), "Validation should fail due to empty tgChatIds.");
    }
}
