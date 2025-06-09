package com.powerchp.chpmanager.config;

import com.powerchp.chpmanager.service.CustomUserDetailsService;
import com.powerchp.chpmanager.config.CustomAuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationSuccessHandler successHandler;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService,
                          CustomAuthenticationSuccessHandler successHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.successHandler = successHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // فعال‌سازی CSRF با Token در کوکی (جهت فرم‌ها)
                .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))

                // مجوزها
                .authorizeHttpRequests(auth -> auth
                        // مسیرهای عمومی
                        .requestMatchers("/login", "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()

                        // فقط ادمین‌ها می‌توانند کارمند جدید اضافه کنند
                        .requestMatchers("/employee/**").hasRole("ADMIN")

                        // فقط ادمین‌ها اجازه ایجاد، ویرایش یا حذف شیفت دارند
                        .requestMatchers("/shifts/new", "/shifts/edit/**", "/shifts/delete/**").hasRole("ADMIN")

                        // اپراتورها و ادمین‌ها می‌توانند لیست شیفت‌ها و PDF را مشاهده کنند
                        .requestMatchers("/shifts", "/shifts/{id}", "/shifts/pdf/**").hasAnyRole("OPERATOR", "ADMIN")

                        // سایر مسیرها نیاز به احراز هویت دارند
                        .anyRequest().authenticated()
                )

                // فرم لاگین
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(successHandler)
                        .permitAll()
                )

                // خروج از سیستم
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )

                // صفحه خطای دسترسی
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/access-denied")
                )

                // مجوز برای iframe (در صورت استفاده از H2 Console یا غیره)
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    // رمزنگاری پسورد
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Provider برای احراز هویت با دیتابیس
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // مدیر احراز هویت
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
