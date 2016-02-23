package com.ckeeda.findme;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.*;
import org.w3c.dom.Text;

import java.util.ArrayList;


public class Findme_main extends Activity {


    public static final String ACTION_SMS_SENT = "com.ckeeda.findme.apis.SMS_SENT_ACTION";
    TextView text_msg,sender;
    ImageView image;
    IntentFilter sms_received;
    AudioManager audioManager;
    LocationManager locationmanager;
    int device_ring_mode;
    String human_readable_address;


    BroadcastReceiver msg_receiver = new BroadcastReceiver(){


        @Override
        public void onReceive(Context context, Intent intent) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            String normal = pref.getString("normal_mode","normal");
            String silent = pref.getString("silent_mode", "silent");
            init();
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            Object[] pdus=(Object[])intent.getExtras().get("pdus");
            SmsMessage Message=SmsMessage.createFromPdu((byte[]) pdus[0]);

            String sender = Message.getOriginatingAddress();
            String message_body = Message.getDisplayMessageBody();

            if(message_body.equalsIgnoreCase(normal)) {
                Log.v("RECEIVER", message_body);
                toNormalmode();
                setMessageData(message_body, sender);
                new address_location().execute(sender);
            }
            if(message_body.equalsIgnoreCase(silent)) {
                Log.v("RECEIVER", message_body);
                toSilentmode();
                setMessageData(message_body, sender);
                new address_location().execute(sender);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findme_main);
        Log.v("Main", "Starting");
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String normal = pref.getString("normal_mode", "normal");
        String silent = pref.getString("silent_mode", "silent");
        init();
     /*   text_msg = (TextView)findViewById(R.id.message);
        sender = (TextView)findViewById(R.id.sender);
        image = (ImageView)findViewById(R.id.ringer_image); */
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if ((getIntent().getStringExtra(Message_receiver.MESSAGE_BODY) == null)) {

            device_ring_mode = audioManager.getRingerMode();
            if (device_ring_mode == AudioManager.RINGER_MODE_NORMAL)
                image.setImageResource(R.mipmap.normal);
            else
                image.setImageResource(R.mipmap.silent);
        } else {
            String msg = getIntent().getStringExtra(Message_receiver.MESSAGE_BODY);
            String msg_sender = getIntent().getStringExtra(Message_receiver.MESSAGE_SENDER);
            if (msg.equalsIgnoreCase(normal)) {
                Log.i("MAIN NORMAL", msg);
                toNormalmode();
                setMessageData(msg, msg_sender);
                new address_location().execute(msg_sender);
            }
            if (msg.equalsIgnoreCase(silent)) {
                Log.i("MAIN SLIENT", msg);
                toSilentmode();
                setMessageData(msg, msg_sender);
                new address_location().execute(msg_sender);
            }
        }
        sms_received = new IntentFilter();
        sms_received.addAction("android.provider.Telephony.SMS_RECEIVED");


    }

    class address_location extends AsyncTask<String,Void,String>{

        Boolean flag;
        @Override
        protected String doInBackground(String... str) {
         //   Looper.prepare();
              Location_Tracker location_tracker = new Location_Tracker(getApplicationContext());
                human_readable_address = "Sorry...Location not found...";
              if(location_tracker.location_found) {
                  double lat = location_tracker.current_location.getLatitude();
                  double longi = location_tracker.current_location.getLongitude();

                  human_readable_address = gethumanreadableaddress(location_tracker.current_location);
                  Log.v("LOCATION", "HUMAN READABLE:" + human_readable_address);
                  sendmsg(str[0],human_readable_address,location_tracker.current_location);
                  return human_readable_address;

              }


            sendmsg(str[0],human_readable_address,null);
            return human_readable_address;
         //   Looper.loop();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.v("postexe", "postexe");
            Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
        }
    }

    void sendmsg(String sender,String message,Location loc){
        if(loc != null) {
            String msg = "Address:" + message +
                    "\n Google Map: http://maps.google.com/?q=+"+loc.getLatitude()+","+loc.getLongitude();
            Log.v("SEND MSG", sender);
            Log.v("SEND MSG", msg);
            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> parts = sms.divideMessage(msg);
            sms.sendMultipartTextMessage(sender, null, parts, null, null);
            //PendingIntent.getBroadcast(getApplicationContext(),0,new Intent(ACTION_SMS_SENT),0),null);
        }
        if(loc == null){
            Log.v("SEND MSG", sender);
            Log.v("SEND MSG", message);
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(sender,null,message,null,null);


        }
    }
    private String gethumanreadableaddress(Location location) {
        // TODO Auto-generated method stub

        String KEY_RESULTS = "result";
        String KEY_ADDRESES = "formatted_address";
        String map;

        String lat = Double.toString(location.getLatitude());
        String longi = Double.toString(location.getLongitude());

        String Url = "http://maps.googleapis.com/maps/api/geocode/xml?latlng="+lat+","+longi+"&sensor=true";
        XmlParser_for_current_address xmlParser = new XmlParser_for_current_address();
        String xml = xmlParser.getXmlFromUrl(Url);
        Document doc = xmlParser.getDataFromXml(xml);
        NodeList nl = doc.getElementsByTagName(KEY_RESULTS);
        Element el = (Element)nl.item(0);
        NodeList nl1 = el.getElementsByTagName(KEY_ADDRESES);
        Element el1 = (Element)nl1.item(0);
        nl1 = el1.getChildNodes();
        map = nl1.item(0).getNodeValue();

        return map;

    }



    void init(){
        text_msg = (TextView)findViewById(R.id.message);
        sender = (TextView)findViewById(R.id.sender);
        image = (ImageView)findViewById(R.id.ringer_image);
    }

    void setMessageData(String msg,String msg_sender) {
        sender.setText(msg_sender);
        text_msg.setText(msg);
    }

    void toSilentmode(){
        audioManager.setStreamVolume(AudioManager.STREAM_RING,
                AudioManager.ADJUST_LOWER,AudioManager.FLAG_ALLOW_RINGER_MODES|AudioManager.RINGER_MODE_VIBRATE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        audioManager.setRingerMode(AudioManager.FLAG_VIBRATE);
        image.setImageResource(R.mipmap.silent);
    }

    void toNormalmode(){
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audioManager.setRingerMode(AudioManager.FLAG_VIBRATE);
        image.setImageResource(R.mipmap.normal);
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(msg_receiver);
        Log.i("MAIN","UNREGISTER RECEIVER");
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(msg_receiver,sms_received);
        Log.i("MAIN","REGISTER RECEIVER");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_findme_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent setting = new Intent(this,SettingsActivity.class);
             startActivity(setting);
        }

        return super.onOptionsItemSelected(item);
    }
}
