package co.yolo;

import co.yolo.model.Response;
import me.corsin.javatools.http.HttpMethod;
import me.corsin.javatools.http.ServerRequest;

import java.io.IOException;

/**
 * Created by scorsin on 1/13/16.
 */
public class StolenRequest extends ServerRequest {

    public StolenRequest(String path, String token, HttpMethod method) {
        super("https://api.getstolen.com/api" + (path.startsWith("/") ? path : "/" + path), method);

        getHeaders().put("Authorization", "Bearer " + token);
        getHeaders().put("User-Agent", "Stolen/293 CFNetwork/758.2.8 Darwin/15.0.0");
        setResponseTransformer(JacksonResponseTransformer.INSTANCE);
    }

    public <T extends Response<?>> T response(Class<T> responseType) throws IOException {
        T response = this.getResponse(responseType);

        if (!response.isSuccess()) {
            throw new IOException("API Error: " + response.getMeta().getErrorMessage());
        }
        if (response.getData() == null) {
            throw new IOException("Unable to deserialize data");
        }

        return response;
    }

}
