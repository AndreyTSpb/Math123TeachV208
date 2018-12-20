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
import java.util.HashMap;

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
             * вЫВОД списка групп где есть текущая дата
             */
            selectStudents = new ArrayList<>();

            for (int i = 1; i < arrStudents.length(); i++) {
                try {
                    selectStudents.add(new SelectStudents(arrStudents.getJSONObject(i).getString("nameStud"), Integer.parseInt(arrStudents.getJSONObject(i).getString("idStud")), 1, 1));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            ListView listStuds = findViewById(R.id.listStuds);
            adapter = new SelectStudentsAdapter(this, R.layout.items_select_students, selectStudents);
            listStuds.setAdapter(adapter);
        }
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
     */

    public void goToBall(String name, int id) {
        Intent intent = new Intent(this, StudentBallsActivity.class);
        intent.putExtra("NameStud", name);
        //intent.putExtra("idGroup", ""+id);
        startActivity(intent);
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
                System.out.println("eror1-arr" + arrStudents);
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
}
