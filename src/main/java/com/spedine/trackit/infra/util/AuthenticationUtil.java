package com.spedine.trackit.infra.util;

import com.spedine.trackit.model.User;
import com.spedine.trackit.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationUtil {

    private final UserService userService;

    public AuthenticationUtil(UserService userService) {
        this.userService = userService;
    }

    public User getCurrentUser() {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return userService.findByEmail(email);
    }
}
