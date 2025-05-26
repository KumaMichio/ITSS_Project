package controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserServiceImpl userService;

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
        if(updatedUser != null)
            return ResponseEntity.ok(updatedUser);
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot change user role");
    }
}
