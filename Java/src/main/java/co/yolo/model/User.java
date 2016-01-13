package co.yolo.model;

/**
 * Created by scorsin on 1/13/16.
 */
public class User extends Entity {

    private boolean isYou;
    private String name;
    private double netWorth;
    private User owner;
    private Sale lastsale;
    private int petSlots;
    private double petWorth;
    private boolean verified;
    private boolean youFollow;
    private boolean hasLoggedIn;
    private Identity[] identities;

    public boolean isYou() {
        return isYou;
    }

    public boolean isOwnedBy(User other) {
        if (this.owner == null || other == null) {
            return false;
        }
        return this.owner.equals(other);
    }

    public void setIsYou(boolean isYou) {
        this.isYou = isYou;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getNetWorth() {
        return netWorth;
    }

    public void setNetWorth(double netWorth) {
        this.netWorth = netWorth;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Sale getLastsale() {
        return lastsale;
    }

    public void setLastsale(Sale lastsale) {
        this.lastsale = lastsale;
    }

    public int getPetSlots() {
        return petSlots;
    }

    public void setPetSlots(int petSlots) {
        this.petSlots = petSlots;
    }

    public double getPetWorth() {
        return petWorth;
    }

    public void setPetWorth(double petWorth) {
        this.petWorth = petWorth;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isYouFollow() {
        return youFollow;
    }

    public void setYouFollow(boolean youFollow) {
        this.youFollow = youFollow;
    }

    public Identity[] getIdentities() {
        return identities;
    }

    public void setIdentities(Identity[] identities) {
        this.identities = identities;
    }

    public boolean isHasLoggedIn() {
        return hasLoggedIn;
    }

    public void setHasLoggedIn(boolean hasLoggedIn) {
        this.hasLoggedIn = hasLoggedIn;
    }
}
