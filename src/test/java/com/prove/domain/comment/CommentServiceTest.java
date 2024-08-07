package com.prove.domain.comment;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import com.prove.domain.Prove.Prove;
import com.prove.domain.User.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @MockBean
    private CommentRepository commentRepository;

    private UserEntity userEntity;
    private Prove prove;

    @BeforeEach
    public void setUp() {
        userEntity = new UserEntity();
        prove = new Prove();
    }

    @Test
    public void testMakeComment() {
        // Given
        String commentText = "Test comment";

        // When
        commentService.makeComment(commentText, userEntity, prove);

        // Then
        verify(commentRepository, times(1)).save(any(Comment.class));
        assert userEntity.getComments().size() == 1;
        assert prove.getCommentList().size() == 1;
        assert userEntity.getComments().get(0).getComment().equals(commentText);
        assert prove.getCommentList().get(0).getComment().equals(commentText);
    }

    @Test
    public void testMakeCommentDtos() {
        // 가짜 Comment 객체 생성
        Comment comment1 = new Comment();
        comment1.setComment("Test Comment 1");

        Comment comment2 = new Comment();
        comment2.setComment("Test Comment 2");

        List<Comment> comments = new ArrayList<>();
        comments.add(comment1);
        comments.add(comment2);

        // 메서드 호출
        List<CommentDto> commentDtos = commentService.makeCommentDtos(comments);

        // 검증
        assertEquals(2, commentDtos.size());
        assertEquals("Test Comment 1", commentDtos.get(0).getComment());
        assertEquals("Test Comment 2", commentDtos.get(1).getComment());
    }
}
