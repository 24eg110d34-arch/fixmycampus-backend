package com.fixmycampus.config;

import com.fixmycampus.model.Department;
import com.fixmycampus.model.Role;
import com.fixmycampus.model.User;
import com.fixmycampus.repository.DepartmentRepository;
import com.fixmycampus.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/** Runs at start-up: creates the maintenance departments and the first admin account if missing. */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final DepartmentRepository departments;
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final String adminName;
    private final String adminEmail;
    private final String adminPassword;

    public DataSeeder(DepartmentRepository departments,
                      UserRepository users,
                      PasswordEncoder encoder,
                      @Value("${app.admin.name}") String adminName,
                      @Value("${app.admin.email}") String adminEmail,
                      @Value("${app.admin.password}") String adminPassword) {
        this.departments = departments;
        this.users = users;
        this.encoder = encoder;
        this.adminName = adminName;
        this.adminEmail = adminEmail.trim().toLowerCase();
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        if (departments.count() == 0) {
            List.of("Electrical", "Plumbing", "Housekeeping", "IT & Network", "Carpentry & Furniture", "General Maintenance")
                    .forEach(name -> departments.save(new Department(name)));
            log.info("Created default departments");
        }

        if (!users.existsByEmail(adminEmail)) {
            User admin = new User();
            admin.setName(adminName);
            admin.setEmail(adminEmail);
            admin.setPassword(encoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            users.save(admin);
            log.info("Created admin account: {}", adminEmail);
        }
    }
}
