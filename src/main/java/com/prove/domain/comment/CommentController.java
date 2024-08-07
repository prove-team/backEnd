package com.prove.domain.comment;

import com.prove.domain.PagedDTO;
import com.prove.domain.Prove.Prove;
import com.prove.domain.Prove.ProveRepository;
import com.prove.domain.User.UserEntity;
import com.prove.domain.User.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    private final ProveRepository proveRepository;
    private final UserRepository userRepository;
    private final CommentService commentService;
    private final CommentRepository commentRepository;

    @PostMapping("/comment/{prove_id}/{comment}")
    public ResponseEntity<?> makeComment(@PathVariable Long prove_id, @PathVariable String comment){
        Prove prove = proveRepository.findById(prove_id).orElseThrow(() -> new EntityNotFoundException("Prove not found with id: " + prove_id));
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByUsername(username);
        return commentService.makeComment(comment,userEntity,prove);
    }


    //TODO Pageable--완료
    @GetMapping("/myComment")
    public PagedDTO<List<CommentDto>> getMyAllComment(@PageableDefault(page = 0, size = 5) Pageable pageable){
        return commentService.pagedCommentDTO(pageable);
    }

    @PutMapping("/comment/{comment_id}")
    public ResponseEntity<?> editComment(@PathVariable Long comment_id, @RequestBody String comment){
        return commentService.editComment(comment_id,comment);
    }

    @DeleteMapping("/comment/{comment_id}")
    public ResponseEntity <?> deleteComment(@PathVariable Long comment_id){
        return commentService.deleteComment(comment_id);
    }

    @PostMapping("/comment/like/{comment_id}")
    public ResponseEntity<?> addCommentLike(@PathVariable Long comment_id){
        return commentService.addCommentLike(comment_id);
    }
}
