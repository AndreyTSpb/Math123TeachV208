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

public class StudentsInGroupActivity extends AppCompatActivity {

    private String nameGroup;
    private int idGroup;
    private ArrayList<SelectStudents> selectStudents;
    private SelectStudentsAdapter adapter;

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
        Intent intent = getIntent();
        nameGroup = intent.getStringExtra("NameGroup").toString();
        //idGroup = Integer.parseInt(intent.getStringExtra("idGroup").toString());
        System.out.println("testtest"+nameGroup);
        TextView ng = findViewById(R.id.nameGroup);
        ng.setText(nameGroup);

        /**
         * вЫВОД списка групп где есть текущая дата
         */
        selectStudents = new ArrayList<>();
        for(int i=1; i<9; i++){
            selectStudents.add(new SelectStudents("Ученик № "+i, i, 1,1));
        }

        ListView listStuds = findViewById(R.id.listStuds);
        adapter = new SelectStudentsAdapter(this, R.layout.items_select_students, selectStudents);
        listStuds.setAdapter(adapter);
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
    public boolean JSONURL(String result){
        try{
            System.out.println("json_test"+ result);
            //создали читателя json объектов и отдали ему строку - result
            JSONObject json  = new JSONObject(result);
            //дальше находим вход в наш json им является ключевое слово data
            JSONArray urls = json.getJSONArray("data");
            System.out.println("test-g"+urls);
            /*Проверяем есть ли данные*/
            System.out.println("test-mass"+urls.getJSONObject(0).getString("error"));
            if(urls.getJSONObject(0).getString("error").equals("FALSE")) {

                Global.ID_TEACH   = urls.getJSONObject(1).getInt("idTeach");
                Global.NAME_TEACH = urls.getJSONObject(1).getString("nameTeach");
                Global.HESH_KEY   = urls.getJSONObject(1).getString("heshKey");

                return true;
            }else{
                System.out.print("test-err"+urls.getJSONObject(1).getString("errorText"));
                String error = urls.getJSONObject(1).getString("errorText");
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("error", error);
                System.out.println("test-error"+error);
                startActivity(intent);
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        return false;
    }
}
