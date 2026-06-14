package com.example.atv3_associacoes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/cadastro/**", "/css/**", "/js/**", "/img/**").permitAll()

                        // Controle Administrativo
                        .requestMatchers("/clientes/lista").hasRole("ADMIN")
                        .requestMatchers("/clientes/form-pf", "/clientes/save-pf").hasRole("ADMIN")
                        .requestMatchers("/clientes/form-pj", "/clientes/save-pj").hasRole("ADMIN")
                        .requestMatchers("/produtos/form", "/produtos/save").hasRole("ADMIN")

                        // Escopo do usuário e rotas de vendas gerais
                        .requestMatchers("/produtos/lista", "/produtos/adicionar").authenticated()
                        .requestMatchers("/vendas/**").authenticated()

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/vendas/lista", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}