package me.maddie_wang.todoapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class EditItemActivity extends AppCompatActivity {

    EditText etItemText; // text field that contains updated item description
    int position; // to know which item we are editing


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        etItemText = (EditText) findViewById(R.id.etItemText); // text field from our edit page
        etItemText.setText(getIntent().getStringExtra(MainActivity.ITEM_TEXT)); // set txtfield w/ content from intent
        position = getIntent().getIntExtra(MainActivity.ITEM_POSITION, 0); // track position of item
        getSupportActionBar().setTitle("Edit Item");

    }

    // Handler - updates the item with the new text
    public void onSaveItem(View v) {
        Intent data = new Intent(); // prepare intent to pass back to MainActivity
        data.putExtra(MainActivity.ITEM_TEXT, etItemText.getText().toString()); // sends back txtbox's content
        data.putExtra(MainActivity.ITEM_POSITION, position); // and position
        setResult(RESULT_OK, data); // set result code + bundle response
        finish(); // closes edit activity + passes intent data back to main

    }
}
