package co.yolo;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            String token = ".eJxFyjEKwCAMQNG7ZG5Ao8bY28So4FZwLL177dQ_ft4N65oNTnA7byNj5KYYcvcYRBJ-V5OaKyPCAevHTQ1ZhLF7Klir8cakFJ1poA7PC1PMF7Q.7ZDtYNbB8yHlkMR-MJTEuVk2S2w";
            StolenBuyer buyer = new StolenBuyer(BuyerMethod.EFFICIENT, token);
            buyer.startBecomingRich();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
