package co.yolo;

import co.yolo.model.Response;
import co.yolo.model.Responses;
import co.yolo.model.Sale;
import co.yolo.model.User;
import me.corsin.javatools.array.ArrayUtils;
import me.corsin.javatools.http.HttpMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Created by scorsin on 1/13/16.
 */
public class StolenBuyer {

    private String token;
    private double balance;
    private User myUser;
    private double serverTime;

    public StolenBuyer(String token) throws IOException {
        this.token = token;
        checkToken();
    }

    public void startBecomingRich() {
        boolean cont = true;

        while (cont) {
            harvestAllPets();

            buyWhatYouCan();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
        }
    }

    public static int userScore(User user) {
        return (int)user.getLastsale().getTotalTimesPurchased();
    }

    private void buyWhatYouCan() {
        User[] users = ArrayUtils.addItems(getFriends(), getRecentlyStolen());

        List<User> buyableUsers = new ArrayList<User>();
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

        buyableUsers.sort(new Comparator<User>() {
            public int compare(User o1, User o2) {
                int o1Score = StolenBuyer.userScore(o1);
                int o2Score = StolenBuyer.userScore(o2);
                return o2Score - o1Score;
            }
        });

        try {
            while (!buyableUsers.isEmpty()) {
                User user = buyableUsers.get(0);
                buyableUsers.remove(0);

                if (user.getLastsale().getDisplayPrice() <= this.balance) {
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
                throw new RuntimeException(e.getMessage());
            }
            e.printStackTrace();
        }
    }

    private void harvestAllPets() {
        User[] users = getUsers("people/" + myUser.getId() + "/pets");
        for (User user : users) {
            harvest(user);
        }
    }

    private void harvest(User user) {
        if (user.getLastsale() == null || user.getLastsale().getCurrentHarvestAmount() < 10 || user.getOwner() == null || !user.getOwner().equals(myUser)) {
            return;
        }
        StolenRequest request = createRequest("/me/pets/" + user.getId() + "/harvest", HttpMethod.POST);
        try {
            Responses.HarvestResponse response = request.response(Responses.HarvestResponse.class);
            System.out.println("Harvested " + user.getName() + " for " + response.getData().getAmount() + " coins");
            updateMeta(response);
        } catch (IOException e) {
            System.out.println("Failed: " + e.getMessage());
        }
    }

    private User[] getRecentlyStolen() {
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

    private void checkToken() throws IOException {
        StolenRequest request = createRequest("/me", HttpMethod.GET);
        Responses.UserResponse response = request.response(Responses.UserResponse.class);

        if (!response.isSuccess()) {
            throw new IOException("Unable to login: " + response.getMeta().getErrorMessage());
        }
        this.myUser = response.getData();
        updateMeta(response);
    }

    private void updateMeta(Response<?> response) {
        if (response.getMeta() != null) {
            if (response.getMeta().getWallet() != null) {
                double balance = response.getMeta().getWallet().getBalance();
                if (balance != this.balance) {
                    this.balance = balance;
                    System.out.println("Balance is now " + balance);
                }
            }
            this.serverTime = response.getMeta().getServerTime();
        }
    }

    private StolenRequest createRequest(String path, HttpMethod method) {
        return new StolenRequest(path, token, method);
    }

}
