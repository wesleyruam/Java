package com.wesleyruam.ticketflow.dto.Ticket;

import com.wesleyruam.ticketflow.model.Ticket.PrioritiesEnum;
import com.wesleyruam.ticketflow.model.Ticket.StatusEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTicketDTO {
    private String title;
    private String description;
    private PrioritiesEnum priority;
    private StatusEnum status;
}
