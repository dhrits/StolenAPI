package co.yolo;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import me.corsin.javatools.http.APICommunicator;
import me.corsin.javatools.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by scorsin on 1/13/16.
 */
public class JacksonResponseTransformer implements APICommunicator.IResponseTransformer {

    ////////////////////////
    // VARIABLES
    ////////////////

    public static final JacksonResponseTransformer INSTANCE = new JacksonResponseTransformer();
    private ObjectMapper objectMapper;

    ////////////////////////
    // CONSTRUCTORS
    ////////////////

    public JacksonResponseTransformer() {
        this.objectMapper = new ObjectMapper();

        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
    }

    ////////////////////////
    // METHODS
    ////////////////

    public Object transformResponse(InputStream inputStream, Class<?> expectedOutputResponse) throws IOException {
        return this.objectMapper.readValue(inputStream, expectedOutputResponse);
    }

    ////////////////////////
    // GETTERS/SETTERS
    ////////////////
}
