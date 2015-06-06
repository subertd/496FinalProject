package edu.oregonstate.subertd.finalproject;

import android.util.Log;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Donald on 6/4/2015.
 */
public class ServerProxy {

    private static final String TAG = ServerProxy.class.getName();

    private static final String PROTOCOL_HTTP = "http://";

    private static final String API_ROOT = "nodejs-subertd.rhcloud.com";

    private static final String USER_PATH = "/users";

    private static final String LISTS_PATH = "/lists";

    public JSONObject createAccount(final String userName, final String password) throws ServerException {

        final String path = PROTOCOL_HTTP + API_ROOT + USER_PATH;
        Log.i(TAG, "Create Account Path: " + path);
        Log.i(TAG, "username: " + userName);
        Log.i(TAG, "password: " + password);

        try {
            HttpPost request = new HttpPost(path);
            request.setHeader("accept", "application/json");
            request.setHeader("username", userName);
            request.setHeader("password", password);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            final ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpClient.execute(request, responseHandler);

            Log.i(TAG, "Create Account Response: " + response);

            return new JSONObject(response);
        }
        catch (final IOException e) {
            final String message = "Unable to execute http request to create account";
            Log.e(TAG, message, e);
            throw new ServerException(message, e);
        }
        catch (final JSONException e) {
            final String message = "Unable to parse the create account response as JSON";
            Log.e(TAG, message, e);
            throw new ServerException(message, e);
        }
    }

    public JSONObject logIn(final String userName, final String password) throws ServerException {

        try {
            HttpPost request = new HttpPost(PROTOCOL_HTTP + API_ROOT + USER_PATH + "/logIn");
            request.setHeader("accept", "application/json");
            request.setHeader("username", userName);
            request.setHeader("password", password);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            final ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpClient.execute(request, responseHandler);

            Log.i(TAG, "Log In Response: " + response);

            return new JSONObject(response);
        }
        catch (final IOException e) {
            final String message = "Unable to execute http request to log in";
            Log.e(TAG, message, e);
            throw new ServerException(message, e);
        }
        catch (final JSONException e) {
            final String message = "Unable to parse the log response as JSON";
            Log.e(TAG, message, e);
            throw new ServerException(message, e);
        }
    }

    public JSONArray getLists(final String userId, final String token) throws ServerException {
        try {
            HttpGet request = new HttpGet(PROTOCOL_HTTP + API_ROOT + LISTS_PATH);
            request.setHeader("accept", "application/json");
            request.setHeader("userid", userId);
            request.setHeader("token", token);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            final ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpClient.execute(request, responseHandler);

            Log.i(TAG, "Get Lists Response: " + response);

            return new JSONArray(response);
        } catch (final IOException e) {
            final String message = "Unable to execute http request to get lists";
            Log.e(TAG, message, e);
            throw new ServerException(message, e);
        } catch (final JSONException e) {
            final String message = "Unable to parse the get lists response as JSON";
            Log.e(TAG, message, e);
            throw new ServerException(message, e);
        }
    }

    public JSONArray getListItems(final String userId, final String token, final String listId) throws ServerException {

        final String path = PROTOCOL_HTTP + API_ROOT + LISTS_PATH + "/" + listId + "/items";
        Log.e(TAG, "Get List Items Path: " + path);

        try {
            HttpGet request = new HttpGet(path);
            request.setHeader("accept", "application/json");
            request.setHeader("userid", userId);
            request.setHeader("token", token);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            final ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpClient.execute(request, responseHandler);

            Log.i(TAG, "Get Lists Response: " + response);

            return new JSONArray(response);
        }
        catch (final IOException e) {
            final String message = "Unable to execute http request to get list items";
            Log.e(TAG, message, e);
            throw new ServerException(message, e);
        }
        catch (final JSONException e) {
            final String message = "Unable to parse the get list items response as JSON";
            Log.e(TAG, message, e);
            throw new ServerException(message, e);
        }
    }

    public JSONObject addList(final String userId, final String token, final ShoppingList list) throws ServerException {

        final String path = PROTOCOL_HTTP + API_ROOT + LISTS_PATH;
        Log.i(TAG, "Add List Path: " + path);

        try {
            final JSONObject params = list.asJsonObject();
            final String paramsString = params.toString();
            final StringEntity body = new StringEntity(paramsString);
            Log.i(TAG, "Add List Parameters: " + paramsString);

            HttpPost httpPost = new HttpPost(path);
            httpPost.setHeader("accept", "application/json");
            httpPost.setHeader("content-type", "application/json");
            httpPost.setHeader("userid", userId);
            httpPost.setHeader("token", token);
            httpPost.setEntity(body);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            final ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpClient.execute(httpPost, responseHandler);

            Log.i(TAG, "Add List Response: " + response);

            return  new JSONObject(response);
        }
        catch (final IOException | JSONException e) {
            final String message = "Unable to add the list";
            Log.e(TAG, message, e);
            throw new ServerException(message, e);
        }
    }

    public JSONObject addListItem(final String userId, final String token,
                        final String listId, final ShoppingListItem item) throws ServerException {

        final String path = PROTOCOL_HTTP + API_ROOT + LISTS_PATH + "/" + listId + "/items";
        Log.i(TAG, "Add List Item Path: " + path);

        try {
            final JSONObject params = item.asJsonObject();
            final String paramsString = params.toString();
            final StringEntity body = new StringEntity(paramsString);
            Log.i(TAG, "Add List Item Parameters: " + paramsString);

            HttpPost httpPost = new HttpPost(path);
            httpPost.setHeader("accept", "application/json");
            httpPost.setHeader("content-type", "application/json");
            httpPost.setHeader("userid", userId);
            httpPost.setHeader("token", token);
            httpPost.setEntity(body);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            final ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpClient.execute(httpPost, responseHandler);

            Log.i(TAG, "Add List Item Response: " + response);

            return new JSONObject(response);
        }
        catch (final IOException | JSONException e) {
            final String message = "Unable to add the item to the list";
            Log.e(TAG, message, e);
            throw new ServerException(message, e);
        }
    }
}
