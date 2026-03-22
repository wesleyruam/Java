package com.wesleyruam.ticketflow.dto.Ticket;

import java.time.LocalDateTime;

import com.wesleyruam.ticketflow.model.Ticket.PrioritiesEnum;
import com.wesleyruam.ticketflow.model.Ticket.StatusEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TicketResponseDTO {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private PrioritiesEnum prority;
    private StatusEnum status;
    private String requestingUserName;
    private String responsibleUserName;
}
