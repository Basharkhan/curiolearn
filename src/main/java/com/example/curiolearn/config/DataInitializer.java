package com.example.curiolearn.config;

import com.example.curiolearn.user.entity.Role;
import com.example.curiolearn.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        createRoleIfNotExists(Role.RoleName.ADMIN);
        createRoleIfNotExists(Role.RoleName.STUDENT);
        createRoleIfNotExists(Role.RoleName.INSTRUCTOR);
    }

    private void createRoleIfNotExists(Role.RoleName name) {
        roleRepository.findByName(name).orElseGet(() -> {
            Role role = new Role();
            role.setName(name);
            return roleRepository.save(role);
        });
    }
}
