package lnvappz.com.leadtracker.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import lnvappz.com.leadtracker.AddLead;
import lnvappz.com.leadtracker.LeadDB.DBHelper;
import lnvappz.com.leadtracker.LeadDB.DBMain;
import lnvappz.com.leadtracker.R;

/**
 * Created by i309948 on 9/28/15.
 */
public class LeadCursorAdapter extends CursorAdapter {
    DBHelper dbHelper;
    ListView listView;
    Activity mainActivity;
    Context applicationContext;

    public LeadCursorAdapter(Activity mainActivity1,Context context,Cursor c){
        super(context,c,0);
        this.mainActivity = mainActivity1;
        this.applicationContext = context;
        listView = (ListView) mainActivity.findViewById(R.id.lead_list);
        dbHelper = new DBHelper(context);
    }

    public String convertTimeStampToDate(long timeStamp){
        Date date = new Date(timeStamp); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // the format of your date
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+5:30")); // give a timezone reference for formating (see comment at the bottom
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.lead_list_row, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        String leadDetails="";
        String leadUpdated="";
        final int position = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(DBMain.KEY_ID)));

        // Find fields to populate in inflated template
        TextView leadDetailsTextview = (TextView) view.findViewById(R.id.update_lead_details_textview);
        TextView leadDateAddedTextview = (TextView) view.findViewById(R.id.update_lead_date_added_textview);
        Button updateLeadDetails = (Button) view.findViewById(R.id.update_lead_update_button);
        Button deleteLeadDetails = (Button) view.findViewById(R.id.update_lead_delete_button);

        // Extract properties from cursor
        String lID = cursor.getString(cursor.getColumnIndexOrThrow(DBMain.KEY_ID));
        String lName = cursor.getString(cursor.getColumnIndexOrThrow(DBMain.LEAD_NAME));
        String lArea = cursor.getString(cursor.getColumnIndexOrThrow(DBMain.LEAD_AREA));
        String lType = cursor.getString(cursor.getColumnIndexOrThrow(DBMain.LEAD_TYPE));
        String lpincode = cursor.getString(cursor.getColumnIndexOrThrow(DBMain.PINCODE));
        String lDate = cursor.getString(cursor.getColumnIndexOrThrow(DBMain.DATE_ADDED));
        String lUpdatedDate = cursor.getString(cursor.getColumnIndexOrThrow(DBMain.DATE_UPDATED));
        String lMobile = cursor.getString(cursor.getColumnIndexOrThrow(DBMain.MOBILE_NUMBER));
        String lStatus = cursor.getString(cursor.getColumnIndexOrThrow(DBMain.STATUS));
        String lAgentId = cursor.getString(cursor.getColumnIndexOrThrow(DBMain.AGENT_ID));

        if(DBMain.isDebug) {
            leadDetails = "Id: " + lID;
            leadDetails += "Name: " + lName;
            leadDetails += "Area: " + lArea;
            leadDetails += "Type: " + lType;
            leadDetails += "Pincode: " + lpincode;
            leadDetails += "Mobile: " + lMobile;
            String lStatusString = getStatus(Integer.parseInt(lStatus));
            leadUpdated += "Status: " + lStatusString+"\n";
            leadDetails += "Agent : " + lAgentId;
            leadDetails += "Added on: " + lDate;
            leadDetailsTextview.setText(leadDetails);

            leadUpdated += "Added on: " + convertTimeStampToDate(Long.parseLong(lDate));
            leadUpdated += "Updated on: " + convertTimeStampToDate(Long.parseLong(lUpdatedDate));
            leadDateAddedTextview.setText(leadUpdated);
        }
        else {
            leadDetails += "Name: " + lName+"\n";
            leadDetails += "Agent : " + lAgentId+"\n";
            leadDetailsTextview.setText(leadDetails);
            String lStatusString = getStatus(Integer.parseInt(lStatus));
            leadUpdated += "Status: " + lStatusString+"\n";
            leadUpdated += "Date: " + convertTimeStampToDate(Long.parseLong(lUpdatedDate))+"\n";
            leadDateAddedTextview.setText(leadUpdated);
        }

        if(Integer.parseInt(lAgentId)==0){
            updateLeadDetails.setText("ASSIGN");
        }
        else{
            updateLeadDetails.setText("UPDATE");
        }

        updateLeadDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE: // Yes button clicked
                                Toast.makeText(mainActivity.getApplicationContext(), "Click Updated to update lead with new details", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(applicationContext, AddLead.class);
                                i.putExtra("lead_id", position);
                                mainActivity.startActivity(i);
                                mainActivity.finish();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE: // No button clicked // do nothing
                                Toast.makeText(mainActivity.getApplicationContext(), "No Action Done", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                builder.setMessage("Do you want to update lead details?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
            }
        });

        deleteLeadDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE: // Yes button clicked
                                Toast.makeText(mainActivity.getApplicationContext(), "Deleting the lead", Toast.LENGTH_LONG).show();
                                dbHelper.deleteLead(position);
                                Cursor newCursor = dbHelper.getAllLeads();
                                changeCursor(newCursor);
                                notifyDataSetChanged();
                                Toast.makeText(mainActivity.getApplicationContext(), "Lead Deleted Successfully", Toast.LENGTH_LONG).show();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE: // No button clicked // do nothing
                                Toast.makeText(mainActivity.getApplicationContext(), "No Action Done", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                builder.setMessage("Do you want to update lead details?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
            }
        });
    }

    public String getStatus(int i){
        if(i == DBMain.STATUS_CALL_BACK){
            return "CB";
        }
        if(i == DBMain.STATUS_CLOSED){
            return "C";
        }
        if(i == DBMain.STATUS_DROPPED){
            return "D";
        }
        if(i == DBMain.STATUS_FIXED_APPOINTMENT){
            return "F";
        }
        if(i == DBMain.STATUS_NO_RESPONSE){
            return "NR";
        }
        return "CB";
    }

}
