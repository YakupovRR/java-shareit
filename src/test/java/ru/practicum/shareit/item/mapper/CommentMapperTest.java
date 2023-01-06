package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentMapperTest {

    private final CommentMapper commentMapper;
    private final LocalDateTime localDateTime = LocalDateTime.now();
    private final CommentDto commentDtoTest = CommentDto.builder()
            .id(1)
            .text("text")
            .authorName("Mike")
            .created(localDateTime)
            .build();
    private final Comment commentTest = Comment.builder()
            .id(1)
            .text("text")
            .item(null)
            .author(null)
            .created(localDateTime)
            .build();
    private final Comment commentTestWithAuthor = Comment.builder()
            .id(1)
            .text("text")
            .item(null)
            .author(User.builder().id(1).name("Mike").build())
            .created(localDateTime)
            .build();

    @Test
    void testToComment() {
        Comment comment = commentMapper.toComment(commentDtoTest);
        assertEquals(commentTest, comment);
    }

    @Test
    void testToCommentDto() {
        CommentDto commentDto = commentMapper.toCommentDto(commentTestWithAuthor);
        assertEquals(commentDtoTest, commentDto);
    }
}