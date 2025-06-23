package tw.ispan.librarysystem.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import tw.ispan.librarysystem.exception.JwtAuthException;
import tw.ispan.librarysystem.util.JwtTool;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class JwtAspect {

    @Around("@annotation(tw.ispan.librarysystem.security.CheckJwt)")
    public Object checkJwt(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();

        // Authorization: Bearer Token
        String authHeader = req.getHeader("Authorization");
        if (authHeader == null) {
            throw new JwtAuthException("no Authorization");
        }
        System.out.println(authHeader);
        if (!authHeader.startsWith("Bearer ")) {
            throw new JwtAuthException("token format error");
        }
        String token = authHeader.substring(7);
        try {
            String email = JwtTool.parseToken(token);

            if (email == null) {
                throw new JwtAuthException("token 失效");
            }
            System.out.println("Token OK:" + email);

            return joinPoint.proceed();

        } catch (Exception e) {
            throw new JwtAuthException("token 無效");
        }

    }

}