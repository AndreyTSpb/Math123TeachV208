package potaskun.enot.math123teachv20;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SelectGroupActivity extends AppCompatActivity {
    private TextView textDate;
    private Calendar calendar = Calendar.getInstance();
    private ArrayList<SelectGroups> selectGroups;
    private SelectGroupsAdapter adapter;
    public HashMap<String, Object> hm;
    public static String JsonURL;
    private String error;
    private JSONArray arrGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group);
        /*
         * Кнопка возврата
         */
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        /*Menu*/
        /*
        Текущяя дата
         */
        textDate = findViewById(R.id.textDate);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date now = new Date(calendar.getTimeInMillis());
        textDate.setText(sdf.format(now));

        /**
         * Выбор даты при нажатии на текущию
         */
        textDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, day);
                        setTextView();
                    }
                };
                DatePickerDialog dateDialog = new DatePickerDialog(SelectGroupActivity.this, dateSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );
                dateDialog.show();
            }
        });
        /**
         * Обработка ответа от сервера
         */
        //принимаем параметр который мы послылали в manActivity
        Bundle extras = getIntent().getExtras();
        //превращаем в тип стринг для парсинга
        assert extras != null;
        String json = extras.getString(JsonURL);
        //передаем в метод парсинга
        if(!JSONURL(json)){
            //String error = "Произошла ошибка";
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("error", error);
            System.out.println("test-error"+error);
            startActivity(intent);
        }

        /**
         * вЫВОД списка групп где есть текущая дата
         */

        selectGroups = new ArrayList<>();
        for(int i=0; i<arrGroups.length(); i++){
            try {
                selectGroups.add(new SelectGroups(arrGroups.getJSONObject(i).getString("nameGroup"), arrGroups.getJSONObject(i).getString("idGroup")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ListView listGroup = findViewById(R.id.listGroup);
        adapter = new SelectGroupsAdapter(this, R.layout.items_select_groups, selectGroups);
        listGroup.setAdapter(adapter);
    }
    private void setTextView() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date d = new Date(calendar.getTimeInMillis());
        textDate.setText(sdf.format(d));
    }

    /**
     * Переход в QR код сканер
     * @param name
     * @param id
     */
    public void goQrCode(String name, int id){
        Intent intent = new Intent(this, QrCodeScannerActivity.class);
        intent.putExtra("NameGroup", name);
        intent.putExtra("idGroup", id);
        startActivity(intent);
    }

    /**
     * Переход в список учеников группы
     * @param name
     * @param id
     */
    public void goToGroup(String name, int id){
        Intent intent = new Intent(this, StudentsInGroupActivity.class);
        intent.putExtra("NameGroup", name);
        //intent.putExtra("idGroup", ""+id);
        startActivity(intent);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.service_menu, menu);
        return true;
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
            case R.id.test:
            {
                Intent intent = new Intent(this, TestActivity.class);
                startActivity(intent);
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Обработка ответа сервера
     * @param result */
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
                /*
                 * [{"idGroup":"785","nameGroup":"ММ3-Лимпик-2 (Чт-17:55)","subject":"1","id_less":"109551"},]
                 */
                arrGroups = urls.getJSONObject(1).getJSONArray("groups");
                System.out.println("eror-arr" + arrGroups);
                return true;
            }else{
                System.out.print("test-err"+urls.getJSONObject(1).getString("errorText"));
                error = urls.getJSONObject(1).getString("errorText");
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
