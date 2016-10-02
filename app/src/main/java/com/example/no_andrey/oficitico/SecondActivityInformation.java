package com.example.no_andrey.oficitico;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class SecondActivityInformation extends AppCompatActivity {

    private long row; // the last row added
    private DBHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_activity_information);

        Bundle extras = getIntent().getExtras();
        row = extras.getLong("newPersonRow"); //newPersonRow is the key for the last row added
    }

    public void thirdActivityInformation(View view)
    {
        EditText services = (EditText) findViewById(R.id.editTextServices);
        Map<String, String> map = new HashMap<String, String>();
        map.put("services", services.getText().toString());

        database = new DBHelper(this);

        int rows = database.updatePersonData(this.row, map);

        Toast.makeText(this, "Número de filas afectadas "+rows, Toast.LENGTH_SHORT).show();
    }
}
