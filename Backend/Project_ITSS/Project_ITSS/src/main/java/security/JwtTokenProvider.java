package security;

@Component
public class JwtTokenProvider {
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + SecurityConstants.JWT_EXPIRATION);
        return Jwts.builder().setSubject(username).setIssuedAt((new Date())).setExpiration(expirationDate).signWith(SignatureAlgorithm.HS512, SecurityConstants.JWT_SECRET).compact();
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser().setSigningKey(SecurityConstants.JWT_SECRET).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SecurityConstants.JWT_SECRET).parseClaimsJws(token);
            return true;
        } catch (Exception ex){
            throw new AuthenticationCredentialsNotFoundException("JWT was expired or incorrect");
        }
    }
}
