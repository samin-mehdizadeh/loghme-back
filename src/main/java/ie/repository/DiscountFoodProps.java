package ie.repository;

import java.util.ArrayList;

public class DiscountFoodProps {
    private DiscountFood discountFood;
    private Restaurant ownerRestaurant;

    public DiscountFood getDiscountFood() {
        return discountFood;
    }

    public void setDiscountFood(DiscountFood discountFood) {
        this.discountFood = discountFood;
    }

    public Restaurant getOwnerRestaurant() {
        return ownerRestaurant;
    }

    public void setOwnerRestaurant(Restaurant ownerRestaurant) {
        this.ownerRestaurant = ownerRestaurant;
    }
}
