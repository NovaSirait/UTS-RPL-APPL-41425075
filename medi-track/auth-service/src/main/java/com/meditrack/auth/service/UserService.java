package com.meditrack.auth.service;

import com.meditrack.auth.dto.RegisterRequest;
import com.meditrack.auth.entity.Role;
import com.meditrack.auth.entity.User;
import com.meditrack.auth.repository.RoleRepository;
import com.meditrack.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());

        Role.RoleType roleType;
        if (request.getRole() != null && request.getRole().equalsIgnoreCase("DOCTOR")) {
            roleType = Role.RoleType.DOCTOR;
        } else if (request.getRole() != null && request.getRole().equalsIgnoreCase("PHARMACIST")) {
            roleType = Role.RoleType.PHARMACIST;
        } else if (request.getRole() != null && request.getRole().equalsIgnoreCase("ADMIN")) {
            roleType = Role.RoleType.ADMIN;
        } else {
            roleType = Role.RoleType.PATIENT;
        }

        Role role = roleRepository.findByName(roleType)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleType));

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}