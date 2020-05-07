package ie.service;

import ie.domain.*;
import ie.domain.Manager;
import ie.repository.OrderMapper;
import ie.repository.UserMapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

//@CrossOrigin(origins = "*", maxAge = 3600)
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
        String username = Manager.getInstance().getClient().getUsername();
        int price = OrderMapper.getInstance().recoverOrdersAndGetAdditionalPrice(username);
        Manager.getInstance().addCredit(price);
        Manager.getInstance().insertPreviousOrdersFromDb(username);
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
                        @RequestParam(value = "password") String password) throws SQLException {

        System.out.println("yyyyyyyyyyyyyyyyyyyyyyyyyyy");

        Result result = new Result();
        Client user = UserMapper.getInstance().selectUser(username,password,1);
        if(user == null){
            System.out.println("tttttttttttttttttttttttttttttt");
            result.setMessage("نام کاربری یا رمز عبور اشتباه است.");
        }
        else{
            //authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            System.out.println("eeeeeeeeeeeeeeee");

            String token = JwtTokenUtil.getInstance().generateToken(user);
            System.out.println("hanie");
            System.out.println(token);
            result.setMessage("سلام!");
            result.setToken(token);
        }
        return result;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public Result logout() {
        UserMapper.getInstance().deleteUserBasket(Manager.getInstance().getClient().getUsername());
        Result result = new Result();
        result.setMessage("بدرود!");
        result.setStatus(200);
        result.setToken("");
        return result;
    }

}
