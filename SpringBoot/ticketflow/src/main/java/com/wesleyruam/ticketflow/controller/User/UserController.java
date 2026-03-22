package com.wesleyruam.ticketflow.controller.User;

import com.wesleyruam.ticketflow.dto.ServiceResponse;
import com.wesleyruam.ticketflow.dto.User.CreateUserDTO;
import com.wesleyruam.ticketflow.dto.User.UpdateUserDTO;
import com.wesleyruam.ticketflow.dto.User.UserResponseDTO;
import com.wesleyruam.ticketflow.service.User.UserService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    public ResponseEntity<ServiceResponse<UserResponseDTO>> createUser(@RequestBody CreateUserDTO createDTO) {
        ServiceResponse<UserResponseDTO> response = userService.createUser(createDTO);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping
    public ResponseEntity<ServiceResponse<List<UserResponseDTO>>> listAllUsers() {
        ServiceResponse<List<UserResponseDTO>> response = userService.listAllUsers();
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(403).body(response);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse<UserResponseDTO>> getUserById(@PathVariable Long id) {
        ServiceResponse<UserResponseDTO> response = userService.getUserById(id);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse<UserResponseDTO>> updateUser(
            @PathVariable Long id, 
            @RequestBody UpdateUserDTO updateDTO) {
        ServiceResponse<UserResponseDTO> response = userService.updateUser(id, updateDTO);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResponse<Void>> deleteUser(@PathVariable Long id) {
        ServiceResponse<Void> response = userService.deleteUser(id);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}