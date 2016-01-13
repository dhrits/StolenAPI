package co.yolo.model;

/**
 * Created by scorsin on 1/13/16.
 */
public class Identity extends Entity {

    private long followerCount;
    private long friendCount;
    private int type;

    public long getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(long followerCount) {
        this.followerCount = followerCount;
    }

    public long getFriendCount() {
        return friendCount;
    }

    public void setFriendCount(long friendCount) {
        this.friendCount = friendCount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
