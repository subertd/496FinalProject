package edu.oregonstate.subertd.finalproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ItemsActivity extends AppCompatActivity {

    private static final String TAG = ItemsActivity.class.getName();

    private String userId;
    private String token;
    private String listName;
    private String listId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        final Bundle extras = getIntent().getExtras();
        this.userId = extras.getString("userId");
        this.token = extras.getString("token");
        this.listName = extras.getString("listName");
        this.listId = extras.getString("listId");

        populateListNameLabel();

        populateListOfItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_items, menu);
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
        if (id == R.id.action_add_item) {
            showAddItemDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void populateListNameLabel() {

        final TextView listNameLabel = (TextView)findViewById(R.id.list_name_label);
        listNameLabel.setText(listName);
    }

    private void populateListOfItems() {
        new ListOfItemsPopulator().execute(userId, token, listId);
    }

    private class ListOfItemsPopulator extends AsyncTask<String, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(String...params) {
            try {
                final String userId = params[0];
                final String token = params[1];
                final String listId = params[2];

                if(userId != null && userId.length() > 0
                        && token != null && token.length() > 0
                        && listId != null && listId.length() > 0)
                {
                    return new ServerProxy().getListItems(userId, token, listId);
                }
                else {
                    final String message = "Missing the required data to populate the list";
                    Log.e(TAG, message);
                    return new JSONArray();
                }
            }
            catch(final ServerException e) {
                final String message = "Unable to execute the server transaction for get list items";
                Log.e(TAG, message, e);
                return new JSONArray();
            }
        }

        @Override
        protected void onPostExecute(final JSONArray items) {
            try {
                // Convert the JSON Array to Java array
                final ShoppingListItem[] listOfItems = new ShoppingListItem[items.length()];
                for (int i = 0; i < items.length(); ++i) {
                    final JSONObject item = (JSONObject)items.get(i);
                    Log.i(TAG, "ITEM: " + item.toString());
                    listOfItems[i] = new ShoppingListItem(item);
                    Log.i(TAG, "ITEM NAME: " + listOfItems[i].getName());
                }

                ListView listView = (ListView)findViewById(R.id.list_of_items);
                listView.setAdapter(new ItemsArrayAdapter(ItemsActivity.this, listOfItems));

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(final AdapterView<?> parent, final View view,
                                            final int position, final long id)
                    {
                        final ShoppingListItem item = listOfItems[position];

                        // TODO mark the item as checked
                    }
                });
            }
            catch (final JSONException e) {
                final String message = "Unable to Display List Items";
                Log.e(TAG, message, e);
                Toast.makeText(ItemsActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * @citation http://developer.android.com/guide/topics/ui/dialogs.html
     */
    private void showAddItemDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_item, null);
        dialogBuilder.setView(dialogView);

        // @citation http://stackoverflow.com/questions/5525500/findviewbyid-returns-null-in-a-dialog
        final EditText itemNameField = (EditText)dialogView.findViewById(R.id.item_name_field);
        final NumberPicker quantityField = (NumberPicker)dialogView.findViewById(R.id.quantity_field);
        quantityField.setMaxValue(Integer.MAX_VALUE);
        quantityField.setMinValue(0);

        dialogBuilder.setTitle(R.string.add_item_dialog_title);
        dialogBuilder.setPositiveButton(R.string.add_item_dialog_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int d) {
                final String itemName = itemNameField.getText().toString();
                final int quantity = quantityField.getValue();

                final ShoppingListItem shoppingListItem = new ShoppingListItem();
                shoppingListItem.setName(itemName);
                shoppingListItem.setQuantity(quantity);

                new NewListItemAdder().execute(shoppingListItem);
            }
        });
        dialogBuilder.setCancelable(true);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private class NewListItemAdder extends AsyncTask<ShoppingListItem, Void, String> {

        @Override
        protected String doInBackground(ShoppingListItem...params) {

            final ShoppingListItem shoppingListItem = params[0];

            try {
                final JSONObject response = new ServerProxy()
                        .addListItem(userId, token, listId, shoppingListItem);

                final boolean success = response.getBoolean("success");
                final JSONObject item = response.getJSONObject("item");
                final String itemName = item.getString("name");

                if (success) {
                    return "Successfully Added new Shopping List Item: " + itemName;
                }
                else {
                    return "Unable to Add List Item (1)";
                }
            }
            catch (final ServerException e) {
                return "Unable to Add List Item (2)";
            }
            catch (final JSONException e) {
                return "Unexpected Response from Server";
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            Toast.makeText(ItemsActivity.this, result, Toast.LENGTH_LONG).show();
            populateListOfItems();
        }
    }

    /**
     * @citation https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView
     */
    private class ItemsArrayAdapter extends ArrayAdapter<ShoppingListItem> {

        public ItemsArrayAdapter(Context context, ShoppingListItem[] shoppingListItems) {
            super(context, 0, shoppingListItems);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // Get the data item for this position
            ShoppingListItem shoppingListItem = getItem(position);

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            }

            final TextView listItemQuantity = (TextView)convertView.findViewById(R.id.list_item_quantity);
            final TextView listItemName = (TextView)convertView.findViewById(R.id.list_item_name);
            final CheckBox listItemCheck = (CheckBox)convertView.findViewById(R.id.list_item_check);

            listItemQuantity.setText(String.valueOf(shoppingListItem.getQuantity()));
            listItemName.setText(shoppingListItem.getName());
            listItemCheck.setChecked(shoppingListItem.isChecked());

            return convertView;
        }
    }
}