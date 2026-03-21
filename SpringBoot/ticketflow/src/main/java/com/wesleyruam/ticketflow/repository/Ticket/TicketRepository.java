package com.wesleyruam.ticketflow.repository.Ticket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wesleyruam.ticketflow.model.Ticket.TicketModel;

@Repository
public interface TicketRepository extends JpaRepository<TicketModel, Long>{

} 
  