package com.hrm.dto;

import java.util.List;

public class AuthDtos {

    public record LoginRequest(String username, String password) {}

    public record LoginResponse(
            String accessToken,
            String tokenType,
            String username,
            String role,
            Long nhanVienId,
            String hoTen,
            List<String> permissions
    ) {}
}
