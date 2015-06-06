package edu.oregonstate.subertd.finalproject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListsActivity extends AppCompatActivity {

    private static final String TAG = ListsActivity.class.getName();

    private String userId;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        final Bundle extras = getIntent().getExtras();
        this.userId = extras.getString("userId");
        this.token = extras.getString("token");

        populateListOfLists();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lists, menu);
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
        if (id == R.id.action_add_list) {
            showAddListDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private void populateListOfLists() {

        new ListOfListsPopulator().execute(userId, token);
    }

    private class ListOfListsPopulator extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(final String... params) {
            try {
                final String userId = params[0];
                final String token = params[1];

                if (userId != null && userId.length() > 0
                        && token != null && token.length() > 0) {
                    return new ServerProxy().getLists(userId, token);
                }
                else {
                    final String message = "Missing user id or token data";
                    Log.e(TAG, message);
                    return new JSONArray();
                }
            } catch (final ServerException e) {
                final String message = "Unable to execute the server transaction for get lists";
                Log.e(TAG, message, e);
                return new JSONArray();
            }
        }

        @Override
        protected void onPostExecute(final JSONArray lists) {
            try {
                // Convert the JSON array to Java array
                final ShoppingList[] listOfLists = new ShoppingList[lists.length()];
                for (int i = 0; i < lists.length(); ++i) {
                    final JSONObject list = (JSONObject) lists.get(i);
                    Log.i(TAG, "LIST: " + list.toString());
                    listOfLists[i] = new ShoppingList(list);
                    Log.i(TAG, "LIST NAME: " + listOfLists[i].getName());
                }

                ListView listView = (ListView)findViewById(R.id.list_of_lists);
                listView.setAdapter(new ArrayAdapter<ShoppingList>(ListsActivity.this,
                        android.R.layout.simple_list_item_1, listOfLists));

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(final AdapterView<?> listView, final View view,
                                            final int position, final long id) {
                    final ShoppingList list = listOfLists[position];

                    // Start the Items Activity for the selected list
                    final Intent itemsActivity = new Intent(ListsActivity.this, ItemsActivity.class);
                    itemsActivity.putExtra("listName", list.getName());
                    itemsActivity.putExtra("listId", list.getId());
                    itemsActivity.putExtra("userId", userId);
                    itemsActivity.putExtra("token", token);
                    startActivity(itemsActivity);
                    }
                });
            }
            catch (final JSONException e) {
                final String message = "Unable to Display Lists";
                Log.e(TAG, message, e);
                Toast.makeText(ListsActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * @citation http://developer.android.com/guide/topics/ui/dialogs.html
     */
    private void showAddListDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_list, null);
        dialogBuilder.setView(dialogView);

        // @citation http://stackoverflow.com/questions/5525500/findviewbyid-returns-null-in-a-dialog
        final EditText listNameField = (EditText)dialogView.findViewById(R.id.list_name_field);

        dialogBuilder.setTitle(R.string.add_list_dialog_title);
        dialogBuilder.setPositiveButton(R.string.add_list_dialog_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int d) {
                new NewListAdder().execute(listNameField.getText().toString());
            }
        });
        dialogBuilder.setCancelable(true);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private class NewListAdder extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String...params) {

            final ShoppingList shoppingList = new ShoppingList();
            shoppingList.setName(params[0]);

            try {
                final JSONObject response = new ServerProxy().addList(userId, token, shoppingList);

                final boolean success = response.getBoolean("success");
                final JSONObject list = response.getJSONObject("list");
                final String listName = list.getString("name");

                if (success) {
                    return "Successfully Added new Shopping List: " + listName;
                }
                else {
                    return "Unable to Add List (1)";
                }
            }
            catch (final ServerException e) {
                return "Unable to Add List (2)";
            }
            catch (final JSONException e) {
                return "Unexpected Response from Server";
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            Toast.makeText(ListsActivity.this, result, Toast.LENGTH_LONG).show();
            populateListOfLists();
        }
    }
}
