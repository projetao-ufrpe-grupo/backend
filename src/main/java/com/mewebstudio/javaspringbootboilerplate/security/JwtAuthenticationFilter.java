package com.mewebstudio.javaspringbootboilerplate.security;

import java.io.IOException;
import java.util.Objects;

import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mewebstudio.javaspringbootboilerplate.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Profile("!mvcIT")
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    private final UserService userService;

    @Override
    protected final void doFilterInternal(@NonNull final HttpServletRequest request,
                                          @NonNull final HttpServletResponse response,
                                          @NonNull final FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = jwtTokenProvider.extractJwtFromRequest(request);
            log.info("Token extraído: {}", token != null ? "presente" : "ausente");
            
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token, request)) {
                log.info("Token é válido");
                String id = jwtTokenProvider.getUserIdFromToken(token);
                log.info("ID do usuário extraído: {}", id);
                UserDetails user = userService.loadUserById(id);
                log.info("Usuário carregado: {}", user != null ? user.getUsername() : "null");

                if (Objects.nonNull(user)) {
                    UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.info("Autenticação definida no contexto de segurança");
                }
            } else if (StringUtils.hasText(token)) {
                log.error("Token presente mas inválido");
            }
        } catch (Exception e) {
            log.error("Não foi possível definir a autenticação do usuário no contexto de segurança", e);
        }

        filterChain.doFilter(request, response);
        log.info("IP da requisição: {}", request.getRemoteAddr());
    }
}
