package com.meteor.jwt.filter;


import javax.servlet.*;
import java.io.IOException;

public class MyFiler2 implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("필터2");
        // 프로세스를 계속 진행시키기 위해서는 체인에 넘겨줘야한다.
        chain.doFilter(request, response);
    }
}
