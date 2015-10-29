package edu.buffalo.cse.cse486586.simpledht;

import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SimpleDhtActivity extends Activity {

    Button ldump;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_dht_main);
        
        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());
        findViewById(R.id.button3).setOnClickListener(
                new OnTestClickListener(tv, getContentResolver()));

        ldump = (Button)findViewById(R.id.button1);
        ldump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDhtProvider simpleDhtProvider = new SimpleDhtProvider();
                Cursor cursor = simpleDhtProvider.Local_Dump("\"*\"");
                String text = "";
                if(cursor.moveToFirst()){
                    do{
                       text = text + cursor.getString(0) + "     " + cursor.getString(1) + "\n";
                    }while(cursor.moveToNext());
                }
                Toast.makeText(getBaseContext(),text,Toast.LENGTH_LONG);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_simple_dht_main, menu);
        return true;
    }

}
