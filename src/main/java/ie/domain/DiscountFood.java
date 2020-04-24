package ie.domain;

public class DiscountFood extends Food{
    private int count;
    private int oldPrice;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(int oldPrice) {
        this.oldPrice = oldPrice;
    }

    public void decreaseCount(int value) {count = count - value;}
}

