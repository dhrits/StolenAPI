package co.yolo.model;

/**
 * Created by scorsin on 1/13/16.
 */
public class Sale {

    private double canonicalPrice;
    private double currentHarvestAmount;
    private double decayRate;
    private double displayPrice;
    private double lastHarvestDate;
    private double lastSaleDate;
    private double lastSalePrice;
    private String ownerId;
    private String purchaseUuid;
    private double secondsSinceHarvest;
    private double secondsSinceSale;
    private long totalTimesPurchased;

    public double getCanonicalPrice() {
        return canonicalPrice;
    }

    public void setCanonicalPrice(double canonicalPrice) {
        this.canonicalPrice = canonicalPrice;
    }

    public double getCurrentHarvestAmount() {
        return currentHarvestAmount;
    }

    public void setCurrentHarvestAmount(double currentHarvestAmount) {
        this.currentHarvestAmount = currentHarvestAmount;
    }

    public double getDecayRate() {
        return decayRate;
    }

    public void setDecayRate(double decayRate) {
        this.decayRate = decayRate;
    }

    public double getDisplayPrice() {
        return displayPrice;
    }

    public void setDisplayPrice(double displayPrice) {
        this.displayPrice = displayPrice;
    }

    public double getLastHarvestDate() {
        return lastHarvestDate;
    }

    public void setLastHarvestDate(double lastHarvestDate) {
        this.lastHarvestDate = lastHarvestDate;
    }

    public double getLastSaleDate() {
        return lastSaleDate;
    }

    public void setLastSaleDate(double lastSaleDate) {
        this.lastSaleDate = lastSaleDate;
    }

    public double getLastSalePrice() {
        return lastSalePrice;
    }

    public void setLastSalePrice(double lastSalePrice) {
        this.lastSalePrice = lastSalePrice;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getPurchaseUuid() {
        return purchaseUuid;
    }

    public void setPurchaseUuid(String purchaseUuid) {
        this.purchaseUuid = purchaseUuid;
    }

    public double getSecondsSinceHarvest() {
        return secondsSinceHarvest;
    }

    public void setSecondsSinceHarvest(double secondsSinceHarvest) {
        this.secondsSinceHarvest = secondsSinceHarvest;
    }

    public double getSecondsSinceSale() {
        return secondsSinceSale;
    }

    public void setSecondsSinceSale(double secondsSinceSale) {
        this.secondsSinceSale = secondsSinceSale;
    }

    public long getTotalTimesPurchased() {
        return totalTimesPurchased;
    }

    public void setTotalTimesPurchased(long totalTimesPurchased) {
        this.totalTimesPurchased = totalTimesPurchased;
    }
}
