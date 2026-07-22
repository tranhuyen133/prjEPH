package com.hrm.service;

import com.hrm.dto.AuthDtos.LoginRequest;
import com.hrm.dto.AuthDtos.LoginResponse;
import com.hrm.entity.User;
import com.hrm.exception.BusinessException;
import com.hrm.repository.UserRepository;
import com.hrm.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest req) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        } catch (BadCredentialsException e) {
            throw new BusinessException("Ten dang nhap hoac mat khau khong dung");
        }

        User user = userRepository.findByUsername(req.username())
                .orElseThrow(() -> new BusinessException("Khong tim thay tai khoan"));

        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());

        return new LoginResponse(
                token,
                "Bearer",
                user.getUsername(),
                user.getRole().name(),
                user.getNhanVien() != null ? user.getNhanVien().getId() : null,
                user.getNhanVien() != null ? user.getNhanVien().getHoTen() : user.getUsername(),
                permissionsFor(user.getRole().name())
        );
    }

    private List<String> permissionsFor(String role) {
        return switch (role) {
            case "ROLE_ADMIN" -> List.of("employees", "org", "attendance", "leaves", "payroll", "contracts", "reports");
            case "ROLE_MANAGER" -> List.of("employees:view", "attendance", "leaves:approve", "payroll", "reports");
            default -> List.of("attendance", "leaves", "payroll:me");
        };
    }
}
