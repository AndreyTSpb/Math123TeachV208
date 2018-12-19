package potaskun.enot.math123teachv20;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TestActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        tv = findViewById(R.id.textView8);

        Button but = findViewById(R.id.button);
        but.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        tv.setText("test test");
    }
}
