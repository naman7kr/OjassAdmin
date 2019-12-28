package ojassadmin.nitjsr.in.ojassadmin;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static ojassadmin.nitjsr.in.ojassadmin.Constants.FIREBASE_REF_NOTIFICATIONS;

public class NotificationActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etTitle, etBody;
    private Spinner spChannel;
    private Button btnSend;
    private DatabaseReference notiRef;
    private RequestQueue queue;
    private static final String VOLLEY_TAG = "VolleyTag";
    private static final String FCM_KEY = "AAAAX90eYv8:APA91bG_JJUSsjVJfntkCsVDGn-_0oecmrV4QX-fOeqP2WZr6R8bSlUX8_4NyAlg6ElfzqYqQSkK-ctRZ4zHh21ziPDpqR6wSl_w4A4k_a9GdyvN5B3--qNeUI6zn80HOkNrKgLg5irD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        queue  =Volley.newRequestQueue(this);

        btnSend = findViewById(R.id.btn_send);
        etTitle = findViewById(R.id.et_noti_title);
        etBody = findViewById(R.id.et_noti_body);
        spChannel = findViewById(R.id.sp_noti_channel);

        notiRef = FirebaseDatabase.getInstance().getReference(FIREBASE_REF_NOTIFICATIONS);

        btnSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == btnSend){
            sendPushNotification();
        }
    }

    void sendPushNotification() {
        String topic = spChannel.getSelectedItem().toString();
        if(!TextUtils.isEmpty(etTitle.getText()) && !TextUtils.isEmpty(etBody.getText())) {
            String key = notiRef.child(topic).push().getKey();
            notiRef.child(topic).child(key).child("ques").setValue(etTitle.getText().toString());
            notiRef.child(topic).child(key).child("ans").setValue(etBody.getText().toString());
            Toast.makeText(getApplication(),"Notification Sent",Toast.LENGTH_SHORT).show();
            NotificationTask notificationTask = new NotificationTask();
            notificationTask.execute(etTitle.getText().toString().trim(),etBody.getText().toString().trim());
            etTitle.setText("");
            etBody.setText("");
        } else {
            Toast.makeText(getApplication(),"Field can't be left blank!",Toast.LENGTH_SHORT).show();
        }
    }

    class NotificationTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                String jsonResponse;

                URL url = new URL("https://onesignal.com/api/v1/notifications");
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setUseCaches(false);
                con.setDoOutput(true);
                con.setDoInput(true);

                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                con.setRequestProperty("Authorization", "Basic MDViZTQ3MTItZjNmYi00NjNmLWE2NTgtZDA1NTZmOTc0MTI4");
                con.setRequestMethod("POST");
                String strJsonBody = "{"
                        +   "\"app_id\": \"a1a3776f-af94-474f-af74-f852c5cbc974\","
                        +   "\"included_segments\": [\"All\"],"
                        +   "\"data\": {\"foo\": \"bar\"},"
                        +   "\"headings\": {\"en\": \"" + strings[0] +"\"},"
                        +   "\"contents\": {\"en\": \"" + strings[1] +"\"},"
                        +   "\"small_icon\":  \"icon\""
                        + "}";

                byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                con.setFixedLengthStreamingMode(sendBytes.length);

                OutputStream outputStream = con.getOutputStream();
                outputStream.write(sendBytes);

                int httpResponse = con.getResponseCode();

                if (  httpResponse >= HttpURLConnection.HTTP_OK
                        && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                    Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                    jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                    scanner.close();
                }
                else {
                    Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                    jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                    scanner.close();
                }
                System.out.println("jsonResponse:\n" + jsonResponse);

            } catch(Throwable t) {
                t.printStackTrace();
            }
            return null;
        }
    }
}
