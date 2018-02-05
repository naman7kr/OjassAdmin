package ojassadmin.nitjsr.in.ojassadmin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSearch=(Button)findViewById(R.id.search);

    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.search)
        {
            Intent intent=new Intent(MainActivity.this,SearchActivity.class);
            startActivity(intent);
        }
    }
}
