package com.ckeeda.findme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class Message_receiver extends BroadcastReceiver {

    final static String MESSAGE_BODY = "message_body";
    final static String MESSAGE_SENDER = "message_sender";
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String start = pref.getString("start_app","start");

        Object[] pdus=(Object[])intent.getExtras().get("pdus");
        SmsMessage Message=SmsMessage.createFromPdu((byte[]) pdus[0]);

        String sender = Message.getOriginatingAddress();
        String message_body = Message.getDisplayMessageBody();

        Intent start_main_activity = new Intent(context, Findme_main.class);
        start_main_activity.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        start_main_activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        start_main_activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        start_main_activity.putExtra(MESSAGE_SENDER, sender);

        if(message_body.contains(" ")) {
            Log.v("RECEIVER","2 WORDS");
            String[] msg = message_body.split("\\s+");
            Log.v("Split", String.valueOf(msg.length));

            if(msg[0].equalsIgnoreCase(start)) {
                Log.v("RECEIVER",msg[1]);
                start_main_activity.putExtra(MESSAGE_BODY,msg[1]);
                context.startActivity(start_main_activity);
            }
        }else {
            Log.v("RECEIVER", "1 WORD");
            if (message_body.equalsIgnoreCase(start)) {
                start_main_activity.putExtra(MESSAGE_BODY, message_body);
                context.startActivity(start_main_activity);
            }
        }
//         start_main_activity.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        }


    }

