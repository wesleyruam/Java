package com.wesleyruam.ticketflow.service.Ticket;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.wesleyruam.ticketflow.dto.ServiceResponse;
import com.wesleyruam.ticketflow.dto.Ticket.CreateTicketDTO;
import com.wesleyruam.ticketflow.dto.Ticket.TicketResponseDTO;
import com.wesleyruam.ticketflow.dto.Ticket.UpdateTicketDTO;
import com.wesleyruam.ticketflow.model.Ticket.TicketModel;
import com.wesleyruam.ticketflow.model.User.UserModel;
import com.wesleyruam.ticketflow.repository.Ticket.TicketRepository;
import com.wesleyruam.ticketflow.repository.User.UserRepository;
import com.wesleyruam.ticketflow.security.AuthContext;
import com.wesleyruam.ticketflow.security.Permission;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final AuthContext authContext;

    public TicketService(
            TicketRepository ticketRepository,
            UserRepository userRepository,
            AuthContext authContext
    ) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.authContext = authContext;
    }


    public ServiceResponse<TicketResponseDTO> createTicket(CreateTicketDTO ticketData) {

        try {

            authContext.requirePermission(Permission.CREATE_TICKET);

            UserModel requestingUser = userRepository
                    .findById(ticketData.getRequestingUserId())
                    .orElseThrow(() ->
                            new RuntimeException("ID do usuário solicitante não encontrado")
                    );

            UserModel responsibleUser = userRepository
                    .findById(ticketData.getResponsibleUserId())
                    .orElseThrow(() ->
                            new RuntimeException("ID do usuário responsável não encontrado")
                    );

            TicketModel ticket = new TicketModel();

            ticket.setTitle(ticketData.getTitle());
            ticket.setDescription(ticketData.getDescription());
            ticket.setPriority(ticketData.getPriority());

            ticket.setRequestingUser(requestingUser);
            ticket.setResponsibleUser(responsibleUser);

            TicketModel saved = ticketRepository.save(ticket);

            return ServiceResponse.success(
                    "Ticket criado com sucesso",
                    toResponseDTO(saved)
            );

        } catch (Exception e) {
            return ServiceResponse.error(e.getMessage());
        }
    }


    public ServiceResponse<TicketResponseDTO> getTicketById(Long idTicket) {

        TicketModel ticket = ticketRepository
                .findById(idTicket)
                .orElseThrow(() ->
                        new RuntimeException("ID de ticket não encontrado")
                );

        return ServiceResponse.success(
                "Sucesso",
                toResponseDTO(ticket)
        );
    }

    public ServiceResponse<List<TicketResponseDTO>> listAllTickets() {

        authContext.requirePermission(Permission.VIEW_ALL_TICKETS);

        List<TicketResponseDTO> response = ticketRepository
                .findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());

        return ServiceResponse.success("Sucesso", response);
    }



    public ServiceResponse<Boolean> deleteTicket(Long idTicket) {

        authContext.requirePermission(Permission.DELETE_TICKET);

        if (!ticketRepository.existsById(idTicket)) {
            return ServiceResponse.error("ID de ticket não encontrado");
        }

        ticketRepository.deleteById(idTicket);

        return ServiceResponse.success(
                "Ticket removido com sucesso",
                true
        );
    }



    public ServiceResponse<TicketResponseDTO> updateTicket(Long id, UpdateTicketDTO ticketDTO) {

        authContext.requirePermission(Permission.EDIT_TICKET);

        TicketModel ticket = ticketRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("ID de ticket não encontrado")
                );

        updateIfNotNull(ticketDTO.getTitle(), ticket::setTitle);
        updateIfNotNull(ticketDTO.getDescription(), ticket::setDescription);
        updateIfNotNull(ticketDTO.getPriority(), ticket::setPriority);
        updateIfNotNull(ticketDTO.getStatus(), ticket::setStatus);

        TicketModel saved = ticketRepository.save(ticket);

        return ServiceResponse.success(
                "Ticket alterado com sucesso",
                toResponseDTO(saved)
        );
    }



    private <T> void updateIfNotNull(T value, java.util.function.Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }


    private TicketResponseDTO toResponseDTO(TicketModel ticket) {

        return new TicketResponseDTO(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt(),
                ticket.getPriority(),
                ticket.getStatus(),
                ticket.getRequestingUser().getName(),
                ticket.getResponsibleUser().getName()
        );
    }

}