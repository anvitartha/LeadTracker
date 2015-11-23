package lnvappz.com.leadtracker;

import android.content.Context;
import android.database.Cursor;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import lnvappz.com.leadtracker.LeadDB.DBHelper;
import lnvappz.com.leadtracker.LeadDB.DBMain;

/**
 * Created by i309948 on 10/3/15.
 */
public class SendReport {
    DBHelper dbHelper;
    Context context;
    String report="Telecalling Setup Name : ";

    public SendReport(Context context1){
        context=context1;
        dbHelper = new DBHelper(context);
    }


    public void sendReport() {
        SmsManager manager = SmsManager.getDefault();
        Cursor c = dbHelper.getAllSendToList();
        int totalSendToNumbersCount = c.getCount();
        if(totalSendToNumbersCount == 0){
            Toast.makeText(context,"Add Send To Numbers to Send Report",Toast.LENGTH_SHORT).show();
        }
        else{
            c.moveToFirst();
            for(int i=0;i<totalSendToNumbersCount;i++){
                manager.sendTextMessage(c.getString(c.getColumnIndexOrThrow(DBMain.MOBILE_NUMBER)), null, report, null, null);
                c.moveToNext();
            }
        }

    }

    public long fieldTime(int field){
        Calendar cal = Calendar.getInstance();
        long now = cal.getTimeInMillis();
        cal.add(field,1);
        long after = cal.getTimeInMillis();
        long fieldTime = after - now;
        return fieldTime;
    }

    public String getTodayDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public boolean isToday(long leadAddedtime){
        Calendar cal = Calendar.getInstance();
        long now = cal.getTimeInMillis();
        long fieldTime = fieldTime(Calendar.DAY_OF_YEAR);
        long isTodayValue = (now-leadAddedtime)/fieldTime;
        if(isTodayValue<1 && isTodayValue>=0){
            return true;
        }
        return false;
    }

    public void generateReport(){
        Cursor c = dbHelper.getAllLeads();
        int noOfLeads = c.getCount();
        int leadsAdded = 0;
        int leadsClosed = 0;
        int leadsCallBack = 0;

        if(noOfLeads>0){
            c.moveToPosition(-1);
            while(c.moveToNext()){

                long leadAddedTime = Long.parseLong(c.getString(c.getColumnIndexOrThrow(DBMain.DATE_ADDED)));
                long leadUpdatedTime = Long.parseLong(c.getString(c.getColumnIndexOrThrow(DBMain.DATE_UPDATED)));
                String status = c.getString(c.getColumnIndexOrThrow(DBMain.STATUS));

                if(isToday(leadAddedTime)){
                    leadsAdded = leadsAdded+1;
                }
                if(isToday(leadUpdatedTime)&& status.equals(String.valueOf(DBMain.STATUS_CALL_BACK))){
                    leadsCallBack = leadsCallBack+1;
                }
                if(isToday(leadUpdatedTime) && status.equals(String.valueOf(DBMain.STATUS_CLOSED))){
                    leadsClosed = leadsClosed + 1;
                }
            }
        }


        Cursor c2 = dbHelper.getAllTeleCallers();
        int count = c2.getCount();

        if(count == 0){
            report = report+ "Telecaller not Set";
            Toast.makeText(context,"Tele caller name not present",Toast.LENGTH_SHORT).show();
        }
        else if (count == 1){
            c2.moveToFirst();
            String teleCallerNameAlreadyEntered = c2.getString(c2.getColumnIndexOrThrow(DBMain.TELECALLER));
            report = report+ teleCallerNameAlreadyEntered;
        }

        report = report + " Date : "+getTodayDate();
        report = report + " Total Leads Generated : " + leadsAdded;
        report = report + " Total Leads Closed : "+leadsClosed;
        report = report + " Total Followups : "+leadsCallBack;


    }

}
