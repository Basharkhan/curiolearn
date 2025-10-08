package com.example.curiolearn;

import com.example.curiolearn.user.entity.Role;
import com.example.curiolearn.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

@SpringBootApplication
@RequiredArgsConstructor
public class CuriolearnApplication {
    private final RoleRepository roleRepository;

	public static void main(String[] args) {
		SpringApplication.run(CuriolearnApplication.class, args);
        System.out.println("App is running...");
	}

    @Bean
    CommandLineRunner runner() {
        return args -> {
            Optional<Role> role = roleRepository.findByName(Role.RoleName.STUDENT);
            if (role.isEmpty()) {
                Role role1 = new Role();
                role1.setName(Role.RoleName.STUDENT);
                roleRepository.save(role1);
            }
        };
    }
}
