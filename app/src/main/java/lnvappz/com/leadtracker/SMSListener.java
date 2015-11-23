package lnvappz.com.leadtracker;

/**
 * Created by i309948 on 10/3/15.
 */

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import lnvappz.com.leadtracker.LeadDB.DBHelper;
import lnvappz.com.leadtracker.LeadDB.DBMain;


public class SMSListener extends BroadcastReceiver{
    private SharedPreferences preferences;
    private ArrayList<String> numAl = new ArrayList<String>();
    DBHelper dbHelper;


    @Override
    public void onReceive(Context context, Intent intent) {
        dbHelper = new DBHelper(context);
        Calendar cal = Calendar.getInstance();
        boolean isPresent = false;
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();        //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from;

            if (bundle != null) {
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();
                        System.out.println(msgBody);
                        System.out.println(msg_from);
                        msg_from = msg_from.substring(msg_from.length()-10);
                        System.out.println(msg_from);

                        Cursor c = dbHelper.getAllAgentMobiles();

                        c.moveToFirst();

                        while (!c.isAfterLast()) {
                            String num = c.getString(c.getColumnIndex(DBMain.MOBILE_NUMBER));
                            if(num.length()>=10) {
                                int length = num.length();
                                if (msg_from.contains(num.substring(length - 10))) {
                                    isPresent = true;
                                    break;
                                } else {
                                    c.moveToNext();
                                }
                            }
                            else {
                                c.moveToNext();
                            }
                        }

                        if(isPresent) {
                            String[] msg = msgBody.split(" ");
                            String msgKey = msg[0].trim();
                            String msgStatus = msg[1].trim();
                            int messageStatus = getMessageStatus(msgStatus);
                            ContentValues values = new ContentValues();
                            values.put(DBMain.STATUS, messageStatus);
                            long now = cal.getTimeInMillis();
                            values.put(DBMain.DATE_UPDATED,now);
                            dbHelper.updateLeadStatus(values, String.valueOf(msgKey));
                            Toast.makeText(context, "Lead updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                         Log.d("Exception caught",e.getMessage());
                }
            }
        }
    }

    public int getMessageStatus(String msgStatus){
        if(msgStatus.toUpperCase().equals("C")){
            return DBMain.STATUS_CLOSED;
        }
        if(msgStatus.toUpperCase().equals("CB")){
            return DBMain.STATUS_CALL_BACK;
        }
        if(msgStatus.toUpperCase().equals("D")){
            return DBMain.STATUS_DROPPED;
        }
        if(msgStatus.toUpperCase().equals("F")){
            return DBMain.STATUS_FIXED_APPOINTMENT;
        }
        if(msgStatus.toUpperCase().equals("NR")){
            return DBMain.STATUS_NO_RESPONSE;
        }
        return DBMain.STATUS_CALL_BACK;
    }
}
