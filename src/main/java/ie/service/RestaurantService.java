package ie.service;
import ie.repository.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;

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

    @RequestMapping(value = "/DiscountFoods", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DiscountFoodProps> getDiscountFoods() {
        List<DiscountFoodProps> allDiscountFoods = new ArrayList<>();
        List<Restaurant> restaurants = Manager.getInstance().getRestaurants();
        for (Restaurant restaurant : restaurants) {
            if (!(restaurant.getFoodParty().isEmpty())) {
                List<DiscountFood> foodParties = restaurant.getFoodParty();

                for (DiscountFood food : foodParties) {
                    DiscountFoodProps temp = new DiscountFoodProps();
                    temp.setDiscountFood(food);
                    temp.setOwnerRestaurant(restaurant);
                    allDiscountFoods.add(temp);
                }
            }

        }
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
            DeliveryFinder deliveryChecker = new DeliveryFinder(Manager.getInstance().getClient().getCurrentBasket());
            deliveryChecker.search();
            Manager.getInstance().getClient().assignNewBasket();
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
                addPartyFoodResult.setMessage("مهلت غذا تمام شده استتتتت" );
                return addPartyFoodResult;
            }
        }

        Restaurant restaurant = Manager.getInstance().getRestaurantById(id);
        DiscountFood partyFood = restaurant.findPartyFood(food);
        if(partyFood == null){
            addPartyFoodResult.setStatus(402);
            addPartyFoodResult.setMessage("مهلت غذا تمام شده است");
            System.out.println("null");
            return addPartyFoodResult;
        }

        int status = Manager.getInstance().getClient().addPartyToCart(food, id, count);
        System.out.println("uuu");
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
            addPartyFoodResult.setMessage(" سفارش " + partyFood.getName() + " با موقیت به سبد افزوده شد ");
        }

        return addPartyFoodResult;
    }



    @RequestMapping(value = "/currentBasket", method = RequestMethod.GET)
    public Basket getCurrentBasket() {
        return Manager.getInstance().getClient().getCurrentBasket();
    }


    @RequestMapping(value = "/getDiscountFood/{inf}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public DiscountFood getDiscountFood(@PathVariable(value = "inf") String inf){
        String restaurantId = inf.split("-")[0];
        String food = inf.split("-")[1];
        Restaurant restaurant = Manager.getInstance().getRestaurantById(restaurantId);
        DiscountFood dFood = restaurant.findPartyFood(food);
        return dFood;

    }
}
