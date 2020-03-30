package ie.service;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.ArrayList;
import ie.repository.Manager;
import ie.repository.Restaurant;
@RestController
public class RestaurantService {
    @RequestMapping(value = "/restaurants", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Restaurant> getRestaurants() {
        List<Restaurant> restaurants = new ArrayList<Restaurant>();
        restaurants = Manager.getInstance().getRestaurants();
        return restaurants;
    }

    @RequestMapping(value = "/restaurantInfo/{id}", method = RequestMethod.GET)
    public Restaurant getAvailableSeats(@PathVariable(value = "id") String id) {
        return Manager.getInstance().getRestaurantById(id);
    }

}
