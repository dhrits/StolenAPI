package co.yolo;

import co.yolo.model.*;
import me.corsin.javatools.http.HttpMethod;
import me.corsin.javatools.task.TaskQueue;
import me.corsin.javatools.task.ThreadedConcurrentTaskQueue;
import twitter4j.Twitter;
import twitter4j.TwitterAPIConfiguration;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by scorsin on 1/14/16.
 */
public class StolenAPI {

    public interface UsersFetcher {

        User[] fetch();

    }

    private String token;
    private double balance;
    private User myUser;
    private double serverTime;
    private TaskQueue processTaskQueue;

    public StolenAPI(String token) throws IOException {
        this.token = token;
        this.processTaskQueue = new ThreadedConcurrentTaskQueue(4);
        getMyProfile();
    }

    public User[] fetchUsersSimultaneously(UsersFetcher... fetchers) {
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

    public void buyUsers(User[] users) {
        List<User> buyableUsers = new ArrayList<>(Arrays.asList(users));

        try {
            while (!buyableUsers.isEmpty()) {
                User user = buyableUsers.get(0);
                buyableUsers.remove(0);

                if (user.getLastsale().getDisplayPrice() <= this.balance) {
                    buyUser(user);
                }
            }
        } catch (RuntimeException ignored) {
            ignored.printStackTrace();
        }
    }

    public void buyUser(User user) {
        System.out.println("Buying user " + user.getName() + " (total bought " + user.getLastsale().getTotalTimesPurchased() + ")");

        StolenRequest request = createRequest("people/" + user.getId() + "/buy", HttpMethod.POST);
        request.addParameter("purchase_uuid", user.getLastsale().getPurchaseUuid());
        try {
            Responses.BuyResponse response = request.response(Responses.BuyResponse.class);
            updateMeta(response);
            System.out.println("Bought " + user.getName() + " for " + response.getData().getBought().getLastsale().getLastSalePrice());
            this.processTaskQueue.executeAsync(() -> harvest(response.getData().getBought()));
        } catch (IOException e) {
            if (e.getMessage().contains("too many pets")) {
                System.out.println("Can't buy anymore, reached max slots");
                throw new RuntimeException(e.getMessage());
            }
            e.printStackTrace();
        }
    }

    public void harvestAllPets() {
        User[] users = getUsers("people/" + myUser.getId() + "/pets");

        for (User user : users) {
            this.processTaskQueue.executeAsync(() -> harvest(user));
        }
        this.processTaskQueue.waitAllTasks();
    }

    public void harvest(User user) {
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

    public User[] getGlobalRecentlyStolen() {
        return getUsers("lists/recently_stolen");
    }

    public User[] getMyRecentlyStolen() {
        return getPets("me/pets/recently_stolen");
    }

    public User[] getFriends() {
        return getUsers("/me/friends");
    }

    public User[] getPets(String path) {
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

    public User[] getUsers(String path) {
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

    public void getMyProfile() throws IOException {
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

    public User getMyUser() {
        return myUser;
    }

    public double getBalance() {
        return balance;
    }
}
