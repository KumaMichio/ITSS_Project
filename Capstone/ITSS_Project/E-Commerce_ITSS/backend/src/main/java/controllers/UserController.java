package controllers;

import jakarta.validation.Valid;
import models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import repositories.UserRepository;
import services.UserService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/user")
    public ResponseEntity<User> createUser(@RequestBody User userData) {
        System.out.println("🔄 UserController - Create user request received");
        System.out.println("📝 UserController - User data: " + userData.getUsername() + ", " + userData.getEmail()
                + ", role: " + userData.getRole());

        // Debug: Check password field
        System.out.println("🔍 UserController - Password received: "
                + (userData.getPassword() != null ? "YES (length: " + userData.getPassword().length() + ")" : "NULL"));

        // Debug: Print raw request info if possible
        System.out.println("🔍 UserController - Full user object: " + userData.toString());

        // Debug: Check current authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            System.out.println("🔍 Current user: " + authentication.getName());
            System.out.println("🔍 Current authorities: " + authentication.getAuthorities());
            authentication.getAuthorities().forEach(auth -> {
                System.out.println("  - Authority: " + auth.getAuthority());
            });
        } else {
            System.out.println("❌ No authentication found");
        }

        // Check if password is null and return early with error
        if (userData.getPassword() == null || userData.getPassword().trim().isEmpty()) {
            System.out.println("❌ UserController - Password is null or empty");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password cannot be null or empty");
        }

        // Check if username already exists
        if (userRepository.existsByUsername(userData.getUsername())) {
            System.out.println("❌ UserController - Username already exists: " + userData.getUsername());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is already taken");
        }

        // Encode password and create user
        userData.setPassword(passwordEncoder.encode(userData.getPassword()));
        User createdUser = userService.createUser(userData);

        System.out.println("✅ UserController - User created successfully: " + createdUser.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/update-user/{id}")
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user, @PathVariable int id) {
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user")
    public ResponseEntity<List<User>> getUser() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/user/role/{id}")
    public ResponseEntity<User> changeUserRole(@PathVariable int id, @RequestParam String role) {

        User updatedUser = userService.changeUserRole(id, role);
        if (updatedUser != null)
            return ResponseEntity.ok(updatedUser);
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot change user role");
    }

    @GetMapping("/user/test-auth")
    public ResponseEntity<String> testAuth() {
        System.out.println("🧪 UserController - Test auth endpoint called");
        return ResponseEntity.ok("Authentication successful!");
    }
}
