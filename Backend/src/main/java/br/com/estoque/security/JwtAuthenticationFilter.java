package br.com.estoque.security;


import br.com.estoque.service.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtTokenUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;


    public JwtAuthenticationFilter(JwtUtil jwtTokenUtil, CustomUserDetailsService customUserDetailsService, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        System.out.println("Authorization header: " + request.getHeader("Authorization"));  // DEBUG

        String token = jwtTokenUtil.getTokenFromRequest(request);

        if (token != null) {
            try {
                if (jwtTokenUtil.validarToken(token)) {
                    String username = jwtTokenUtil.getUsernameFromToken(token);
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (ExpiredJwtException ex) {
                // Token expirado, invoca o AuthenticationEntryPoint
                SecurityContextHolder.clearContext();  // Limpa o contexto de segurança
                jwtAuthenticationEntryPoint.commence(request, response, new AuthenticationException("Token expirado") {});
                return;  // Não segue o filtro para que a exceção seja tratada
            } catch (JwtException | IllegalArgumentException ex) {
                // Token inválido ou malformado, invoca o AuthenticationEntryPoint
                SecurityContextHolder.clearContext();  // Limpa o contexto de segurança
                jwtAuthenticationEntryPoint.commence(request, response, new AuthenticationException("Token inválido") {});
                return;  // Não segue o filtro para que a exceção seja tratada
            }
        }

        chain.doFilter(request, response);
    }




}

