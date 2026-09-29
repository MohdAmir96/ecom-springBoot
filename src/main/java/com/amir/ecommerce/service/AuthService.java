package com.amir.ecommerce.service;

import com.amir.ecommerce.dto.AuthRequest;
import com.amir.ecommerce.dto.AuthResponse;
import com.amir.ecommerce.model.Users;
import com.amir.ecommerce.repo.UsersRepo;
import com.amir.ecommerce.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UsersRepo usersRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(AuthRequest request) {
        if (usersRepo.existsByUserName(request.userName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already registered");
        }

        Users user = new Users();
        user.setUserName(request.userName());
        user.setPassword(passwordEncoder.encode(request.password()));
        Users savedUser = usersRepo.save(user);
        return tokenResponse(savedUser.getUserName());
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.userName(), request.password()));
        return tokenResponse(request.userName());
    }

    private AuthResponse tokenResponse(String username) {
        return new AuthResponse("Bearer", jwtService.generateToken(username));
    }
}
