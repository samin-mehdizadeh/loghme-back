package ie.service;

import ie.repository.Basket;
import ie.repository.Client;
import ie.domain.Manager;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
        int newCredit = Manager.getInstance().addCredit(credit);
        UserCredit userCredit = new UserCredit();
        userCredit.setCredit(newCredit);
        return userCredit;
    }

    @RequestMapping(value = "/Orders", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Basket> getOrders() {
        return Manager.getInstance().getClient().getBaskets();

    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Result signup(@RequestParam(value = "name") String name,
                         @RequestParam(value = "lastName") String lastName,
                         @RequestParam(value = "phone") String phone,
                         @RequestParam(value = "email") String email,
                         @RequestParam(value = "username") String username,
                         @RequestParam(value = "password") String password) {

        Result result = new Result();
        int stat = Manager.getInstance().addUser(name,lastName,phone,email,username,password);
        if(stat == 0){
            result.setMessage("نام کاربری یا ایمیل یا شماره موبایل قبلا در سیستم وارد شده. دوباره امتحان کنید.");
        }
        if(stat == 1){
            result.setMessage("به لقمه خوش آمدید.");
        }
        return result;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Result login(@RequestParam(value = "username") String username,
                         @RequestParam(value = "password") String password) {

        Result result = new Result();
        int stat = Manager.getInstance().setUser(username,password);
        if(stat == 0){
            result.setMessage("نام کاربری یا رمز عبور اشتباه است.");
        }
        if(stat == 1){
            result.setMessage("سلام!");
        }
        return result;
    }



}
