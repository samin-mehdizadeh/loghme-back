package ie.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class jwt {
    @RequestMapping(value = "/jwt", method = RequestMethod.GET)
    public Result getRestaurant() {
        Result result = new Result();
        result.setMessage("valid JWT");
        result.setStatus(200);
        return result;
    }
}
