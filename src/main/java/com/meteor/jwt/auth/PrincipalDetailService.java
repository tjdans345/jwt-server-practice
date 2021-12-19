package com.meteor.jwt.auth;

import com.meteor.jwt.model.User;
import com.meteor.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// http:/localhost:8080/login <<< 시큐리티에 설정한경로로 로그인 요청이 올 때 동작을 하는 서비스 but 지금은 form 로그인을 사용하지않기 때문에 동작을 안한다.
@Service
@RequiredArgsConstructor
public class PrincipalDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("PrincipalDetailsService의 loadUserByUsername()" );
        User userEntity = userRepository.findByUsername(username);
        System.out.println("userEntity + " + userEntity);

        return new PrincipalDetails(userEntity);
    }


}
