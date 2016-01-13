package co.yolo.model;

/**
 * Created by scorsin on 1/13/16.
 */
public class Responses {

    public static class UserResponse extends Response<User> {}
    public static class HarvestResponse extends Response<Harvest> {}
    public static class UsersResponse extends Response<User[]> {}
    public static class PetsResponse extends Response<Pets> {}
    public static class BuyResponse extends Response<Buy> {}

}
