package ie.initial;
import ie.repository.urlReader;
import ie.repository.Restaurant;
import ie.repository.Manager;
import ie.repository.Client;
import ie.repository.FoodPartyTimer;
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
        Manager.getInstance().setRestaurants(restaurants);
        Client client = new Client();
        client.setName("Haniyeh");
        client.setLastName("Nasseri");
        client.setPhoneNumber("09357463903");
        client.setEmailAddress("haniyeh.nasseri99@gmail.com");
        client.setCredit(200000);
        Manager.getInstance().setClient(client);

        FoodPartyTimer foodPartyTimer = new FoodPartyTimer();
        foodPartyTimer.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
