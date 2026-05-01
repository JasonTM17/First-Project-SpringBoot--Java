package vn.hoidanit.laptopshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;

import jakarta.servlet.DispatcherType;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();

    }

    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return new CustomSuccessHandler();
    }

    @Bean
    public SpringSessionRememberMeServices rememberMeServices() {
        SpringSessionRememberMeServices rememberMeServices
                = new SpringSessionRememberMeServices();
        rememberMeServices.setAlwaysRemember(true);
        return rememberMeServices;
    }

    @Bean
    public AuthenticationEntryPoint apiAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(401);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"message\":\"Bạn cần đăng nhập để thực hiện thao tác này\"}");
        };
    }

    @Bean
    public AccessDeniedHandler apiAccessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(403);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"message\":\"Phiên làm việc không hợp lệ hoặc bạn không có quyền thực hiện thao tác này\"}");
        };
    }

    @Bean
    SecurityFilterChain filterChain(
            HttpSecurity http,
            PasswordEncoder passwordEncoder,
            UserDetailsService userDetailsService,
            AuthenticationEntryPoint apiAuthenticationEntryPoint,
            AccessDeniedHandler apiAccessDeniedHandler) throws Exception {
        AuthenticationEntryPoint loginAuthenticationEntryPoint = new LoginUrlAuthenticationEntryPoint("/login");
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        authProvider.setHideUserNotFoundExceptions(false);

        http
                .authenticationProvider(authProvider)
                .authorizeHttpRequests(authorize -> authorize
                .dispatcherTypeMatchers(DispatcherType.ERROR, DispatcherType.FORWARD, DispatcherType.INCLUDE).permitAll()
                .requestMatchers("/", "/about", "/login", "/register", "/products", "/product/**", "/error", "/access-deny",
                        "/favicon.ico", "/site.webmanifest", "/robots.txt", "/sitemap.xml",
                        "/client/**", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/api/products/suggest").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/account/**", "/order-history", "/cart", "/checkout", "/confirm-checkout",
                        "/place-order", "/thanks", "/add-product-to-cart/**", "/delete-cart-product/**",
                        "/add-product-from-view-detail", "/api/**").authenticated()
                .anyRequest().permitAll())
                //Session
                .sessionManagement((sessionManagement) -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .invalidSessionStrategy((request, response) -> {
                    if (isApiRequest(request)) {
                        apiAuthenticationEntryPoint.commence(request, response, null);
                        return;
                    }
                    response.sendRedirect(request.getContextPath() + "/logout?expired");
                })
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false))
                .logout(logout -> logout.deleteCookies("JSESSIONID").invalidateHttpSession(true))
                .rememberMe(r -> r.rememberMeServices(rememberMeServices()))
                //form login
                .formLogin(formLogin -> formLogin
                .loginPage("/login")
                .failureUrl("/login?error")
                .successHandler(customSuccessHandler())
                .permitAll()
                )
                .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    if (isApiRequest(request)) {
                        apiAuthenticationEntryPoint.commence(request, response, authException);
                        return;
                    }
                    loginAuthenticationEntryPoint.commence(request, response, authException);
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    if (isApiRequest(request)) {
                        apiAccessDeniedHandler.handle(request, response, accessDeniedException);
                        return;
                    }
                    response.setStatus(403);
                    request.getRequestDispatcher("/access-deny").forward(request, response);
                }));
        return http.build();
    }

    private boolean isApiRequest(jakarta.servlet.http.HttpServletRequest request) {
        return request.getRequestURI().startsWith(request.getContextPath() + "/api/")
                || request.getServletPath().startsWith("/api/");
    }
}
