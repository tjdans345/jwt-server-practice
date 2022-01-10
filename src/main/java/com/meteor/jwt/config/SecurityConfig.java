package com.meteor.jwt.config;

import com.meteor.jwt.filter.MyFiler1;
import com.meteor.jwt.filter.MyFiler3;
import com.meteor.jwt.jwt.JwtAuthenticationFilter;
import com.meteor.jwt.jwt.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration // Ioc 등록
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CorsFilter corsFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder () {
        return new BCryptPasswordEncoder();
    }

    // 기본적으로 시큐리티 필터가 먼저 실행되고 커스텀 필터가 실행이된다.
    // 커스텀필터가 시큐리티 필터보다 먼저 실행되게 하려면 SecurityContextPersistenceFilter 보다 먼저 타게 설정하면 된다.
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 스프링 필터를 타기전에 먼저 커스텀한 필터를 타게 만든다.
//        http.addFilterBefore(new MyFiler3(), SecurityContextPersistenceFilter.class); // 따로 파일을 만들어서 걸어줘도 된다.
        http.csrf().disable();
        // 세션을 사용하지 않겠다는 설정 stateless 서버로 만들겠다
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(corsFilter) // cors 정책에서 벗어날 수 있게하는 설정 // @CrossOrigin(인증X), 시큐리티 필터에 등록 인증(O) << 인증이 필요할 때
                .formLogin().disable() // 여기까지는 jwt 방식을 사용할 때 고정이다.
                .httpBasic().disable() // Bearer 방식을 쓰기위한 설정
                .addFilter(new JwtAuthenticationFilter(authenticationManager())) // authenticationManager 라는 파라미터를 던져줘야한다. 매니저를 통해서 로그인을 진행하기 때문에
                .addFilter(new JwtAuthorizationFilter(authenticationManager())) // authenticationManager 라는 파라미터를 던져줘야한다. 매니저를 통해서 로그인을 진행하기 때문에
                .authorizeRequests()
                .antMatchers("/api/v1/user/**")
                .access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/api/v1/manager/**")
                .access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/api/v1/admin/**")
                .access("hasRole('ROLE_ADMIN')")
                .anyRequest()
                .permitAll();
    }
}
