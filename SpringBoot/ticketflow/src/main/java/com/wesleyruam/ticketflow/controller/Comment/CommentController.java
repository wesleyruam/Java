package com.wesleyruam.ticketflow.controller.Comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wesleyruam.ticketflow.dto.ServiceResponse;
import com.wesleyruam.ticketflow.dto.Comment.AddCommentDTO;
import com.wesleyruam.ticketflow.dto.Comment.CommentResponseDTO;
import com.wesleyruam.ticketflow.dto.Comment.UpdateCommentDTO;
import com.wesleyruam.ticketflow.service.Comment.CommentService;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<ServiceResponse<CommentResponseDTO>> createComment(@RequestBody AddCommentDTO addCommentDTO){
        ServiceResponse<CommentResponseDTO> response = commentService.addComment(addCommentDTO);

        if (response.isSuccess()){
            return ResponseEntity.ok(response);
        }else{
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse<CommentResponseDTO>> getCommentById(@PathVariable Long id){
        ServiceResponse<CommentResponseDTO> response = commentService.getCommentById(id);

        if (response.isSuccess()){
            return ResponseEntity.ok(response);
        }else{
            return ResponseEntity.badRequest().body(response);
        }   
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse<CommentResponseDTO>> updateComment(@PathVariable Long id, @RequestBody UpdateCommentDTO commentDTO){
        ServiceResponse<CommentResponseDTO> response = commentService.updateComment(id, commentDTO);

        if (response.isSuccess()){
            return ResponseEntity.ok(response);
        }else{
            return ResponseEntity.badRequest().body(response);
        } 
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResponse<CommentResponseDTO>> deleteComment(@PathVariable Long id){
         ServiceResponse<CommentResponseDTO> response = commentService.deleteComment(id);

         if (response.isSuccess()){
            return ResponseEntity.ok(response);
        }else{
            return ResponseEntity.badRequest().body(response);
        }
    }
}
