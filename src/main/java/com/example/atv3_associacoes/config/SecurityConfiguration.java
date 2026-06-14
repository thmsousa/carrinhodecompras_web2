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
                        // 1. TORNAR A LISTAGEM DE PRODUTOS PÚBLICA PARA A ABERTURA INICIAL FUNCIONAR
                        .requestMatchers("/", "/login", "/cadastro/**", "/produtos/lista", "/css/**", "/js/**", "/img/**").permitAll()

                        // Controle Administrativo (Apenas ADMIN)
                        .requestMatchers("/clientes/lista").hasRole("ADMIN")
                        .requestMatchers("/clientes/form-pf", "/clientes/save-pf").hasRole("ADMIN")
                        .requestMatchers("/clientes/form-pj", "/clientes/save-pj").hasRole("ADMIN")
                        .requestMatchers("/produtos/form", "/produtos/save").hasRole("ADMIN")

                        // 2. ADICIONAR AO CARRINHO E ROTAS DE VENDAS EXIGEM LOGINS
                        .requestMatchers("/produtos/adicionar").authenticated()
                        .requestMatchers("/vendas/**").authenticated()

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        // 3. SE LOGAR COM SUCESSO, CONTINUA NA LISTA DE PRODUTOS
                        .defaultSuccessUrl("/produtos/lista", true)
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