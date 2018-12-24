package potaskun.enot.math123teachv20;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StudentsInGroupActivity extends AppCompatActivity {

    public static String nameGroup;
    public static int idGroup;
    public static int idLess;
    private ArrayList<SelectStudents> selectStudents;
    private SelectStudentsAdapter adapter;
    public HashMap<String, Object> hm;
    public static String JsonURL;
    private String error;
    private JSONArray arrStudents;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_in_group);
        /*
         * Кнопка возврата
         */
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        /*Получаем переданные данные*/
        /**
         * Обработка ответа от сервера
         */
        //принимаем параметр который мы послылали в manActivity
        Bundle extras = getIntent().getExtras();
        //превращаем в тип стринг для парсинга
        assert extras != null;
        String json = extras.getString(JsonURL);
        System.out.println("test1-jsonurl" + JsonURL);
        //передаем в метод парсинга
        if (!JSONURL1(json)) {
            if(error.isEmpty()){error = "Произошла ошибка  json-null";}
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("error", error);
            System.out.println("test-error" + error);
            startActivity(intent);
        }else {

            Intent intent = getIntent();
            nameGroup = intent.getStringExtra("nameGroup");
            idGroup = Integer.parseInt(intent.getStringExtra("idGroup"));
            idLess = Integer.parseInt(intent.getStringExtra("idLess"));
            System.out.println("testtest" + nameGroup);
            TextView ng = findViewById(R.id.nameGroup);
            ng.setText(nameGroup);

            /**
             * Вывод списка детей
             */
            selectStudents = new ArrayList<>();

            for (int i = 0; i < arrStudents.length(); i++) {
                try {
                    selectStudents.add(new SelectStudents(arrStudents.getJSONObject(i).getString("nameStud"), Integer.parseInt(arrStudents.getJSONObject(i).getString("idStud")), Integer.parseInt(arrStudents.getJSONObject(i).getString("pas")), 1));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            ListView listStuds = findViewById(R.id.listStuds);
            adapter = new SelectStudentsAdapter(this, R.layout.items_select_students, selectStudents);
            listStuds.setAdapter(adapter);
        }
    }

    public void chekInUser(int idStud, String check){
        new RequestTaskChekInUser().execute("https://math123.ru/rest/index.php", ""+idStud, check);
    }
    public void showToast(int id, String check) {
        chekInUser(id, check);
        //создаём и отображаем текстовое уведомление
        Toast toast = Toast.makeText(getApplicationContext(),
                "Отмечен",
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
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
     * Переход к балам студента
     * @id - id_stud
     */

    public void goToBall(String name, int id) {
        new RequestTaskBallUser().execute("https://math123.ru/rest/index.php", name, ""+id, ""+idGroup, ""+idLess);
    }

    /**
     * Получаем данные об оценках пользователя
     */
    class RequestTaskBallUser extends AsyncTask<String, String, String>{
        @Override
        protected void onPreExecute() {

            dialog = new ProgressDialog(StudentsInGroupActivity.this);
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

            String name    = strings[1];
            String idStud  = strings[2];
            String idGroup = strings[3];
            String idLess  = strings[4];

            //будем передавать два параметра
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
            //передаем параметры из наших текстбоксов
            //маршрут
            nameValuePairs.add(new BasicNameValuePair("route", "getBallUser"));
            //айди группы
            nameValuePairs.add(new BasicNameValuePair("id_group", ""+idGroup));
            //айди урока
            nameValuePairs.add(new BasicNameValuePair("id_less", ""+idLess));
            //айди студента
            nameValuePairs.add(new BasicNameValuePair("id_stud", ""+idStud));
            //КлючПроверки
            nameValuePairs.add(new BasicNameValuePair("hesh_key", Global.HESH_KEY));
            //Логин + Пароль
            nameValuePairs.add(new BasicNameValuePair("loginPass", Global.LOGIN+Global.PASS));
            System.out.println("test5 nameValuePairs"+nameValuePairs);
            //собераем их вместе и посылаем на сервер
            String response = hc.execute(postMethod, res);

            postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            //получаем ответ от сервера
            System.out.println("test5-postMetod"+postMethod);

            Intent intent = new Intent(StudentsInGroupActivity.this, StudentBallsActivity.class);
            intent.putExtra(StudentBallsActivity.JsonURL, response);
            intent.putExtra("NameStud", name);
            //intent.putExtra("idGroup", ""+id);
            startActivity(intent);
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

    /** @param result */
    public boolean JSONURL1(String result){
        try{
            System.out.println("test1-json"+ result);
            //создали читателя json объектов и отдали ему строку - result
            JSONObject json  = new JSONObject(result);
            //дальше находим вход в наш json им является ключевое слово data
            JSONArray urls = json.getJSONArray("data");
            System.out.println("test1-g"+urls);
            /*Проверяем есть ли данные*/
            System.out.println("test1-mass"+urls.getJSONObject(0).getString("error"));
            if(urls.getJSONObject(0).getString("error").equals("FALSE")) {
                arrStudents = urls.getJSONObject(1).getJSONArray("students");
                System.out.println("test1-eror1-arr" + arrStudents);
                return true;
            }else{
                System.out.print("test1-err"+urls.getJSONObject(1).getString("errorText"));
                error = urls.getJSONObject(1).getString("errorText");
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        return false;
    }
    class RequestTaskChekInUser extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

            dialog = new ProgressDialog(StudentsInGroupActivity.this);
            dialog.setMessage("Загружаюсь...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.show();
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... strings) {
            //создаем запрос на сервер
            DefaultHttpClient hc = new DefaultHttpClient();
            ResponseHandler<String> res = new BasicResponseHandler();
            //он у нас будет посылать post запрос
            HttpPost postMethod = new HttpPost(strings[0]);
            String idStud = strings[1];
            String check = strings[2];
            //будем передавать два параметра
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
            //передаем параметры из наших текстбоксов
            //маршрут
            nameValuePairs.add(new BasicNameValuePair("route", "upDatePasUser"));
            //айди группы
            nameValuePairs.add(new BasicNameValuePair("id_group", ""+idGroup));
            //айди урока
            nameValuePairs.add(new BasicNameValuePair("id_less", ""+idLess));
            //айди студента
            nameValuePairs.add(new BasicNameValuePair("id_stud", ""+idStud));
            // статус чекбокса 1 был , 2 небыл
            nameValuePairs.add(new BasicNameValuePair("check", check));
            //КлючПроверки
            nameValuePairs.add(new BasicNameValuePair("hesh_key", Global.HESH_KEY));
            //Логин + Пароль
            nameValuePairs.add(new BasicNameValuePair("loginPass", Global.LOGIN+Global.PASS));
            System.out.println("test5 nameValuePairs"+nameValuePairs);
            //собераем их вместе и посылаем на сервер
            try {
                postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //получаем ответ от сервера
            System.out.println("test5-postMetod"+postMethod);
            try {
                String response = hc.execute(postMethod, res);
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
