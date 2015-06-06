package edu.oregonstate.subertd.finalproject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Donald on 6/4/2015.
 */
public class ShoppingList {

    public static final String NAME = "name";
    public static final String ID = "_id";
    public static final String ITEMS = "items";

    private String name;
    private String id;
    private List<ShoppingListItem> items = new ArrayList<ShoppingListItem>();

    public ShoppingList() {
        super();
    }

    public ShoppingList(JSONObject jsonObject) throws JSONException {
        super();

        this.name = jsonObject.getString(NAME);
        this.id = jsonObject.getString(ID);
        this.items = new ArrayList<ShoppingListItem>();
        final JSONArray items = jsonObject.getJSONArray(ITEMS);
        for (int i = 0; i < items.length(); ++i) {
            final JSONObject shoppingListItem = (JSONObject)items.get(i);
            this.items.add(new ShoppingListItem(shoppingListItem));
        }
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ShoppingListItem> getItems() {
        return items;
    }

    public void setItems(List<ShoppingListItem> items) {
        this.items = items;
    }

    public JSONObject asJsonObject() throws JSONException {
        final JSONObject o = new JSONObject();

        final JSONArray items = new JSONArray();
        for (ShoppingListItem i : this.items) {
            items.put(i.asJsonObject());
        }

        o.put(NAME, this.name);
        o.put(ID, this.id);
        o.put(ITEMS, items);

        return o;
    }
}
