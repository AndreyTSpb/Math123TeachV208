package potaskun.enot.math123teachv20;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StudentBallsActivity extends AppCompatActivity {

    private ArrayList<StudBall> studBalls;
    private StudBallAdapter adapter;
    public static String JsonURL;
    public static String JsonURL1;
    public String error;
    private JSONArray jsonBall;
    private ProgressDialog dialog;
    private String idStud;
    private String idLess;

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
        idStud = extras.getString("idStud");
        idLess = extras.getString("idLess");
        System.out.println("test-jsonBall" + jsonBall);


        /**
         * вЫВОД списка групп где есть текущая дата
         */
        studBalls = new ArrayList<>();
        for(int i=0; i<jsonBall.length(); i++){
            try {
                studBalls.add(new StudBall(i, jsonBall.get(i).toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
                jsonBall = urls.getJSONObject(3).getJSONArray("balls");
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

    /**
     * Ставим отметки
     */
    public void addBall(String ball, String quest){
        new RequestTaskAddBall().execute("http://math123.ru/rest/index.php", idStud, idLess, ball, quest);
    }

    /**
     * Получаем данные об оценках пользователя
     */
    class RequestTaskAddBall extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {

            dialog = new ProgressDialog(StudentBallsActivity.this);
            dialog.setMessage("Загружаюсь...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                //создаем запрос на сервер
                DefaultHttpClient hc = new DefaultHttpClient();
                ResponseHandler<String> res = new BasicResponseHandler();
                //он у нас будет посылать post запрос
                HttpPost postMethod = new HttpPost(strings[0]);

                String idStud  = strings[1];
                String idLess  = strings[2];
                String ball    = strings[3];
                String quest   = strings[4];

                //будем передавать два параметра
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
                //передаем параметры из наших текстбоксов
                //маршрут
                nameValuePairs.add(new BasicNameValuePair("route", "addBall"));
                //айди урока
                nameValuePairs.add(new BasicNameValuePair("id_less", ""+idLess));
                //айди студента
                nameValuePairs.add(new BasicNameValuePair("id_stud", ""+idStud));

                nameValuePairs.add(new BasicNameValuePair("ball", ball));

                nameValuePairs.add(new BasicNameValuePair("quest", quest));
                //КлючПроверки
                nameValuePairs.add(new BasicNameValuePair("hesh_key", Global.HESH_KEY));
                //Логин + Пароль
                nameValuePairs.add(new BasicNameValuePair("loginPass", Global.LOGIN+Global.PASS));
                System.out.println("test7 nameValuePairs"+nameValuePairs);
                //собераем их вместе и посылаем на сервер
                postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                String response = hc.execute(postMethod, res);
                //получаем ответ от сервера
                System.out.println("test7-postMetod"+response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            dialog.dismiss();
            super.onPostExecute(result);
        }
    }

}
