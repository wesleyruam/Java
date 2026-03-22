package com.wesleyruam.ticketflow.repository.Comment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wesleyruam.ticketflow.model.Comment.CommentModel;

@Repository
public interface CommentRepository extends JpaRepository<CommentModel, Long>{
    List<CommentModel> findAllByTicketId(Long ticketId);
}
