package ie.initial;
import ie.domain.Manager;
import ie.repository.*;
import ie.domain.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;


@WebListener()
public class Setup implements ServletContextListener {@Override

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        urlReader urlreader = new urlReader();
        String result = null;
        try {
            result = urlreader.readURL("http://138.197.181.131:8080/restaurants");
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Restaurant> restaurants = new ArrayList<Restaurant>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            restaurants = Arrays.asList(mapper.readValue(result, Restaurant[].class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Manager.getInstance().addRestaurants(restaurants,"ordinary");
        //Manager.getInstance().getUsersDB();
        Manager.getInstance().recoverOrdersInDb();
        FoodPartyTimer foodPartyTimer = new FoodPartyTimer();
        foodPartyTimer.start();
    }




    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
