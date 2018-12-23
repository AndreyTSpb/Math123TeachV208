package potaskun.enot.math123teachv20;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import java.util.ArrayList;
import java.util.List;

public class CartUserActivity extends AppCompatActivity {
    public static String JsonURL;
    public String error;
    private ProgressDialog dialog;
    private String nameStud;
    private String acses;
    private String birthDay;
    private String dogovor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_user);
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
        }else {
            /*Заполняем поля на странице*/
            /*Имя Ученика*/
            nameStud = extras.getString("nameStud");
            TextView nS = findViewById(R.id.nameStud);
            nS.setText(nameStud);
            /*Разреешон ли доступ*/
            TextView acsText = findViewById(R.id.assecUser);
            if(acses.equals("TRUE")){
                acsText.setText("Разрешен");
                acsText.setTextColor(Color.parseColor("#669900"));
            }else{
                acsText.setText("Запрещен");
                acsText.setTextColor(Color.parseColor("#990005"));
            }
            /*День Рождения*/
            /*?Родитель?*/
            /*Договор Сдал/неСдал*/

            /*Вспомогательные кнопки*/
            TextView tvJson = findViewById(R.id.textJson);
            tvJson.setText(json);

            TextView glob = findViewById(R.id.textGlobal);
            glob.setText("hesh:" + Global.HESH_KEY + " ,group:" + Global.NAME_GROUP + " ,teach:" + Global.NAME_TEACH);

            /*Кнопки*/
            Button qrCod = findViewById(R.id.buttonScan);
            qrCod.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goQrCode(Global.NAME_GROUP, Global.ID_GROUP, Global.ID_LESS);
                }
            });
            Button toGroup = findViewById(R.id.buttonToGroup);
            toGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToGroup();
                }
            });
        }
    }

    /**
     * Обработка ответа сервера
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
                acses = urls.getJSONObject(1).getString("acs");
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
     * Переход в QR код сканер
     *
     * @param name
     * @param id
     */
    public void goQrCode(String name, String id, String idLess) {
        Intent intent = new Intent(this, QrCodeScannerActivity.class);
        intent.putExtra("NameGroup", name);
        intent.putExtra("idGroup", ""+id);
        intent.putExtra("idLess", ""+idLess);
        startActivity(intent);
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
     * Переход в список учеников группы
     *
     */
    public void goToGroup() {
        new RequestTask().execute("https://math123.ru/rest/index.php");
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
                nameValuePairs.add(new BasicNameValuePair("id_group", Global.ID_GROUP));
                //айди урока
                nameValuePairs.add(new BasicNameValuePair("id_less", Global.ID_LESS));
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
                Intent intent = new Intent(CartUserActivity.this, StudentsInGroupActivity.class);
                //то что куда мы будем передавать и что, putExtra(куда, что);
                intent.putExtra(StudentsInGroupActivity.JsonURL, response);
                intent.putExtra("idGroup", Global.ID_GROUP);
                intent.putExtra("idLess", Global.ID_LESS);
                intent.putExtra("nameGroup", Global.NAME_GROUP);
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

            dialog = new ProgressDialog(CartUserActivity.this);
            dialog.setMessage("Загружаюсь...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.show();
            super.onPreExecute();
        }
    }

    /**
     * Проверяем результат true или false
     * @param json
     * @return
     * @throws JSONException
     */
    public Boolean checkStud(String json) throws JSONException {
        System.out.println("test3-json-checkStud "+json);
        JSONObject json2 = new JSONObject(json);
        //дальше находим вход в наш json им является ключевое слово data
        JSONArray urls = json2.getJSONArray("data");
        System.out.println("test3-urls" + urls);
        String acs = urls.getJSONObject(1).getString("acs");
        System.out.println("test3-acs" + acs);
        return acs.equals("TRUE");
    }
}
