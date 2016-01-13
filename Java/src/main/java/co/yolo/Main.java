package co.yolo;

import me.corsin.javatools.http.ServerRequest;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            StolenBuyer buyer = new StolenBuyer(".eJxFyjEKwCAMQNG7ZG5Ao8bY28So4FZwLL177dQ_ft4N65oNTnA7byNj5KYYcvcYRBJ-V5OaKyPCAevHTQ1ZhLF7Klir8cakFJ1poA7PC1PMF7Q.7ZDtYNbB8yHlkMR-MJTEuVk2S2w");
            buyer.startBecomingRich();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
