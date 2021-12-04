package com.meteor.jwt.config;

import com.meteor.jwt.filter.MyFiler1;
import com.meteor.jwt.filter.MyFiler2;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class FilterConfig {

    // 리퀘스트 요청이 올 때 동작함
    @Bean
    public FilterRegistrationBean<MyFiler1> filter1() {
        FilterRegistrationBean<MyFiler1> bean = new FilterRegistrationBean<>(new MyFiler1());
        bean.addUrlPatterns("/*"); // 모든 요청에 대해서 다해라
        bean.setOrder(2); // 낮은 번호가 필터중에서 가장 먼저 실행됨.
        return bean;
    }

    // 리퀘스트 요청이 올 때 동작함
    @Bean
    public FilterRegistrationBean<MyFiler2> filter2() {
        FilterRegistrationBean<MyFiler2> bean = new FilterRegistrationBean<>(new MyFiler2());
        bean.addUrlPatterns("/*"); // 모든 요청에 대해서 다해라
        bean.setOrder(1); // 낮은 번호가 필터중에서 가장 먼저 실행됨.
        return bean;
    }

}
