package potaskun.enot.math123teachv20;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class StudentBallsActivity extends AppCompatActivity {

    private ArrayList<StudBall> studBalls;
    private StudBallAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_balls);
        /*
         * Кнопка возврата
         */
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();

        TextView nameStud = findViewById(R.id.nameStud);
        nameStud.setText(intent.getStringExtra("NameStud"));

        /**
         * вЫВОД списка групп где есть текущая дата
         */
        studBalls = new ArrayList<>();
        for(int i=1; i<9; i++){
            studBalls.add(new StudBall(i, 1));
        }

        ListView listQuest = findViewById(R.id.listQuest);
        adapter = new StudBallAdapter(this, R.layout.items_ball, studBalls);
        listQuest.setAdapter(adapter);
    }

    /**
     * Метод для кнопки возврата
     * возврата к предыдущему экрану, в котором мы просто завершаем работу текущего:
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
