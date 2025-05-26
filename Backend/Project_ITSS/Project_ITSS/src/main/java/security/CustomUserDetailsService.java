package security;

@Component
public class CustomUserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), mapRoleToGrantedAuthority(user.getRole(), user.getEmail(), user.getId()));

    }

    private Collection<GrantedAuthority> mapRoleToGrantedAuthority(String role, String email, int id) {
        return role == null
                ? Collections.emptyList()
                : Collections.singletonList(new CustomGrantedAuthority(role, email, id));
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
