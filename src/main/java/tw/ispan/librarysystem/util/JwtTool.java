package tw.ispan.librarysystem.util;

import java.security.Key;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

public class JwtTool {
    // 延長 token 有效時間到 24 小時
    private static final long EXP_TIME = 24 * 60 * 60 * 1000; // 24小時


    // private static final long EXP_TIME = 10 * 1000; // 10秒

    private static final String SECRET = "A9f2kLm8Qw7zX1pR4sV6bT0yH3jN5uCk";
    private static final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    /**
     * 產生 JWT Token
     * 
     * @param email 使用者 email
     * @return JWT token 字串
     */
    public static String createToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXP_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 JWT Token 並取得 email
     * 
     * @param token JWT token
     * @return 使用者 email
     * @throws RuntimeException 當 token 無效時拋出
     */
    public static String parseToken(String token) {
        try {
            JwtParser parser = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build();

            Claims claims = parser.parseClaimsJws(token).getBody();
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token 已過期");
        } catch (UnsupportedJwtException e) {
            throw new RuntimeException("不支援的 JWT");
        } catch (MalformedJwtException e) {
            throw new RuntimeException("JWT 格式錯誤");
        } catch (SignatureException e) {
            throw new RuntimeException("JWT 簽名驗證失敗");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("JWT 字串為空");
        }
    }

    /**
     * 驗證 JWT Token 是否有效
     * 
     * @param token JWT token
     * @return true 如果 token 有效，false 如果無效
     */
    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 取得 JWT Token 的過期時間
     * 
     * @param token JWT token
     * @return 過期時間的 Date 物件
     * @throws RuntimeException 當 token 無效時拋出
     */
    public static Date getExpirationTime(String token) {
        try {
            JwtParser parser = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build();

            Claims claims = parser.parseClaimsJws(token).getBody();
            return claims.getExpiration();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token 已過期");
        } catch (UnsupportedJwtException e) {
            throw new RuntimeException("不支援的 JWT");
        } catch (MalformedJwtException e) {
            throw new RuntimeException("JWT 格式錯誤");
        } catch (SignatureException e) {
            throw new RuntimeException("JWT 簽名驗證失敗");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("JWT 字串為空");
        }
    }
}