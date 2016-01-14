package co.yolo;

import co.yolo.model.*;
import me.corsin.javatools.http.HttpMethod;
import me.corsin.javatools.task.TaskQueue;
import me.corsin.javatools.task.ThreadedConcurrentTaskQueue;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * Created by scorsin on 1/13/16.
 */
public class StolenBuyer {

    private BuyerMethod buyerMethod;
    private StolenAPI api;

    public StolenBuyer(BuyerMethod buyerMethod, String token) throws IOException {
        this.buyerMethod = buyerMethod;
        this.api = new StolenAPI(token);
    }

    public void startBecomingRich() {
        boolean cont = true;

        while (cont) {
            try {
                api.getMyProfile();
            } catch (IOException ignored) {

            }
            api.harvestAllPets();

            buyWhatYouCan();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
    }

    public static int userScore(User user) {
        int score = 0;

        if (user.getIdentities() != null && user.getIdentities().length > 0) {
            long followCount = 0;
            for (Identity identity : user.getIdentities()) {
                followCount = Math.max(followCount, identity.getFollowerCount());
            }
            score += (int)followCount;
        }
//        score += (int)Math.max(1000.0 * (1.0 - user.getLastsale().getSecondsSinceSale() / 3600.0), 0.0);
//        score += (int)user.getLastsale().getTotalTimesPurchased();
        return score;
    }

    private void buyWhatYouCan() {
        switch (this.buyerMethod) {
            case FRIENDS:
                this.buyUsers(api.getFriends());
                break;
            case EFFICIENT:
                this.buyUsers(api.getGlobalRecentlyStolen());
                break;
            case INACTIVE:
                break;
        }
    }

    private void buyUsers(User[] users) {
        List<User> buyableUsers = new ArrayList<>();
        for (User user: users) {
            Sale sale = user.getLastsale();
            if (!user.isOwnedBy(api.getMyUser()) && sale != null) {
                int minScore = this.buyerMethod == BuyerMethod.EFFICIENT ? 100000 : 500;

                if (sale.getDisplayPrice() > api.getBalance() || sale.getTotalTimesPurchased() == 0 || userScore(user) < minScore || sale.getDisplayPrice() < 50000) {
                    continue;
                }
                if (!buyableUsers.contains(user)) {
                    buyableUsers.add(user);
                }
            }
        }

        buyableUsers.sort((o1, o2) -> {
            int o1Score = StolenBuyer.userScore(o1);
            int o2Score = StolenBuyer.userScore(o2);
            return o2Score - o1Score;
        });

        api.buyUsers(buyableUsers.toArray(new User[buyableUsers.size()]));
    }

}
