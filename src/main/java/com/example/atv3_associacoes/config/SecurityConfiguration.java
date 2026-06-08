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
                        // Libera login, cadastro, arquivos CSS e assets públicos
                        .requestMatchers("/login", "/cadastro/**", "/css/**", "/js/**").permitAll()

                        // Restrições de rotas de Clientes (Regras do seu repositório)
                        .requestMatchers("/clientes/lista").hasAnyRole("ADMIN")
                        .requestMatchers("/clientes/form-pf", "/clientes/save-pf").hasAnyRole("ADMIN")
                        .requestMatchers("/clientes/form-pj", "/clientes/save-pj").hasAnyRole("ADMIN")

                        // Restrições de rotas de Produtos
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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}