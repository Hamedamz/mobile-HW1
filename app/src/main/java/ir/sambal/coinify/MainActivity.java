package ir.sambal.coinify;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private String[] listItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread thread = new Thread(() -> {
            Log.i("ME", "Shoro shod!");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                Log.i("ME", "Update kon");
                listItem = new String[]{"Arshia"}; // getResources().getStringArray(R.array.array_technology);
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                        R.layout.mylist, R.id.textView, listItem);
                listView.setAdapter(adapter);
                Log.i("ME", "Update shod!");
            });
        });

        thread.start();

        listView = (ListView) findViewById(R.id.listView);
        listItem = new String[]{"Hamed"};
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.mylist, R.id.textView, listItem);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // TODO Auto-generated method stub
                String value = adapter.getItem(position);
                Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();

            }
        });

//        try {
//            thread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }
}