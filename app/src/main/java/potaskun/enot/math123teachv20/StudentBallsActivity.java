package potaskun.enot.math123teachv20;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StudentBallsActivity extends AppCompatActivity {

    private ArrayList<StudBall> studBalls;
    private StudBallAdapter adapter;
    public static String JsonURL;
    public String error;

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
        /**
         * Обработка ответа от сервера
         */
        //принимаем параметр который мы послылали в manActivity
        Bundle extras = getIntent().getExtras();
        //превращаем в тип стринг для парсинга
        assert extras != null;
        String json = extras.getString(JsonURL);
        //передаем в метод парсинга
        if (!JSONURL(json)) {
            if(error.isEmpty()) {
                error = "Произошла ошибка";
            }
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("error", error);
            System.out.println("test-error" + error);
            startActivity(intent);
        }

        TextView nameStud = findViewById(R.id.nameStud);
        nameStud.setText(extras.getString("NameStud"));

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

    /**
     * Обработка ответа сервера c оценками
     *
     * @param result
     */
    public boolean JSONURL(String result) {
        try {
            System.out.println("json_test6" + result);
            //создали читателя json объектов и отдали ему строку - result
            JSONObject json = new JSONObject(result);
            //дальше находим вход в наш json им является ключевое слово data
            JSONArray urls = json.getJSONArray("data");
            System.out.println("test6-g" + urls);
            /*Проверяем есть ли данные*/
            System.out.println("test6-mass" + urls.getJSONObject(0).getString("error"));
            if (urls.getJSONObject(0).getString("error").equals("FALSE")) {
                //{"error":"FALSE"},{"kol_ball":"8"},{"subject":"1"},{"balls":["1","2","2","2","2","2","0","0"]},{"errorText":"Урок и Ученик не переданы"}
                return true;
            } else {
                System.out.print("test6-err" + urls.getJSONObject(1).getString("errorText"));
                error = urls.getJSONObject(1).getString("errorText");
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        return false;
    }
}
