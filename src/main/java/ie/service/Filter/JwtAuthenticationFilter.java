package ie.service.Filter;

import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import ie.domain.Client;
import ie.domain.JwtTokenUtil;
import ie.domain.Manager;
import ie.repository.UserMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;


import javax.servlet.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;



import java.util.Collections;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import org.springframework.web.bind.annotation.ModelAttribute;



public class JwtAuthenticationFilter implements Filter {

    public JwtAuthenticationFilter() {
        // TODO Auto-generated constructor stub
    }


    public void destroy() {
        // TODO Auto-generated method stub
    }

    public String verifyGoogle(String authToken) throws GeneralSecurityException, IOException {

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList("805689182939-0ga0omqkur2mmmo22066rphmi97d1qkt.apps.googleusercontent.com"))
                .build();




        GoogleIdToken idToken = verifier.verify(authToken);
        if (idToken != null) {
            Payload payload = idToken.getPayload();

            String userId = payload.getSubject();
            //System.out.println("User ID: " + userId);

            // Get profile information from payload
            String email = payload.getEmail();

            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");
            return email;

        } else {
            System.out.println("Invalid ID token.");
            return null;
        }
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
            if(path.contains("loginByGoogle")){
                /*if (request.getHeader("X-Requested-With") == null) {
                    // Without the `X-Requested-With` header, this request could be forged. Aborts.
                }*/
                try {
                    String email = this.verifyGoogle(authToken);
                    request.setAttribute("email",email);
                    chain.doFilter(request, response);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
                return;
            }
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
                    if(!path.contains("restaurant") || !path.contains("search") || !path.contains("FoodPartyTime"))
                        Manager.getInstance().setClient(user);
                    chain.doFilter(request, response);
                }

                else{
                    RequestDispatcher requestDispatcher = request.getRequestDispatcher("/error");
                    requestDispatcher.forward(request, response);
                }
            }

            else {
                if(path.contains("checkLogin") || (path.contains("checkSignUp"))){
                    chain.doFilter(request, response);
                }

                else {

                    httpResponse.setStatus(401);
                    RequestDispatcher requestDispatcher = request.getRequestDispatcher("/error");
                    requestDispatcher.forward(request, response);



                }
            }
        }

        else {
            if(path.contains("checkLogin") || (path.contains("checkSignUp")) || (path.contains("login")) || path.contains("signup")
                    || (path.contains("loginByGoogle"))) {
                chain.doFilter(request, response);
            }

            else {
               // System.out.println("hi");
                httpResponse.setStatus(401);
                RequestDispatcher requestDispatcher = request.getRequestDispatcher("/error");
                requestDispatcher.forward(request, response);
            }
        }
    }


    public void init(FilterConfig fConfig) throws ServletException {
        // TODO Auto-generated method stub
    }
}