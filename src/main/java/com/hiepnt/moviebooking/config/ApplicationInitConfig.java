package com.hiepnt.moviebooking.config;

import com.hiepnt.moviebooking.entity.User;
import com.hiepnt.moviebooking.entity.enums.Role;
import com.hiepnt.moviebooking.entity.enums.Status;
import com.hiepnt.moviebooking.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if(!userRepository.existsByEmail("admin@gmail.com")){
                PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
                User user = User.builder()
                        .email("admin@gmail.com")
                        .fullName("admin")
                        .password(passwordEncoder.encode("123456"))
                        .role(Role.ADMIN)
                        .isActive(Status.ACTIVE)
                        .build();
                userRepository.save(user);
                log.warn("Admin user has been created with default password: admin, please change it");
            }
        };
    }
}
