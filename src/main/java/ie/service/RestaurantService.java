package ie.service;
import ie.domain.Manager;
import ie.domain.*;
import ie.service.serviceDTO.DiscountFoodProps;
import ie.service.serviceDTO.RestaurantInfo;
import ie.service.serviceDTO.Result;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;

@RestController
public class RestaurantService {
    @RequestMapping(value = "/restaurants/{page}/{limit}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RestaurantInfo> getRestaurants(@PathVariable(value = "page") int page, @PathVariable(value = "limit") int limit) {
        List<Restaurant> restaurants = Manager.getInstance().getRestaurants(page,limit);
        List<RestaurantInfo> restaurantInfos = new ArrayList<>();
        for(Restaurant restaurant: restaurants){
            RestaurantInfo restaurantInfo = new RestaurantInfo();
            restaurantInfo.setId(restaurant.getId());
            restaurantInfo.setName(restaurant.getName());
            restaurantInfo.setLogo(restaurant.getLogo());
            restaurantInfos.add(restaurantInfo);
        }
        return restaurantInfos;
    }

    @RequestMapping(value = "/restaurantInfo/{id}", method = RequestMethod.GET)
    public Restaurant getRestaurant(@PathVariable(value = "id") String id) {
        return Manager.getInstance().getRestaurantById(id);
    }

    @RequestMapping(value = "/DiscountFoods", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DiscountFoodProps> getDiscountFoods() {
        List<DiscountFoodProps> allDiscountFoods = new ArrayList<>();
        allDiscountFoods  = Manager.getInstance().getDiscounts();
        return allDiscountFoods;
    }

    @RequestMapping(value = "/FoodPartyTime", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public FoodPartyCounter getFoodPartyTime() {
        System.out.println(FoodPartyCounter.getInstance().getRemainingTime());
        return FoodPartyCounter.getInstance();
    }


    @RequestMapping(value = "/buyFood", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Result buyTheFood(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "food") String food,
            @RequestParam(value = "count") int count){
        if(id.equals("current")){
            id = Manager.getInstance().getClient().getCurrentBasket().getRestaurantId();
        }
        int status = Manager.getInstance().getClient().addOrdinaryToCart(food, id,count);
        Result result = new Result();
        if(status == 0)
            result.setStatus(200);
        else
            result.setStatus(402);
        System.out.println(result.getStatus());
        return result;
    }

    @RequestMapping(value = "/decreasePartyFood", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Result descreasePartyFood(
            @RequestParam(value = "food") String food,
            @RequestParam(value = "id") String id){
        Result result = new Result();
        if(id.equals("current")){
            id = Manager.getInstance().getClient().getCurrentBasket().getRestaurantId();
            System.out.println(id);
            if(id == "null"){
                result.setStatus(402);
                result.setMessage("مهلت غذا تمام شده است");
                return result;
            }
        }
        int status = Manager.getInstance().getClient().decreasePartyFood(food);
        if(status == 0)
            result.setStatus(200);
        else {
            result.setStatus(402);
            result.setMessage("مهلت غذا تمام شده است");
        }
        return result;
    }

    @RequestMapping(value = "/finalizeOrder", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Result finalizeOrder(){
        System.out.println("finalize");
        int status = Manager.getInstance().getClient().finalizeOrder();
        Result result = new Result();
        if(status == -1){
            result.setStatus(402);
            result.setMessage("اعتبار کم است!");
            return result;

        }
        if(status == -2){
            result.setStatus(402);
            result.setMessage("سبد خالی است");
            return result;

        }
        else{
            result.setStatus(200);
            result.setMessage("خرید شما ثبت شد");
            return result;
        }

    }

    @RequestMapping(value = "/decreaseOrdinaryFood", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Result descreaseOrdinaryFood(
            @RequestParam(value = "food") String food){
        int status = Manager.getInstance().getClient().decreaseOrdinaryFood(food);
        Result result = new Result();
        if(status == 0)
            result.setStatus(200);
        else {
            result.setStatus(402);
            result.setMessage("مهلت غذا تمام شده است");
        }
        return result;
    }

    @RequestMapping(value = "/addDiscountFoodToBasket", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Result addDiscountFoodToBasket(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "food") String food,
            @RequestParam(value = "count") int count) {
        Result addPartyFoodResult = new Result();
        if(id.equals("current")){
            id = Manager.getInstance().getClient().getCurrentBasket().getRestaurantId();
            System.out.println("current");
            if(id == "null"){
                addPartyFoodResult.setStatus(402);
                addPartyFoodResult.setMessage("مهلت غذا تمام شده است" );
                return addPartyFoodResult;
            }
        }

        Restaurant restaurant = Manager.getInstance().getRestaurantById(id);
        System.out.println(restaurant.getId());
        DiscountFood partyFood = Manager.getInstance().getPartyFood(restaurant.getId(),food);
        if(partyFood == null){
            addPartyFoodResult.setStatus(402);
            addPartyFoodResult.setMessage("مهلت غذا تمام شده است");
            return addPartyFoodResult;
        }

        int status = Manager.getInstance().getClient().addPartyToCart(food, id, count);
        System.out.println(status);
        if (status == -3){
            addPartyFoodResult.setStatus(402);
            addPartyFoodResult.setMessage("اجازه خرید بیشتر ندارید");
        }

        if (status == -2){
            addPartyFoodResult.setStatus(402);
            addPartyFoodResult.setMessage("تعداد بیش از موجودی غذا!");
        }
        else if (status == -1){
            addPartyFoodResult.setStatus(402);
            addPartyFoodResult.setMessage("سفارش از رستوران دیگری است");
        }

        else if(status == 0){
            addPartyFoodResult.setStatus(200);
            addPartyFoodResult.setMessage(" سفارش " + partyFood.getName() + " با موفقیت به سبد افزوده شد ");
        }

        return addPartyFoodResult;
    }



    @RequestMapping(value = "/currentBasket", method = RequestMethod.GET)
    public Basket getCurrentBasket() {
        return
                Manager.getInstance().getClient().getCurrentBasket();
    }


    @RequestMapping(value = "/DiscountFood/{inf}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public DiscountFood getDiscountFood(@PathVariable(value = "inf") String inf){
        String restaurantId = inf.split("-")[0];
        String food = inf.split("-")[1];
        Restaurant restaurant = Manager.getInstance().getRestaurantById(restaurantId);
        DiscountFood dFood = Manager.getInstance().getPartyFood(restaurantId,food);
        return dFood;

    }
}
