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
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Date;
import java.util.List;

public class SelectActionActivity extends AppCompatActivity {

    public static String JsonURL;
    public String error;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_action);
        /*
         * Кнопка возврата
         */
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        /*
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
            Intent intent = new Intent(SelectActionActivity.this, LoginActivity.class);
            intent.putExtra("error", error);
            System.out.println("test-error" + error);
            startActivity(intent);
        }else {
            TextView textTeachName = findViewById(R.id.textTeachName);
            textTeachName.setText(Global.NAME_TEACH);

            Button buttonToLess = findViewById(R.id.buttonToLess);
            buttonToLess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToGroup();
                }
            });
            /**Сообщение об ошибке*/
            String error = extras.getString("error");
            System.out.println("test-ererer"+error);
            if (error != null) {
                ToastError(error);
            }
        }
    }
    /**
     * Переход к урокам
     */
    public void goToGroup() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date now = new Date();
        String dt = sdf.format(now);
        new RequestTaskGetGroups().execute("http://math123.ru/rest/index.php", dt);
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
                Intent intent = new Intent(SelectActionActivity.this, SelectGroupActivity.class);
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

            dialog = new ProgressDialog(SelectActionActivity.this);
            dialog.setMessage("Загружаюсь...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.show();
            super.onPreExecute();
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
     * Обработка отображения ошибок
     * @param error
     */
    public void ToastError (String error){
        //создаём и отображаем текстовое уведомление
        Toast toast = Toast.makeText(getApplicationContext(),
                error,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
