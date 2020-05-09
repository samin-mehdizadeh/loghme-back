package ie.service;

import ie.domain.*;
import ie.domain.Manager;
import ie.repository.UserMapper;
import ie.service.serviceDTO.Result;
import ie.service.serviceDTO.UserCredit;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.List;

//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class UserService {
    @ModelAttribute("email")
    public String getEmail(HttpServletRequest request)
    {
        return (String) request.getAttribute("email");
    }
    @RequestMapping(value = "/User", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Client getUser() {
        return Manager.getInstance().getClient();
    }

    @RequestMapping(value = "/addCredit", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public UserCredit addCredit(@RequestParam(value = "credit") int credit) {
        int newCredit = Manager.getInstance().addCredit(credit);
        UserCredit userCredit = new UserCredit();
        userCredit.setCredit(newCredit);
        return userCredit;
    }

    @RequestMapping(value = "/Orders", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Basket> getOrders() {
        Manager.getInstance().insertPreviousOrdersFromDb(Manager.getInstance().getClient().getUsername());
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

        Result result = new Result();
        Client user = UserMapper.getInstance().selectUser(username,password,1);
        if(user == null){
            result.setMessage("نام کاربری یا رمز عبور اشتباه است.");
        }
        else{
            String token = JwtTokenUtil.getInstance().generateToken(user);
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

    @RequestMapping(value = "/loginByGoogle", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Result login(@ModelAttribute("email") String email) throws SQLException {


        Result result = new Result();
        Client user = UserMapper.getInstance().selectUserByEmail(email);
        if(user == null){
            result.setMessage("نام کاربری یا رمز عبور اشتباه است.");
        }
        else{

            String token = JwtTokenUtil.getInstance().generateToken(user);
            result.setMessage("سلام!");
            result.setStatus(200);
            result.setToken(token);
        }
        return result;
    }

}
