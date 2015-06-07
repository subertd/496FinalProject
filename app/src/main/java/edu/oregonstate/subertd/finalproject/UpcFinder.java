package edu.oregonstate.subertd.finalproject;

import android.util.Log;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Donald on 6/7/2015.
 */
public class UpcFinder {

    private static final String TAG = UpcFinder.class.getName();

    private static final String HTTP_PROTOCOL = "http://";
    private static final String API_ROOT = "api.upcdatabase.org";
    private static final String JSON_REPRESENTATION = "/json";
    private static final String API_KEY = "/e389ec5b0adc511ac1d48899d4079d93";

    private static final String PRODUCT_DESCRIPTION = "description";

    public String getProductDescription(final String upc) {

        final String path = HTTP_PROTOCOL + API_ROOT + JSON_REPRESENTATION + API_KEY + "/" + upc;
        Log.i(TAG, "Get Product Description Path: " + path);

        try {
            final HttpGet httpGet = new HttpGet(path);
            final DefaultHttpClient httpClient = new DefaultHttpClient();
            final ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpClient.execute(httpGet, responseHandler);

            final JSONObject jsonResponse = new JSONObject(response);

            final String productDescription = jsonResponse.getString(PRODUCT_DESCRIPTION);

            return productDescription;
        } catch (JSONException | IOException e) {
            final String message = "Unable to get product from UPC";
            Log.e(TAG, message, e);
            return null;
        }
    }
}
