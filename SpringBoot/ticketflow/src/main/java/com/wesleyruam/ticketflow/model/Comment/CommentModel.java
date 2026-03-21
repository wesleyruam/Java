package com.wesleyruam.ticketflow.model.Comment;

import java.time.LocalDateTime;

import com.wesleyruam.ticketflow.model.Ticket.TicketModel;
import com.wesleyruam.ticketflow.model.User.UserModel;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private UserModel author;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private TicketModel ticket;
}