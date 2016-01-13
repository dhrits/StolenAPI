package co.yolo.model;

/**
 * Created by scorsin on 1/13/16.
 */
public class Meta {

    private int code;
    private double serverTime;
    private String errorMessage;
    private Wallet wallet;

    public double getServerTime() {
        return serverTime;
    }

    public void setServerTime(double serverTime) {
        this.serverTime = serverTime;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public boolean isSuccess() {
        return errorMessage == null;
    }
}
