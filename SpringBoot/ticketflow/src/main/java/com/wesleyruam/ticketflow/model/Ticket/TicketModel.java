package com.wesleyruam.ticketflow.model.Ticket;

import java.time.LocalDateTime;

import com.wesleyruam.ticketflow.model.User.UserModel;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    private String title;
    
    @NotBlank
    private String description;

    @NotBlank
    private LocalDateTime createdAt;
    
    @NotBlank
    private LocalDateTime updatedAt;

    @NotBlank
    private PrioritiesEnum priority;
    
    @NotBlank
    private StatusEnum status;

    @ManyToOne
    private UserModel requestingUser; // referência ao usuário que abriu o ticket.

    @ManyToOne
    private UserModel responsibleUser; // referência ao atendente designado (opcional).

    @PrePersist
    protected void onCreate(){
        priority = PrioritiesEnum.BAIXA;
        status = StatusEnum.ABERTO;
    }
}