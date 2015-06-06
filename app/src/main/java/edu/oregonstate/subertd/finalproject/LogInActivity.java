package edu.oregonstate.subertd.finalproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class LogInActivity extends AppCompatActivity {

    private final String TAG = LogInActivity.class.getName();

    public static final String EXPIRATION = "expiration";
    public static final String TOKEN = "token";
    public static final String USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_log_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void logIn(final View view) {
        Log.i(TAG, "handling click of logIn button");

        final String userName = ((EditText)findViewById(R.id.user_name_field)).getText().toString();
        final String password = ((EditText)findViewById(R.id.password_field)).getText().toString();

        new LogInHandler().execute(userName, password);
    }

    public void createAccount(final View view) {
        Log.i(TAG, "handling click of createAccount Button");

        final String userName = ((EditText)findViewById(R.id.user_name_field)).getText().toString();
        final String password = ((EditText)findViewById(R.id.password_field)).getText().toString();

        new AccountCreator().execute(userName, password);
    }

    private class AccountCreator extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String...params) {
            try {
                try {

                    final String userName = params[0];
                    final String password = params[1];

                    if (userName != null && userName.length() > 0
                            && password != null && password.length() > 0) {
                        return new ServerProxy().createAccount(userName, password);
                    }
                    else {
                        final String message = "Missing user name or password fields";
                        Log.e(TAG, message);
                        final JSONObject response = new JSONObject();
                        response.put("success", false);
                        response.put("message", message);
                        return response;
                    }
                }
                catch (final ServerException e) {
                    final String message = "Unable to execute the server transaction for create account";
                    Log.e(TAG, message, e);
                    final JSONObject response = new JSONObject();
                    response.put("success", false);
                    response.put("message", message);
                    return response;
                }
            } catch (final JSONException e) {
                final String message =
                        "Unable to assemble JSONObject to express missing parameters from create account";
                Log.e(TAG, message, e);
                return new JSONObject();
            }
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            try {

                // If it was successful
                final boolean success = response.getBoolean("success");
                if (success) {
                    // Get the session info
                    final String userId = response.getString(USER_ID);
                    final String token = response.getString(TOKEN);
                    final long expiration = response.getLong(EXPIRATION);

                    // report success
                    Toast.makeText(LogInActivity.this,
                            "Successfully Created Account", Toast.LENGTH_LONG).show();

                    /*
                    // move to the next activity
                    final Intent transitionToListsActivity =
                            new Intent(LogInActivity.this, ListsActivity.class);
                    transitionToListsActivity.putExtra(USER_ID, userId);
                    transitionToListsActivity.putExtra(TOKEN, token);
                    transitionToListsActivity.putExtra(EXPIRATION, expiration);
                    startActivity(transitionToListsActivity);
                    */
                }
                else {
                    // Report the failure
                    final String message = response.getString("message");
                    Log.e(TAG, message);
                    Toast.makeText(LogInActivity.this, message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                final String message = "Missing expected content in response";
                Log.e(TAG, message, e);
                Toast.makeText(LogInActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    private class LogInHandler extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String...params) {

            try {
                try {
                    final String userName = params[0];
                    final String password = params[1];

                    if (userName != null && userName.length() > 0
                            && password != null && password.length() > 0) {
                        return new ServerProxy().logIn(userName, password);
                    }
                    else {
                        final String message = "Missing user name or password fields";
                        Log.e(TAG, message);
                        final JSONObject response = new JSONObject();
                        response.put("success", false);
                        response.put("message", message);
                        return response;
                    }
                }
                catch (final ServerException e) {
                    final String message = "Unable to execute the server transaction for log in";
                    Log.e(TAG, message, e);
                    final JSONObject response = new JSONObject();
                    response.put("success", false);
                    response.put("message", message);
                    return response;
                }
            } catch (final JSONException e) {
                final String message =
                        "Unable to assemble JSONObject to express missing parameters from log in";
                Log.e(TAG, message, e);
                return new JSONObject();
            }
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            try {

                // If it was successful
                final boolean success = response.getBoolean("success");
                if (success) {
                    // Get the session info
                    final String userId = response.getString(USER_ID);
                    final String token = response.getString(TOKEN);
                    final long expiration = response.getLong(EXPIRATION);

                    // report success
                    Toast.makeText(LogInActivity.this,
                            "Successfully Logged In", Toast.LENGTH_LONG).show();

                    // move to the next activity
                    final Intent transitionToListsActivity =
                            new Intent(LogInActivity.this, ListsActivity.class);
                    transitionToListsActivity.putExtra(USER_ID, userId);
                    transitionToListsActivity.putExtra(TOKEN, token);
                    transitionToListsActivity.putExtra(EXPIRATION, expiration);
                    startActivity(transitionToListsActivity);
                }
                else {
                    // Report the failure
                    final String message = response.getString("message");
                    Log.e(TAG, message);
                    Toast.makeText(LogInActivity.this, message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                final String message = "Missing expected content in response";
                Log.e(TAG, message, e);
                Toast.makeText(LogInActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
