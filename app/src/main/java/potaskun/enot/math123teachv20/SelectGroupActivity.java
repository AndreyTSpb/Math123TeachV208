package potaskun.enot.math123teachv20;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SelectGroupActivity extends AppCompatActivity {
    private TextView textDate;
    private Calendar calendar = Calendar.getInstance();
    private ArrayList<SelectGroups> selectGroups;
    private SelectGroupsAdapter adapter;
    public HashMap<String, Object> hm;
    public static String JsonURL;
    public String error;
    private JSONArray arrGroups;
    public int idGroup;
    public int idLess;
    public String nameGroup;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group);

        clearGlobalDate();
        /*
         * Кнопка возврата
         */
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        /*Menu*/
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
            Intent intent = new Intent(SelectGroupActivity.this, LoginActivity.class);
            intent.putExtra("error", error);
            System.out.println("test-error" + error);
            startActivity(intent);
        }else {

            /*
                Текущяя дата
            */
            String dateNow = extras.getString("dataLess");
            System.out.println("test-group-create" + json);
            textDate = findViewById(R.id.textDate);
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            Date now = new Date(calendar.getTimeInMillis());

            if(dateNow.isEmpty()){
                textDate.setText(sdf.format(now));
            }else {
                textDate.setText(dateNow);
            }
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
             * вЫВОД списка групп где есть текущая дата
             */

            selectGroups = new ArrayList<>();
            for (int i = 0; i < arrGroups.length(); i++) {
                try {
                    selectGroups.add(new SelectGroups(arrGroups.getJSONObject(i).getString("nameGroup"), arrGroups.getJSONObject(i).getString("idGroup"), arrGroups.getJSONObject(i).getString("id_less")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            ListView listGroup = findViewById(R.id.listGroup);
            adapter = new SelectGroupsAdapter(this, R.layout.items_select_groups, selectGroups);
            listGroup.setAdapter(adapter);
        }
    }

    /**
     * Очистка глобальных переменных для группы при загрузке списка всех групп
     */
    private void clearGlobalDate() {
        Global.ID_GROUP ="";
        Global.ID_LESS  = "";
        Global.NAME_GROUP = "";
    }

    /**
     * Установка нужной даты
     */
    private void setTextView() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date d = new Date(calendar.getTimeInMillis());
        String dt_less = sdf.format(d);
        /**
         * Передаем запрос на выборку данных по группе
         */
        new RequestTaskGetGroups().execute("http://math123.ru/rest/index.php", dt_less);
        textDate.setText(sdf.format(d));
    }

    /**
     * Переход в QR код сканер
     *
     * @param name
     * @param id
     */
    public void goQrCode(String name, int id, int idLess) {
        Intent intent = new Intent(this, QrCodeScannerActivity.class);
        intent.putExtra("NameGroup", name);
        intent.putExtra("idGroup", ""+id);
        intent.putExtra("idLess", ""+idLess);
        startActivity(intent);
    }

    /**
     * Переход в список учеников группы
     *
     * @param name
     * @param id
     */
    public void goToGroup(String name, int id, int idLess) {
        this.idGroup = id;
        this.idLess  = idLess;
        this.nameGroup = name;
        Global.ID_GROUP ="" + id;
        Global.ID_LESS  = "" + idLess;
        Global.NAME_GROUP = name;

        new RequestTask().execute("http://math123.ru/rest/index.php");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.service_menu, menu);
        return true;
    }

    /**
     * Метод для кнопки возврата
     * возврата к предыдущему экрану, в котором мы просто завершаем работу текущего:
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.test: {
                Intent intent = new Intent(this, TestActivity.class);
                startActivity(intent);
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Обработка ответа сервера если произошло залогинивание
     *
     * @param result
     */
    public boolean JSONURL(String result) {
        try {
            System.out.println("json_test" + result);
            //создали читателя json объектов и отдали ему строку - result
            JSONObject json = new JSONObject(result);
            //дальше находим вход в наш json им является ключевое слово data
            JSONArray urls = json.getJSONArray("data");
            System.out.println("test-g" + urls);
            /*Проверяем есть ли данные*/
            System.out.println("test-mass" + urls.getJSONObject(0).getString("error"));
            if (urls.getJSONObject(0).getString("error").equals("FALSE")) {

                Global.ID_TEACH = urls.getJSONObject(1).getInt("idTeach");
                Global.NAME_TEACH = urls.getJSONObject(1).getString("nameTeach");
                Global.HESH_KEY = urls.getJSONObject(1).getString("heshKey");
                /*
                 * [{"idGroup":"785","nameGroup":"ММ3-Лимпик-2 (Чт-17:55)","subject":"1","id_less":"109551"},]
                 */
                arrGroups = urls.getJSONObject(1).getJSONArray("groups");
                System.out.println("test-eror-arr" + arrGroups);
                return true;
            } else {
                //System.out.print("test-err" + urls.getJSONObject(1).getString("errorText"));
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
     * Обработка ответа сервера если изменили дату
     *
     * @param result
     */
    public boolean JSONURLGROUP(String result) {
        try {
            System.out.println("json_test" + result);
            //создали читателя json объектов и отдали ему строку - result
            JSONObject json = new JSONObject(result);
            //дальше находим вход в наш json им является ключевое слово data
            JSONArray urls = json.getJSONArray("data");
            System.out.println("test-g" + urls);
            /*Проверяем есть ли данные*/
            System.out.println("test-mass" + urls.getJSONObject(0).getString("error"));
            if (urls.getJSONObject(0).getString("error").equals("FALSE")) {
                /*
                 * [{"idGroup":"785","nameGroup":"ММ3-Лимпик-2 (Чт-17:55)","subject":"1","id_less":"109551"},]
                 */
                arrGroups = urls.getJSONObject(1).getJSONArray("groups");
                System.out.println("test-groups" + arrGroups);
                return true;
            } else {
                //System.out.print("test-err" + urls.getJSONObject(1).getString("errorText"));
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
     * Создаем запрос на получение списка учеников
     */
    class RequestTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... param) {
            try {
                //создаем запрос на сервер
                DefaultHttpClient hc = new DefaultHttpClient();
                ResponseHandler<String> res = new BasicResponseHandler();
                //он у нас будет посылать post запрос
                HttpPost postMethod = new HttpPost(param[0]);
                //будем передавать два параметра
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                //передаем параметры из наших текстбоксов
                //маршрут
                nameValuePairs.add(new BasicNameValuePair("route", "getUsers"));
                //айди группы
                nameValuePairs.add(new BasicNameValuePair("id_group", ""+idGroup));
                //айди урока
                nameValuePairs.add(new BasicNameValuePair("id_less", ""+idLess));
                //КлючПроверки
                nameValuePairs.add(new BasicNameValuePair("hesh_key", Global.HESH_KEY));
                //Логин + Пароль
                nameValuePairs.add(new BasicNameValuePair("loginPass", Global.LOGIN+Global.PASS));
                System.out.println("test nameValuePairs"+nameValuePairs);
                //собераем их вместе и посылаем на сервер
                postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                //получаем ответ от сервера
                System.out.println("test-postMetod"+postMethod);

                String response = hc.execute(postMethod, res);
                System.out.println("test-respons"+response);
                //посылаем на вторую активность полученные параметры
                Intent intent = new Intent(SelectGroupActivity.this, StudentsInGroupActivity.class);
                //то что куда мы будем передавать и что, putExtra(куда, что);
                intent.putExtra(StudentsInGroupActivity.JsonURL, response);
                intent.putExtra("idGroup", ""+idGroup);
                intent.putExtra("idLess", ""+idLess);
                intent.putExtra("nameGroup", nameGroup);
                startActivity(intent);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
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

        @Override
        protected void onPreExecute() {

            dialog = new ProgressDialog(SelectGroupActivity.this);
            dialog.setMessage("Загружаюсь...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.show();
            super.onPreExecute();
        }
    }

    /**
     * Создаем запрос на получение списка групп по указанной дате
     */
    class RequestTaskGetGroups extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... param) {
            try {
                //создаем запрос на сервер
                DefaultHttpClient hc = new DefaultHttpClient();
                ResponseHandler<String> res = new BasicResponseHandler();
                //он у нас будет посылать post запрос
                HttpPost postMethod = new HttpPost(param[0]);
                //получаем дату урока
                String dtLess = param[1];
                //будем передавать два параметра
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
                //передаем параметры из наших текстбоксов
                //маршрут
                nameValuePairs.add(new BasicNameValuePair("route", "getGroups"));
                //id препода
                nameValuePairs.add(new BasicNameValuePair("id_teach", ""+Global.ID_TEACH));
                nameValuePairs.add(new BasicNameValuePair("name_teach", ""+Global.NAME_TEACH));
                //айди урока
                nameValuePairs.add(new BasicNameValuePair("dt_less", dtLess));
                //КлючПроверки
                nameValuePairs.add(new BasicNameValuePair("hesh_key", Global.HESH_KEY));
                //Логин + Пароль
                nameValuePairs.add(new BasicNameValuePair("loginPass", Global.LOGIN+Global.PASS));
                System.out.println("test nameValuePairs"+nameValuePairs);
                //собераем их вместе и посылаем на сервер
                postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                //получаем ответ от сервера
                System.out.println("test-postMetod"+postMethod);
                String response = hc.execute(postMethod, res);
                System.out.println("test-respons"+response);
                //посылаем на вторую активность полученные параметры
                Intent intent = new Intent(SelectGroupActivity.this, SelectGroupActivity.class);
                //то что куда мы будем передавать и что, putExtra(куда, что);
                intent.putExtra(SelectGroupActivity.JsonURL, response);
                intent.putExtra("dataLess", dtLess);
                startActivity(intent);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
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

        @Override
        protected void onPreExecute() {

            dialog = new ProgressDialog(SelectGroupActivity.this);
            dialog.setMessage("Загружаюсь...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.show();
            super.onPreExecute();
        }
    }
}
