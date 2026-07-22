package com.hrm.service;

import com.hrm.entity.User;
import com.hrm.repository.UserRepository;
import com.hrm.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public User get() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userRepository.findById(principal.getUserId()).orElseThrow();
    }

    public Long currentNhanVienId() {
        User u = get();
        return u.getNhanVien() != null ? u.getNhanVien().getId() : null;
    }

    public boolean isAdmin() {
        return get().getRole().name().equals("ROLE_ADMIN");
    }

    public boolean isAdminOrManager() {
        String r = get().getRole().name();
        return r.equals("ROLE_ADMIN") || r.equals("ROLE_MANAGER");
    }
}
