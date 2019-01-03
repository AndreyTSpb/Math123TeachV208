package potaskun.enot.math123teachv20;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;


import com.google.zxing.Result;

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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import static android.Manifest.permission.CAMERA;

public class QrCodeScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView mScannerView;
    public String idGroup;
    public String idLess;
    public String nameGroup;
    private ProgressDialog dialog;
    private String nameStud;
    private String idStud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_scanner);
        /*
         * Кнопка возврата
         */
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Log.e("onCreate", "onCreate");
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                Toast.makeText(getApplicationContext(), "Доступ уже разрешен", Toast.LENGTH_LONG).show();
            } else {
                requestPermission();
            }
        }
        Intent intent = getIntent();
        idGroup = intent.getStringExtra("idGroup");
        idLess = intent.getStringExtra("idLess");
        nameGroup = intent.getStringExtra("NameGroup");
        Global.ID_GROUP   = idGroup;
        Global.ID_LESS    = idLess;
        Global.NAME_GROUP = nameGroup;

        /**Сообщение об ошибке*/
        String error = intent.getStringExtra("error");
        System.out.println("test-ererer"+error);
        if (error != null) {
            ToastError(error);
        }

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    private boolean checkPermission() {
        return ( ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA ) == PackageManager.PERMISSION_GRANTED);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(QrCodeScannerActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    /**
     * Обработка QR-кода, вывод сообщения на экран.
     * @param rawResult
     */

    @Override
    public void handleResult(Result rawResult) {
        final String result = rawResult.getText();
        Log.d("QRCodeScanner", rawResult.getText());
        Log.d("QRCodeScanner", rawResult.getBarcodeFormat().toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Результат сканирования");
        //Нажатие на кнопку Ок продолжить
        builder.setPositiveButton("Еще", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Пересканировать
                mScannerView.resumeCameraPreview(QrCodeScannerActivity.this);
            }
        });
        //Переход впи нажатии кнопки Проверить
        builder.setNeutralButton("Проверить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goToCartUser();
            }
        });
        builder.setNegativeButton("Группа", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goToGroup();
            }
        });
        //обработка JSON
        String json = rawResult.getText();
        System.out.println("test3-json" + json);
        String id_user = "";
        try {
            JSONObject json2 = new JSONObject(json);
            //дальше находим вход в наш json им является ключевое слово data
            JSONArray urls = json2.getJSONArray("data");
            System.out.println("test3-urls" + urls);
            idStud = urls.getJSONObject(0).getString("idStud");
            nameStud = urls.getJSONObject(0).getString("nameStud");
            id_user = nameStud; //Для проверки есть ли данныее о пользователе
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Строка с именем и то что все ок
        if (!id_user.isEmpty()){
            String name = nameStud;
            builder.setMessage("Имя: " + name + "\n" +"REAL STRING:"+rawResult.getText());
            AlertDialog alert1 = builder.create();
            alert1.show();
        }else{
            builder.setMessage("Повторите сканироание!!!" + "\n" +"REAL STRING:"+rawResult.getText());
            AlertDialog alert1 = builder.create();
            alert1.show();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if(mScannerView == null) {
                    mScannerView = new ZXingScannerView(this);
                    setContentView(mScannerView);
                }
                mScannerView.setResultHandler(this);
                mScannerView.startCamera();
            } else {
                requestPermission();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mScannerView.stopCamera();
    }

    public void postStudCheckToLess(){
        //отправить данные о том что ученик пришел
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
     * Переход в список учеников группы
     *
     */
    public void goToGroup() {
        new RequestTask().execute("http://math123.ru/rest/index.php");
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
                Intent intent = new Intent(QrCodeScannerActivity.this, StudentsInGroupActivity.class);
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

            dialog = new ProgressDialog(QrCodeScannerActivity.this);
            dialog.setMessage("Загружаюсь...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.show();
            super.onPreExecute();
        }
    }

    /**
     * Создаем запрос на получение данных об ученике
     */
    class RequestTaskTestUser extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(QrCodeScannerActivity.this);
            dialog.setMessage("Загружаюсь...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... param) {
            try {
                //создаем запрос на сервер
                DefaultHttpClient hc = new DefaultHttpClient();
                ResponseHandler<String> res = new BasicResponseHandler();
                //он у нас будет посылать post запрос
                HttpPost postMethod = new HttpPost(param[0]);
                //будем передавать два параметра
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
                //передаем параметры из наших текстбоксов
                //маршрут
                nameValuePairs.add(new BasicNameValuePair("route", "getTestQr"));
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
                System.out.println("test3 nameValuePairs"+nameValuePairs);
                //собераем их вместе и посылаем на сервер
                postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                //получаем ответ от сервера
                System.out.println("test3-postMetod"+postMethod);
                String response = hc.execute(postMethod, res);
                System.out.println("test3-respons"+response);

                Intent intent = new Intent(QrCodeScannerActivity.this, CartUserActivity.class);
                //то что куда мы будем передавать и что, putExtra(куда, что);
                intent.putExtra(CartUserActivity.JsonURL, response);
                intent.putExtra("idGroup", Global.ID_GROUP);
                intent.putExtra("idLess", Global.ID_LESS);
                intent.putExtra("nameGroup", Global.NAME_GROUP);
                intent.putExtra("nameStud", nameStud);

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
            super.onPostExecute(result);
            dialog.dismiss();
        }
    }


    /**
     * Переход к карточки ученика
     */
    public void goToCartUser() {
        new RequestTaskTestUser().execute("http://math123.ru/rest/index.php");
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
