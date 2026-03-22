package com.wesleyruam.ticketflow.controller.Ticket;


import java.util.List;

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
import com.wesleyruam.ticketflow.dto.Ticket.CreateTicketDTO;
import com.wesleyruam.ticketflow.dto.Ticket.TicketResponseDTO;
import com.wesleyruam.ticketflow.dto.Ticket.UpdateTicketDTO;
import com.wesleyruam.ticketflow.service.Ticket.TicketService;

@RestController
@RequestMapping("/api/ticket")
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @PostMapping
    public ResponseEntity<ServiceResponse<TicketResponseDTO>> createTicket(@RequestBody CreateTicketDTO createTicketDTO){
        ServiceResponse<TicketResponseDTO> response = ticketService.createTicket(createTicketDTO);

        if (response.isSuccess()){
            return ResponseEntity.ok(response);
        }else{
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse<TicketResponseDTO>> getTicketById(@PathVariable Long id){
        ServiceResponse<TicketResponseDTO> response = ticketService.getTicketById(id);

        if (response.isSuccess()){
            return ResponseEntity.ok(response);
        }else{
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping
    public ResponseEntity<ServiceResponse<List<TicketResponseDTO>>> listAllTickets(){
        ServiceResponse<List<TicketResponseDTO>> response = ticketService.listAllTickets();

        if (response.isSuccess()){
            return ResponseEntity.ok(response);
        }else{
            return ResponseEntity.status(403).body(response);
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResponse<Boolean>> deleteTicket(@PathVariable Long id){
        ServiceResponse<Boolean> response = ticketService.deleteTicket(id);

        if (response.isSuccess()){
            return ResponseEntity.ok(response);
        }else{
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse<TicketResponseDTO>> updateTicket(@PathVariable Long id, @RequestBody UpdateTicketDTO updateTicketDTO){
        ServiceResponse<TicketResponseDTO> response = ticketService.updateTicket(id, updateTicketDTO);

        if (response.isSuccess()){
            return ResponseEntity.ok(response);
        }else{
            return ResponseEntity.badRequest().body(response);
        }
    }
}
