package ie.service;

import ie.domain.Manager;
import ie.domain.Restaurant;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwtErrorService {
    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public Result getRestaurant() {
        Result result = new Result();
        result.setMessage("unvalid request");
        result.setStatus(-1);
        return result;
    }
}
