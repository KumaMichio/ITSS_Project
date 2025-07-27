package security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        System.out.println("🔍 JWT Filter - Processing request: " + request.getMethod() + " " + requestURI);

        String token = getJwtFromRequest(request);
        System.out.println("🔑 JWT Filter - Token present: " + (token != null));

        // ✅ Nếu KHÔNG có token, bỏ qua và để route tự xử lý (ví dụ permitAll)
        if (!StringUtils.hasText(token)) {
            System.out.println("❌ JWT Filter - No token found, continuing without authentication");
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ Nếu có token và hợp lệ thì xử lý
        if (jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsernameFromJWT(token);
            System.out.println("✅ JWT Filter - Valid token for user: " + username);

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            System.out.println("👤 JWT Filter - User authorities: " + userDetails.getAuthorities());

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            System.out.println("🎯 JWT Filter - Authentication set successfully");
        } else {
            System.out.println("❌ JWT Filter - Invalid token");
        }

        filterChain.doFilter(request, response);
    }

    // System.out.println(token);

    // if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
    // String username = jwtTokenProvider.getUsernameFromJWT(token);
    // UserDetails userDetails =
    // customUserDetailsService.loadUserByUsername(username);
    // UsernamePasswordAuthenticationToken authenticationToken = new
    // UsernamePasswordAuthenticationToken(
    // userDetails, null, userDetails.getAuthorities());
    // authenticationToken.setDetails(new
    // WebAuthenticationDetailsSource().buildDetails(request));
    // SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    // // List<String> roles = userDetails.getAuthorities().stream()
    // // .map(GrantedAuthority::getAuthority)
    // // .toList();
    // // List<String> infos = List.of(roles.get(0).split("\\|"));
    // // int userId = Integer.parseInt(infos.get(2));
    // // if (userId != 0) {
    // // request.setAttribute("userId", userId);
    // // }
    // }

    // filterChain.doFilter(request, response);
    // }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
