package com.wesleyruam.ticketflow.service.User;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.wesleyruam.ticketflow.dto.ServiceResponse;
import com.wesleyruam.ticketflow.dto.User.CreateUserDTO;
import com.wesleyruam.ticketflow.dto.User.UpdateUserDTO;
import com.wesleyruam.ticketflow.dto.User.UserResponseDTO;
import com.wesleyruam.ticketflow.model.User.UserModel;
import com.wesleyruam.ticketflow.repository.User.UserRepository;
import com.wesleyruam.ticketflow.security.AuthContext;
import com.wesleyruam.ticketflow.security.Permission;
import com.wesleyruam.ticketflow.security.Role;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository; // auto injetor do UserRepository

    @Autowired
    private AuthContext authContext; // auto Injetor do AuthContext
    
    // Password encoder
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ServiceResponse<UserResponseDTO> createUser(CreateUserDTO createDTO) {
        try {
            // Verifica permissão
            authContext.requirePermission(Permission.CREATE_USER);

            // Pega usuário logado
            UserModel currentUser = authContext.getCurrentUser();

            // Verifica se email já existe
            if (userRepository.findByEmail(createDTO.getEmail()).isPresent()) {
                return ServiceResponse.error("Email já cadastrado");
            }

            // Cria o modelo a partir do DTO
            UserModel newUser = new UserModel();
            newUser.setName(createDTO.getName());
            newUser.setEmail(createDTO.getEmail());
            newUser.setPassword(passwordEncoder.encode(createDTO.getPassword())); // Criptografa senha
            
            // Define a role
            // Se for ADMIN e veio role no DTO, usa a role informada
            if (currentUser.hasPermission(Permission.EDIT_USER) && createDTO.getRole() != null) {
                newUser.setRole(createDTO.getRole());
            } else {
                newUser.setRole(Role.USER); // Default
            }

            // Salva usuário
            UserModel savedUser = userRepository.save(newUser);
            
            // Converte para DTO de resposta
            UserResponseDTO responseDTO = new UserResponseDTO(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.getCreatedAt()
            );
            
            return ServiceResponse.success("Usuário criado com sucesso", responseDTO);
            
        } catch (SecurityException e) {
            return ServiceResponse.error(e.getMessage());
        } catch (Exception e) {
            return ServiceResponse.error("Erro ao criar usuário: " + e.getMessage());
        }
    }
    
    public ServiceResponse<UserResponseDTO> updateUser(Long id, UpdateUserDTO updateDTO) {
        try {
            authContext.requirePermission(Permission.EDIT_USER);
            
            UserModel currentUser = authContext.getCurrentUser();
            UserModel userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            
            // Verifica se pode editar este usuário
            if (userToUpdate.getRole().ordinal() > currentUser.getRole().ordinal()) {
                return ServiceResponse.error("Não pode editar um usuário com nível superior");
            }
            
            // Atualiza dados
            if (updateDTO.getName() != null) {
                userToUpdate.setName(updateDTO.getName());
            }
            if (updateDTO.getEmail() != null) {
                userToUpdate.setEmail(updateDTO.getEmail());
            }
            
            // Só ADMIN pode mudar role
            if (currentUser.hasPermission(Permission.EDIT_USER) && updateDTO.getRole() != null) {
                userToUpdate.setRole(updateDTO.getRole());
            }
            
            UserModel saved = userRepository.save(userToUpdate);
            
            // Converte para DTO de resposta
            UserResponseDTO responseDTO = new UserResponseDTO(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getRole(),
                saved.getCreatedAt()
            );
            
            return ServiceResponse.success("Usuário atualizado com sucesso", responseDTO);
            
        } catch (SecurityException e) {
            return ServiceResponse.error(e.getMessage());
        } catch (Exception e) {
            return ServiceResponse.error("Erro ao atualizar usuário: " + e.getMessage());
        }
    }
    
    public ServiceResponse<UserResponseDTO> getUserById(Long id) {
        try {
            UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            
            UserResponseDTO responseDTO = new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
            );
            
            return ServiceResponse.success("Usuário encontrado", responseDTO);
            
        } catch (Exception e) {
            return ServiceResponse.error(e.getMessage());
        }
    }
    
    public ServiceResponse<List<UserResponseDTO>> listAllUsers() {
        try {
            authContext.requirePermission(Permission.VIEW_ALL_USERS);
            
            List<UserModel> users = userRepository.findAll();
            
            List<UserResponseDTO> responseDTOs = users.stream()
                .map(user -> new UserResponseDTO(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole(),
                    user.getCreatedAt()
                ))
                .collect(Collectors.toList());
            
            return ServiceResponse.success("Usuários listados com sucesso", responseDTOs);
            
        } catch (SecurityException e) {
            return ServiceResponse.error(e.getMessage());
        } catch (Exception e) {
            return ServiceResponse.error("Erro ao listar usuários: " + e.getMessage());
        }
    }
    
    public ServiceResponse<Void> deleteUser(Long id) {
        try {
            authContext.requirePermission(Permission.DELETE_USER);
            
            if (!userRepository.existsById(id)) {
                return ServiceResponse.error("Usuário não encontrado");
            }
            
            userRepository.deleteById(id);
            return ServiceResponse.success("Usuário deletado com sucesso");
            
        } catch (SecurityException e) {
            return ServiceResponse.error(e.getMessage());
        } catch (Exception e) {
            return ServiceResponse.error("Erro ao deletar usuário: " + e.getMessage());
        }
    }
}