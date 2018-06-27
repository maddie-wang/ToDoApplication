package me.maddie_wang.todoapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;
    private static final String FILE_NAME = "todo.txt"; // where our items will be stored in
    public static final int EDIT_REQUEST_CODE = 20; // identity edit activity code
    public static final String ITEM_TEXT = "itemText"; // to pass data between activities
    public static final String ITEM_POSITION = "itemPosition";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // layout template

        // items --[itemsAdapter]--> lvItems object on Phone
        lvItems = (ListView) findViewById(R.id.lvItems); // refers to layout's "listview" object
        readItems(); // initializes arraylist that loads array w/ data in todo.txt
        // initializes adapter with items list --- "items" will be linked to "itemsAdapter"
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdapter); // puts adapter into view
        // MOCK DATA: data items.add("First todo item");
        setupListViewListener(); // setup listener on creation for removing items
    }

    // functionailty for adding an item
    public void onAddItem(View v) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem); // reference to layout's txtbox
        String itemText = etNewItem.getText().toString(); // grabs the user's text from the txtbox
        itemsAdapter.add(itemText); // adds users text to itemsAdapter arraylist that we made before
        writeItems(); // store updated list in todo.txt
        etNewItem.setText(""); // clear txtbox
        Toast.makeText(getApplicationContext(), "Item added to the list", Toast.LENGTH_SHORT).show();
    }
    // functionality for removing an item (long click) + editing an item (single click)
    private void setupListViewListener() {
        // set ListView's itemLongClickListener (removing item)
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                items.remove(position); // remove the item in the list given by position
                itemsAdapter.notifyDataSetChanged(); // notify adapter that data changed
                writeItems(); // store updated list in todo.txt
                Log.i("MainActivity", "Removed Item " + position);
                return true; // to tell framework long click was consumed
            }
        });

        // set ListView's itemClickListener (edit item)
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // first parameter = context, second is class of activity to launch
                Intent i = new Intent(MainActivity.this, EditItemActivity.class);
                i.putExtra(ITEM_TEXT, items.get(position)); // communicates with other class w/
                i.putExtra(ITEM_POSITION, position); // position and text
                startActivityForResult(i, EDIT_REQUEST_CODE); // brings up edit activity

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == EDIT_REQUEST_CODE) { // got request+response
            String updatedItem = data.getExtras().getString(ITEM_TEXT); // grab updated text
            int position = data.getExtras().getInt(ITEM_POSITION, 0); // get pos of item
            items.set(position, updatedItem); // update item w/ updated text
            itemsAdapter.notifyDataSetChanged(); // notify data was changed
            writeItems(); // write changes to file
            Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show(); // notifies user
        }
    }

    // return file which the data is stored in (implements persistence)
    private File getDataFile() {
        return new File(getFilesDir(), FILE_NAME);
    }

    // read items from the file system (implements persistence)
    private void readItems() {
        try {
            // make array that stores the content in the file "todo.txt"
            items = new ArrayList<String>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        }  catch (IOException e) {
            e.printStackTrace(); // print error to console
            items = new ArrayList<>(); // just load an empty list then
        }
    }

    // write items to the file system (implements persistence)
    private void writeItems() {
        try { // save the item list as a comma-deliminated text file!
            FileUtils.writeLines(getDataFile(), items); // puts "items" arraylist into todo.txt
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
