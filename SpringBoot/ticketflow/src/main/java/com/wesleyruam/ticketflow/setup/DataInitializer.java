package com.wesleyruam.ticketflow.setup;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.wesleyruam.ticketflow.model.User.UserModel;
import com.wesleyruam.ticketflow.repository.User.UserRepository;
import com.wesleyruam.ticketflow.security.Role;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initUsers(UserRepository userRepository) {
        return args -> {

            if (userRepository.findByEmail("admin@mail.com").isEmpty()) {

                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

                UserModel admin = new UserModel();
                admin.setName("Admin");
                admin.setEmail("admin@mail.com");
                admin.setPassword(encoder.encode("123"));
                admin.setRole(Role.ADMIN);

                userRepository.save(admin);

                System.out.println("ADMIN criado");
            }

        };
    }
}