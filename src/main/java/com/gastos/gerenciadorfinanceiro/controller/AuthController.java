package com.gastos.gerenciadorfinanceiro.controller;

import com.gastos.gerenciadorfinanceiro.dto.AuthRequestDTO;
import com.gastos.gerenciadorfinanceiro.dto.AuthResponseDTO;
import com.gastos.gerenciadorfinanceiro.model.User;
import com.gastos.gerenciadorfinanceiro.repository.UserRepository;
import com.gastos.gerenciadorfinanceiro.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody AuthRequestDTO dto) {
        if (userRepository.findByUsername(dto.username()).isPresent()){
            return ResponseEntity.badRequest().body("Este usuário ja existe");
        }
        User user = new User();
        user.setUsername(dto.username());

        String encodedPassword = passwordEncoder.encode(dto.password());
        user.setPassword(encodedPassword);

        userRepository.save(user);
        return ResponseEntity.ok("Usuario criado com sucesso");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> loginUser(@RequestBody AuthRequestDTO dto) {
        var authInputToken = new UsernamePasswordAuthenticationToken(dto.username(), dto.password());

        var authenticatedUser = authenticationManager.authenticate(authInputToken);

        var userDetails = (UserDetails) authenticatedUser.getPrincipal();

        var jwtToken = jwtService.generateToken(userDetails);

        var responseDTO = new AuthResponseDTO(jwtToken);

        return ResponseEntity.ok(responseDTO);
    }


}
