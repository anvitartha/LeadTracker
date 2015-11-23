package lnvappz.com.leadtracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import lnvappz.com.leadtracker.LeadDB.DBHelper;
import lnvappz.com.leadtracker.LeadDB.DBMain;

public class MainActivity extends AppCompatActivity {

    ImageButton addLeadButton, manageLeadButton , reportManageButton, mangeFieldAgentButton, reassignLeadsButton;
    final Context context = this;
    DBHelper dbHelper;
    Activity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DBHelper(getApplicationContext());
        mainActivity = this;

        addLeadButton = (ImageButton) findViewById(R.id.main_add_lead);
        mangeFieldAgentButton = (ImageButton) findViewById(R.id.main_field_agent_list);
        manageLeadButton = (ImageButton) findViewById(R.id.main_list_leads);
        reportManageButton = (ImageButton) findViewById(R.id.main_report_manage);
        reassignLeadsButton = (ImageButton) findViewById(R.id.main_reassign_leads);

        addLeadButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(context, AddLead.class);
                startActivity(intent);

            }

        });

        mangeFieldAgentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddAgent.class);
                startActivity(intent);
            }
        });

        manageLeadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ManageLeads.class);
                startActivity(intent);
            }
        });

        reportManageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReportManage.class);
                startActivity(intent);
            }
        });

        reassignLeadsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE: // Yes button clicked
                                doSendSMSForCBLeads();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE: // No button clicked // do nothing
                                Toast.makeText(getApplicationContext(), "No Action Done", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                builder.setMessage("All CB Leads info will be re-assiged to Agents, Are you sure? ")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
    }

    public void doSendSMSForCBLeads(){
        Cursor cursor = dbHelper.getAllLeadsWithCBStatusAndAgentNumber();
        cursor.moveToPosition(-1);
        Toast.makeText(getApplicationContext(), cursor.getCount()+" leads re-assigned", Toast.LENGTH_SHORT).show();

        while (cursor.moveToNext()) {
            // Extract properties from cursor
            String lead_name = cursor.getString(cursor.getColumnIndexOrThrow(DBMain.LEAD_NAME));
            String lead_area = cursor.getString(cursor.getColumnIndexOrThrow(DBMain.LEAD_AREA));
            String lead_type = cursor.getString(cursor.getColumnIndexOrThrow(DBMain.LEAD_TYPE));
            String mobile = cursor.getString(cursor.getColumnIndexOrThrow(DBMain.MOBILE_NUMBER));
            String agentNumber = cursor.getString(cursor.getColumnIndexOrThrow("AgentMobileNumber"));

            String message = "Lead::Customer :" + lead_name + ";Area :" + lead_area + ";Type :" + lead_type + ";Mobile :" + mobile;
            if (!agentNumber.equals(DBMain.WITH_OUT_AGENT)) {
                sendSms(agentNumber, message);
                System.out.println(message + "to agent" + agentNumber);
                Toast.makeText(getApplicationContext(), "Lead re-assigned to Agent", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "No SMS sent as no agent assigned", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void sendSms(String number,String message) {

        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(number, null, message, null, null);
    }
}
