package com.wesleyruam.ticketflow.service.Auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.wesleyruam.ticketflow.dto.ServiceResponse;
import com.wesleyruam.ticketflow.dto.Auth.LoginRequestDTO;
import com.wesleyruam.ticketflow.dto.Auth.LoginResponseDTO;
import com.wesleyruam.ticketflow.model.User.UserModel;
import com.wesleyruam.ticketflow.repository.User.UserRepository;
import com.wesleyruam.ticketflow.security.JwtUtils;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public ServiceResponse<LoginResponseDTO> login(LoginRequestDTO loginDTO) {
        try {

            UserModel user = userRepository.findByEmail(loginDTO.getEmail())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
                return ServiceResponse.error("Senha inválida");
            }

            String token = jwtUtils.generateToken(user.getEmail());

            LoginResponseDTO responseDTO = new LoginResponseDTO(
                    token,
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole()
            );

            return ServiceResponse.success("Login realizado com sucesso", responseDTO);

        } catch (Exception e) {
            return ServiceResponse.error("Erro no login: " + e.getMessage());
        }
    }
}