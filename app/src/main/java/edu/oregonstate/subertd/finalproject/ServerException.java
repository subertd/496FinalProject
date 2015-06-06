package edu.oregonstate.subertd.finalproject;

/**
 * Created by Donald on 6/4/2015.
 */
public class ServerException extends Exception {

    public ServerException() {
        super();
    }

    public ServerException(String detailMessage) {
        super(detailMessage);
    }

    public ServerException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ServerException(Throwable throwable) {
        super(throwable);
    }
}
