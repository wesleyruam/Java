package com.wesley.springboot.usuarios.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.wesley.springboot.usuarios.model.UserModel;
import com.wesley.springboot.usuarios.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;


    public ResponseEntity<UserModel> createUser(UserModel user){
        UserModel savedUser = userRepository.save(user); // Salva no banco
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    public List<UserModel> listUsers(){
        return userRepository.findAll();
    }

    public UserModel findByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado com id: " + id));
    }

    public ResponseEntity<String> deleteUser(Long id){
        if(userRepository.existsById(id)){
            userRepository.deleteById(id);
            return ResponseEntity.ok("Usuário deletado com sucesso");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }
    }
}