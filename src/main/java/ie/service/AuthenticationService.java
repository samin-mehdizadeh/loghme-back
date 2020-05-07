package ie.service;

import ie.domain.Client;
import ie.domain.JwtTokenUtil;
import ie.repository.UserMapper;
import org.springframework.http.MediaType;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
public class AuthenticationService {


    @RequestMapping(value = "/checkLogin", method = RequestMethod.GET)
    public Result checkLogin() {
        Result result = new Result();
        result.setMessage("valid Req");
        result.setStatus(200);
        return result;
    }

    @RequestMapping(value = "/checkSignUp", method = RequestMethod.GET)
    public Result checkSignUp() {
        Result result = new Result();
        result.setMessage("valid Req");
        result.setStatus(200);
        return result;
    }
}
