package com.wesleyruam.ticketflow.service.Comment;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wesleyruam.ticketflow.dto.ServiceResponse;
import com.wesleyruam.ticketflow.dto.Comment.CommentResponseDTO;
import com.wesleyruam.ticketflow.dto.Comment.UpdateCommentDTO;
import com.wesleyruam.ticketflow.model.Comment.CommentModel;
import com.wesleyruam.ticketflow.model.Ticket.TicketModel;
import com.wesleyruam.ticketflow.model.User.UserModel;
import com.wesleyruam.ticketflow.dto.Comment.AddCommentDTO;
import com.wesleyruam.ticketflow.repository.Comment.CommentRepository;
import com.wesleyruam.ticketflow.repository.Ticket.TicketRepository;
import com.wesleyruam.ticketflow.repository.User.UserRepository;
import com.wesleyruam.ticketflow.security.AuthContext;
import com.wesleyruam.ticketflow.security.Permission;

@Service
public class CommentService {
    @Autowired
    CommentRepository commentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    AuthContext authContext;

    private CommentResponseDTO toCommentResponseDTO(CommentModel comment){
        return new CommentResponseDTO(
            comment.getId(),
            comment.getContent(),
            comment.getCreatedAt(),
            comment.getAuthor().getName(),
            comment.getTicket().getTitle()
        );
    }

    public ServiceResponse<CommentResponseDTO> addComment(AddCommentDTO commentDTO){
        try{
            authContext.requirePermission(Permission.ADD_COMMENT);   

            UserModel user = userRepository
                .findById(commentDTO.getUserId())
                .orElseThrow(() ->
                    new RuntimeException("ID do usuário não encontrado")
            );

            TicketModel ticket = ticketRepository
                .findById(commentDTO.getTicketId())
                .orElseThrow(() ->
                    new RuntimeException("ID do ticket não encontrado")
            );

            CommentModel comment = new CommentModel();
            comment.setAuthor(user);
            comment.setContent(commentDTO.getContent());
            comment.setTicket(ticket);

            CommentModel savedComment = commentRepository.save(comment);

            return ServiceResponse.success(
                "Comentário adicionado com sucesso",
                toCommentResponseDTO(savedComment)
            );

        }catch(Exception e){
            return ServiceResponse.error(e.getMessage());
        }
    }

    public ServiceResponse<CommentResponseDTO> getCommentById(Long id){
        CommentModel comment = commentRepository
            .findById(id)
            .orElseThrow(() ->
                new RuntimeException("ID de comentário não encontrado")
        );

        return ServiceResponse.success(
            "Sucesso",
            toCommentResponseDTO(comment)
        );
    }

    public ServiceResponse<List<CommentResponseDTO>> getCommentsByTicketId(Long ticketId){
        List<CommentResponseDTO> response = commentRepository
            .findAllByTicketId(ticketId)
            .stream()
            .map(this::toCommentResponseDTO)
            .collect(Collectors.toList());
        
        return ServiceResponse.success("Sucesso", response);
    }

    public ServiceResponse<CommentResponseDTO> updateComment(Long id, UpdateCommentDTO commentDTO){
        CommentModel comment = commentRepository
            .findById(id)
            .orElseThrow(() ->
                new RuntimeException("ID de comentário não encontrado")
        );

        Long currentUserId = authContext.getCurrentUser().getId();

        if (!currentUserId.equals(comment.getAuthor().getId()) && !authContext.hasPermission(Permission.EDIT_ANY_COMMENT)){
            return ServiceResponse.error("Sem permissão para realizar a ação.");
        }

        if (commentDTO.getContent() != null){
            comment.setContent(commentDTO.getContent());
        }

        CommentModel savedComment = commentRepository.save(comment);

        return ServiceResponse.success("Comentário atualizado com sucesso", toCommentResponseDTO(savedComment));
    }

    public ServiceResponse<CommentResponseDTO> deleteComment(Long idComment){
        authContext.requirePermission(Permission.DELETE_COMMENT);
        
        CommentModel comment = commentRepository
            .findById(idComment)
            .orElseThrow(() ->
                new RuntimeException("ID de comentário não encontrado")
        );

        commentRepository.deleteById(comment.getId());

        return ServiceResponse.success("Comentário excluído com sucesso.");


    }
}
