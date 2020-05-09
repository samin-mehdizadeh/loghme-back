package ie.service.serviceDTO;
import ie.domain.*;

import java.util.ArrayList;

public class DiscountFoodProps {
    private DiscountFood discountFood;
    private String ownerRestaurantName;
    private String ownerRestaurantID;
    public DiscountFood getDiscountFood() {
        return discountFood;
    }

    public void setDiscountFood(DiscountFood discountFood) {
        this.discountFood = discountFood;
    }

    public String getOwnerRestaurantName() {
        return ownerRestaurantName;
    }

    public String getOwnerRestaurantID() {
        return ownerRestaurantID;
    }

    public void setOwnerRestaurantID(String ownerRestaurantID) {
        this.ownerRestaurantID = ownerRestaurantID;
    }

    public void setOwnerRestaurantName(String ownerRestaurantName) {
        this.ownerRestaurantName = ownerRestaurantName;
    }
}
