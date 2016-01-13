package co.yolo.model;

/**
 * Created by scorsin on 1/13/16.
 */
public class Response<T> {

    private Meta meta;
    private T data;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return meta != null ? meta.isSuccess() : data != null;
    }
}
