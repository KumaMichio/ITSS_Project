package security;

import lombok.Getter;
import models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import repositories.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.Arrays;

@Component
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                mapRoleToGrantedAuthority(user.getRole(), user.getEmail(), user.getId()));

    }

    private Collection<GrantedAuthority> mapRoleToGrantedAuthority(String role, String email, int id) {
        if (role == null) {
            return Collections.emptyList();
        }

        // Return both the custom format and the simple role format for compatibility
        return Arrays.asList(
                new CustomGrantedAuthority(role, email, id),
                new SimpleGrantedAuthority(role) // Add simple role authority
        );
    }

    private class CustomGrantedAuthority implements GrantedAuthority {

        private final String role;
        private final String email;

        private final int id;

        public CustomGrantedAuthority(String role, String email, int id) {
            this.role = role;
            this.email = email;
            this.id = id;
        }

        @Override
        public String getAuthority() {
            return role + "|" + email + "|" + id;
        }

        public String getRole() {
            return role;
        }

        public String getEmail() {
            return email;
        }

        public int getId() {
            return id;
        }
    }
}
