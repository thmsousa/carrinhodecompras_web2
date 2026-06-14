package com.example.atv3_associacoes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        // 1. ROTAS PÚBLICAS (Navegação livre sem login)
                        .requestMatchers("/", "/login", "/cadastro", "/cadastro/**", "/css/**", "/js/**", "/img/**").permitAll()
                        .requestMatchers("/produtos/lista").permitAll()

                        // 2. ROTAS DE ADMIN (Bloqueio total por Role)
                        .requestMatchers("/clientes/**").hasAnyRole("ADMIN")
                        .requestMatchers("/produtos/form", "/produtos/save").hasAnyRole("ADMIN")

                        // 3. ROTAS DE CLIENTE LOGADO (Exige login para comprar ou ver histórico)
                        .requestMatchers("/vendas/**", "/carrinho/**").authenticated()

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        // O parâmetro false faz o Spring lembrar de onde o usuário veio antes de pedir o login
                        .defaultSuccessUrl("/produtos/lista", false)
                        .permitAll()
                )
                .httpBasic(withDefaults())
                .logout(LogoutConfigurer::permitAll)
                .rememberMe(withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}