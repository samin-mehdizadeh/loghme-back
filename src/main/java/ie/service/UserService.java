package ie.service;

import ie.repository.Basket;
import ie.repository.Client;
import ie.repository.Manager;
import ie.repository.Restaurant;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class UserService {

    @RequestMapping(value = "/User", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Client getUser() {
        return Manager.getInstance().getClient();
    }

    @RequestMapping(value = "/addCredit", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public UserCredit addCredit(@RequestParam(value = "credit") int credit) {
        System.out.println(credit);
        int newCredit = Manager.getInstance().getClient().getCredit() + credit;
        Manager.getInstance().getClient().setCredit(newCredit);
        UserCredit userCredit = new UserCredit();
        userCredit.setCredit(newCredit);
        return userCredit;
    }

    @RequestMapping(value = "/Orders", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Basket> getOrders() {
        /*Basket b = new Basket("3");
        b.setRestaurantName("Mac");
        b.setStatus("Delivering");
        b.setRemainingTime(35);
        Manager.getInstance().getClient().getBaskets().add(b);*/
        return Manager.getInstance().getClient().getBaskets();

    }

}
