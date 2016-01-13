package co.yolo.model;

/**
 * Created by scorsin on 1/13/16.
 */
public class Harvest {

    private double amount;
    private User pet;
    private User you;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public User getPet() {
        return pet;
    }

    public void setPet(User pet) {
        this.pet = pet;
    }

    public User getYou() {
        return you;
    }

    public void setYou(User you) {
        this.you = you;
    }
}
