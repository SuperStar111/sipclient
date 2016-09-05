package scn.com.sipclient.net;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Created by pakjs on 9/3/2016.
 */
public class ErrorUtils {
    public static APIError parseError(Response<?> response) {
//        Converter<ResponseBody, APIError> converter =
//                APIManager.getServiceAPI()
//                        .responseBodyConverter(APIError.class, new Annotation[0]);

        APIError error = null;

//        try {
//            error = converter.convert(response.errorBody());
//        } catch (IOException e) {
//            return new APIError();
//        }

        return error;
    }
}
