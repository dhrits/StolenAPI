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

    public interface UsersFetcher {

        User[] fetch();

    }


    private BuyerMethod buyerMethod;
    private String token;
    private double balance;
    private User myUser;
    private double serverTime;
    private TaskQueue processTaskQueue;

    public StolenBuyer(BuyerMethod buyerMethod, String token) throws IOException {
        this.buyerMethod = buyerMethod;
        this.token = token;
        this.processTaskQueue = new ThreadedConcurrentTaskQueue(4);
        getMyProfile();
    }

    public void startBecomingRich() {
        boolean cont = true;

        while (cont) {
            try {
                getMyProfile();
            } catch (IOException ignored) {

            }
            harvestAllPets();

            buyWhatYouCan();
            try {
                Thread.sleep(5000);
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
                this.buyUsers(getFriends());
                break;
            case EFFICIENT:
                User[] users = fetchUsersSimultaneously(this::getGlobalRecentlyStolen, this::getMyRecentlyStolen);
                this.buyUsers(users);
                break;
            case INACTIVE:
                break;
        }
    }

    private User[] fetchUsersSimultaneously(UsersFetcher... fetchers) {
        List<User> users = new ArrayList<>();
        for (UsersFetcher fetcher : fetchers) {
            this.processTaskQueue.executeAsync(() -> {
                User[] fetchedUsers = fetcher.fetch();
                synchronized (users) {
                    for (User user : fetchedUsers) {
                        users.add(user);
                    }
                }
            });
        }
        this.processTaskQueue.waitAllTasks();
        return users.toArray(new User[users.size()]);
    }

    private void buyUsers(User[] users) {
        List<User> buyableUsers = new ArrayList<>();
        for (User user: users) {
            Sale sale = user.getLastsale();
            if (!user.isOwnedBy(this.myUser) && sale != null) {
                if (sale.getDisplayPrice() > this.balance || sale.getTotalTimesPurchased() == 0) {
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

        try {
            while (!buyableUsers.isEmpty()) {
                User user = buyableUsers.get(0);
                buyableUsers.remove(0);

                int minScore = this.buyerMethod == BuyerMethod.EFFICIENT ? 100000 : 500;

                if (user.getLastsale().getDisplayPrice() <= this.balance && userScore(user) > minScore) {
                    buyUser(user);
                }
            }
        } catch (RuntimeException ignored) {

        }
    }

    private void buyUser(User user) {
        System.out.println("Buying user " + user.getName() + " (total bought " + user.getLastsale().getTotalTimesPurchased() + ")");

        StolenRequest request = createRequest("people/" + user.getId() + "/buy", HttpMethod.POST);
        request.addParameter("purchase_uuid", user.getLastsale().getPurchaseUuid());
        try {
            Responses.BuyResponse response = request.response(Responses.BuyResponse.class);
            updateMeta(response);
            System.out.println("Bought " + user.getName() + " for " + response.getData().getBought().getLastsale().getLastSalePrice());
            harvest(response.getData().getBought());
        } catch (IOException e) {
            if (e.getMessage().contains("too many pets")) {
                System.out.println("Can't buy anymore, reached max slots");
                throw new RuntimeException(e.getMessage());
            }
            e.printStackTrace();
        }
    }

    private void harvestAllPets() {
        User[] users = getUsers("people/" + myUser.getId() + "/pets");

        for (User user : users) {
            this.processTaskQueue.executeAsync(() -> {
                harvest(user);
            });
        }
        this.processTaskQueue.waitAllTasks();
    }

    private void harvest(User user) {
        if (user.getLastsale() == null || user.getLastsale().getCurrentHarvestAmount() < 10 || user.getOwner() == null || !user.getOwner().equals(myUser)) {
            return;
        }
        StolenRequest request = createRequest("/me/pets/" + user.getId() + "/harvest", HttpMethod.POST);
        try {
            Responses.HarvestResponse response = request.response(Responses.HarvestResponse.class);
            System.out.println("Harvested " + user.getName() + " for " + formatDouble(response.getData().getAmount()) + " coins");
            updateMeta(response);
        } catch (IOException e) {
            System.out.println("Failed: " + e.getMessage());
        }
    }

    private User[] getGlobalRecentlyStolen() {
        return getUsers("lists/recently_stolen");
    }

    private User[] getMyRecentlyStolen() {
        return getPets("me/pets/recently_stolen");
    }

    private User[] getFriends() {
        return getUsers("/me/friends");
    }

    private User[] getPets(String path) {
        StolenRequest request = createRequest("me/pets/recently_stolen", HttpMethod.GET);
        request.addParameter("limit", 100);

        try {
            Responses.PetsResponse response = request.response(Responses.PetsResponse.class);
            updateMeta(response);
            return response.getData().getPets();
        } catch (IOException e) {
            System.out.println("Failed: " + e.getMessage());
            e.printStackTrace();
            return new User[0];
        }
    }

    private User[] getUsers(String path) {
        StolenRequest request = createRequest(path, HttpMethod.GET);
        request.addParameter("limit", 100);

        try {
            Responses.UsersResponse response = request.response(Responses.UsersResponse.class);
            updateMeta(response);
            return response.getData();
        } catch (IOException e) {
            System.out.println("Failed: " + e.getMessage());
            e.printStackTrace();
            return new User[0];
        }
    }

    private static String formatDouble(double d) {
        return DecimalFormat.getInstance(Locale.FRENCH).format(d);
    }

    private void getMyProfile() throws IOException {
        StolenRequest request = createRequest("/me", HttpMethod.GET);
        Responses.UserResponse response = request.response(Responses.UserResponse.class);

        if (!response.isSuccess()) {
            throw new IOException("Unable to login: " + response.getMeta().getErrorMessage());
        }
        if (this.myUser != null) {
            double networkDiff = response.getData().getNetWorth() - this.myUser.getNetWorth();
            if (networkDiff > 0) {
                System.out.println("Net worth has increased by " + formatDouble(networkDiff) + " up to " + formatDouble(response.getData().getNetWorth()));
            } else if (networkDiff < 0) {
                System.out.println("Net worth has decreased by " + formatDouble(networkDiff) + " down to " + formatDouble(response.getData().getNetWorth()));
            }
        }

        this.myUser = response.getData();
        updateMeta(response);
    }

    private synchronized void updateMeta(Response<?> response) {
        if (response.getMeta() != null) {
            if (response.getMeta().getWallet() != null) {
                double balance = response.getMeta().getWallet().getBalance();
                if (balance != this.balance) {
                    this.balance = balance;
                    System.out.println("Balance is now " + formatDouble(balance));
                }
            }
            this.serverTime = response.getMeta().getServerTime();
        }
    }

    private StolenRequest createRequest(String path, HttpMethod method) {
        return new StolenRequest(path, token, method);
    }

}
