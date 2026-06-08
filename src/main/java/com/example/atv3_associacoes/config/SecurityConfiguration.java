package com.example.atv3_associacoes.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    @Autowired
    private UsuarioDetailsConfig usuarioDetailsConfig;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        // Libera login, css e assets públicos
                        .requestMatchers("/login", "/css/**", "/js/**").permitAll()

                        // Proteção das rotas de Clientes (Regras do seu commit estável)
                        .requestMatchers("/clientes/lista").hasAnyRole("ADMIN")
                        .requestMatchers("/clientes/form-pf", "/clientes/save-pf").hasAnyRole("ADMIN")
                        .requestMatchers("/clientes/form-pj", "/clientes/save-pj").hasAnyRole("ADMIN")

                        // Proteção das rotas de Produtos (Regras do seu commit estável)
                        .requestMatchers("/produtos/form", "/produtos/save").hasAnyRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/vendas/lista", true)
                        .permitAll()
                )
                .httpBasic(withDefaults())
                .logout(LogoutConfigurer::permitAll)
                .rememberMe(withDefaults());
        return http.build();
    }

    // PADRÃO DO PROFESSOR: Vincula o service e o BCrypt diretamente no construtor de autenticação global
    @Autowired
    public void configureUserDetails(final AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(usuarioDetailsConfig).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}