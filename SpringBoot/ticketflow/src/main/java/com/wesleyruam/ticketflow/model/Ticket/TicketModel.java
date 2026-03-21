package com.wesleyruam.ticketflow.model.Ticket;

import java.time.LocalDateTime;

import com.wesleyruam.ticketflow.model.User.UserModel;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private PrioritiesEnum prority;
    private StatusEnum status;

    @ManyToOne
    private UserModel requestingUser;

    @ManyToOne
    private UserModel responsibleUser;

    @PrePersist
    protected void onCreate(){
        prority = PrioritiesEnum.BAIXA;
        status = StatusEnum.ABERTO;
    }
}