package io.github.kawakaze430.finance;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
@Component
public class RequestIdFilter extends OncePerRequestFilter {
    @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain) throws ServletException,IOException {
        String id=request.getHeader("X-Request-ID");
        if(id==null || !id.matches("[A-Za-z0-9-]{1,64}")) id=UUID.randomUUID().toString();
        request.setAttribute("requestId",id); response.setHeader("X-Request-ID",id); MDC.put("requestId",id);
        try { chain.doFilter(request,response); } finally { MDC.remove("requestId"); }
    }
}
