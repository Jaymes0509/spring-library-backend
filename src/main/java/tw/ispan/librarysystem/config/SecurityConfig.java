package tw.ispan.librarysystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity  //  啟用 @PreAuthorize 等註解
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // ⚠️ 暫時關閉 CSRF（方便 POST 測試）
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/members/register").permitAll() // ✅ 開放註冊
                        .anyRequest().permitAll() // 👉 暫時開放全部，如需安全可改 authenticated()
                );
        return http.build();
    }
}
