package com.wesley.springboot.usuarios.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wesley.springboot.usuarios.model.UserModel;
import com.wesley.springboot.usuarios.service.UserService;

@RestController
public class UserController {

    @Autowired
    private UserService userService;
    
    @GetMapping("/users")
    public List<UserModel> listUsers(){
        return userService.listUsers();
    }

    @PostMapping("/users")
    public ResponseEntity<UserModel> createUser(@RequestBody UserModel user){
        return userService.createUser(user);
    }

    @GetMapping("/users/{id}")
    public UserModel findById(@PathVariable Long id) {
        return userService.findByIdOrThrow(id);
    }
}