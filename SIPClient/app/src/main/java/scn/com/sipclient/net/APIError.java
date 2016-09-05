package scn.com.sipclient.net;

/**
 * Created by pakjs on 9/3/2016.
 */
public class APIError {
    private int statusCode;
    private String message;

    public APIError() {
    }

    public int status() {
        return statusCode;
    }

    public String message() {
        return message;
    }
}
