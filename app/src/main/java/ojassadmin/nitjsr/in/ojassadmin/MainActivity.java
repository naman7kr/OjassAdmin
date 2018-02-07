package ojassadmin.nitjsr.in.ojassadmin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnSearch,btnEvents;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSearch=(Button)findViewById(R.id.search);
        btnEvents=(Button)findViewById(R.id.events);

        toolbar=(Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ojass 18");

        btnEvents.setOnClickListener(this);
        btnSearch.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.search)
        {
            Intent intent=new Intent(MainActivity.this,SearchActivity.class);
            startActivity(intent);
        }
        if (view.getId()==R.id.events)
        {
            Intent intent=new Intent(MainActivity.this,EventsActivity.class);
            startActivity(intent);
        }
    }
}
