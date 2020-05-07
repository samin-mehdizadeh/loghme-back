package ie.domain;

import ie.repository.UserMapper;
//import io.jsonwebtoken.ExpiredJwtException;
//import io.jsonwebtoken.SignatureException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.web.filter.OncePerRequestFilter;
import ie.domain.JwtTokenUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;


import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebFilter("/*")
public class JwtAuthenticationFilter implements Filter {

    public JwtAuthenticationFilter() {
        // TODO Auto-generated constructor stub
    }


    public void destroy() {
        // TODO Auto-generated method stub
    }


    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String header = httpRequest.getHeader("Authorization");
        String username = null;
        String authToken = null;
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        System.out.println(path);
        System.out.println(header);
        if (header != null && header.startsWith("Bearer")) {
            authToken = header.replace("Bearer","");
            try {
                username = JwtTokenUtil.getInstance().getUserNameFromToken(authToken);
            } catch (IllegalArgumentException e) {
                httpResponse.setStatus(403);
                System.out.println("an error occured during getting username from token");
            } catch (ExpiredJwtException e) {
                httpResponse.setStatus(401);
                System.out.println("the token is expired and not valid anymore");
            } catch(SignatureException e){
                httpResponse.setStatus(403);
                System.out.println("Authentication Failed. Username or Password not valid.");
            }
        }


        if(path.contains("logout")){
            httpResponse.setStatus(200);
            chain.doFilter(request, response);
            return;
        }

        if((httpResponse.getStatus() == 200) && (username != null)){
            System.out.println("hello,200" + username);
            Client user = null;
            try {
                user = UserMapper.getInstance().selectUser(username,"",0);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (JwtTokenUtil.getInstance().validateToken(authToken, user)) {
                System.out.println("validate");
                System.out.println("authenticated user " + username + ", setting security context");
                httpResponse.setStatus(200);
                if((!path.contains("checkLogin")) && (!path.contains("checkSignUp"))){
                    Manager.getInstance().setClient(user);
                    chain.doFilter(request, response);
                }

                else{
                    RequestDispatcher requestDispatcher = request.getRequestDispatcher("/error/home");
                    requestDispatcher.forward(request, response);
                }
            }

            else {
                if(path.contains("checkLogin") || (path.contains("checkSignUp"))){
                    chain.doFilter(request, response);
                }

                else {
                    System.out.println("tt");
                    httpResponse.setStatus(401);
                    RequestDispatcher requestDispatcher = request.getRequestDispatcher("/error/login");
                    requestDispatcher.forward(request, response);

                    System.out.println("tt1");

                }
            }
        }

        else {
            if(path.contains("checkLogin") || (path.contains("checkSignUp")) || (path.contains("login")) || path.contains("signup")) {
                System.out.println("23");
                chain.doFilter(request, response);
            }

            else {
                System.out.println("hi");
                httpResponse.setStatus(401);
                RequestDispatcher requestDispatcher = request.getRequestDispatcher("/error/login");
                requestDispatcher.forward(request, response);
            }
        }
    }


    public void init(FilterConfig fConfig) throws ServletException {
        // TODO Auto-generated method stub
    }
}