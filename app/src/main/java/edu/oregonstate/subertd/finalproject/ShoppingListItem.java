package edu.oregonstate.subertd.finalproject;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Donald on 6/4/2015.
 */
public class ShoppingListItem {

    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String QUANTITY = "quantity";
    public static final String CHECK = "check";

    private String id;
    private String name;
    private int quantity;
    private boolean checked;

    public ShoppingListItem() {
        super();
    }

    public ShoppingListItem(final JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getString(ID);
        this.name = jsonObject.getString(NAME);
        this.quantity = jsonObject.getInt(QUANTITY);
        this.checked = jsonObject.getBoolean(CHECK);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public JSONObject asJsonObject() throws JSONException {

        final JSONObject o = new JSONObject();

        o.put(NAME, this.name);
        o.put(QUANTITY, this.quantity);
        o.put(CHECK, this.checked);

        return o;
    }
}
