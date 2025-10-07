package com.gastos.gerenciadorfinanceiro.controller;

import com.gastos.gerenciadorfinanceiro.dto.UserResponseDTO;
import com.gastos.gerenciadorfinanceiro.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getLoggedInUser(@AuthenticationPrincipal User user) {
        UserResponseDTO userDto = new UserResponseDTO(user.getId(), user.getUsername(), user.getCreatedAt());
        return ResponseEntity.ok(userDto);
    }
}