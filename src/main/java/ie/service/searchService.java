package ie.service;

import ie.domain.Manager;
import ie.repository.Restaurant;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class searchService {
    @RequestMapping(value = "/search", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RestaurantInfo> search(
            @RequestParam(value = "food") String food,
            @RequestParam(value = "restaurant") String restaurant){

        List<RestaurantInfo> restaurantInfos = new ArrayList<>();
        List<Restaurant> searchedRestaurants = Manager.getInstance().getSearchedRestaurants(restaurant,food);
        for(Restaurant _restaurant: searchedRestaurants){
            RestaurantInfo restaurantInfo = new RestaurantInfo();
            restaurantInfo.setId(_restaurant.getId());
            restaurantInfo.setName(_restaurant.getName());
            restaurantInfo.setLogo(_restaurant.getLogo());
            restaurantInfos.add(restaurantInfo);
        }
        return restaurantInfos;
    }
}
