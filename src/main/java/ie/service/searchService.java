package ie.service;

import ie.domain.Manager;
import ie.domain.*;
import ie.service.serviceDTO.RestaurantInfo;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class searchService {
    @RequestMapping(value = "/search", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RestaurantInfo> search(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "limit") int limit,
            @RequestParam(value = "food") String food,
            @RequestParam(value = "restaurant") String restaurant)
    {
        System.out.println("----------------------------------------------------------------------------------------");
        System.out.println("page "+page+"limit"+limit+"food "+food+"restaurant "+restaurant);
        List<RestaurantInfo> restaurantInfos = new ArrayList<>();
        List<Restaurant> searchedRestaurants = Manager.getInstance().getSearchedRestaurants(restaurant,food,page,limit);
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
