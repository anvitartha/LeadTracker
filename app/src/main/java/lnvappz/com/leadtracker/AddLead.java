package lnvappz.com.leadtracker;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import lnvappz.com.leadtracker.LeadDB.DBHelper;
import lnvappz.com.leadtracker.LeadDB.DBMain;

/**
 * Created by i309948 on 9/23/15.
 */
public class AddLead extends AppCompatActivity{
    EditText nameEditText, areaEditText, pincodeEditText, mobileEditText;
    Spinner leadTypeSpinner,fieldAgentSpinner;
    Button addLeadButton;
    DBHelper dbHelper;
    Cursor c;
    final Activity addLeadActivity = this;
    int leadId;
    boolean isUpdateFlow=false;

    /**
     * get datetime
     * */
    private Long getDateTime() {
        final Calendar cal = Calendar.getInstance();
        long now = cal.getTimeInMillis();
        return now;
    }

    public void sendSms(String number,String message) {

        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(number, null, message, null, null);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_lead);
        dbHelper = new DBHelper(getApplicationContext());

        nameEditText = (EditText) findViewById(R.id.add_lead_name_edittext);
        areaEditText = (EditText) findViewById(R.id.add_lead_area_edittext);
        pincodeEditText = (EditText) findViewById(R.id.add_lead_pincode_edittext);
        mobileEditText = (EditText) findViewById(R.id.add_lead_mobile_edittext);

        leadTypeSpinner = (Spinner) findViewById(R.id.add_lead_area_type_spinner);
        fieldAgentSpinner = (Spinner) findViewById(R.id.add_lead_agent_list_spinner);

        addLeadButton = (Button) findViewById(R.id.addLead);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int value = extras.getInt("lead_id");
            this.leadId = value;
            this.isUpdateFlow=true;
            Toast.makeText(getApplicationContext(), "got the lead id " + this.leadId, Toast.LENGTH_SHORT).show();
            addLeadButton.setText("UPDATE LEAD");
            onCreateWithLeadId(savedInstanceState);
        } else {
            onCreateWithOutLeadId(savedInstanceState);
        }

        pincodeEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 6) {
                    List<String> items2 = dbHelper.getAgentMobileNumbersBasedOnPinCode(s.toString());

                    // Creating adapter for spinner
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(addLeadActivity, android.R.layout.simple_spinner_item, items2);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    fieldAgentSpinner.setAdapter(dataAdapter);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });

        addLeadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String agentId;
                String agentNumber = DBMain.WITH_OUT_AGENT;
                String lead_name = nameEditText.getText().toString();
                String lead_area = areaEditText.getText().toString();
                String pincode = pincodeEditText.getText().toString();
                String mobile = mobileEditText.getText().toString();
                boolean doAction = true;

                String lead_type = leadTypeSpinner.getSelectedItem().toString();
                if(fieldAgentSpinner.getSelectedItem() != null) {
                    agentNumber = fieldAgentSpinner.getSelectedItem().toString();
                }

                if(agentNumber!=null && agentNumber.equals(DBMain.WITH_OUT_AGENT)){
                    agentId = "0";
                }
                else{
                    agentId = dbHelper.getAgentBasedOnMobileNumber(agentNumber);
                }

                if(lead_name.trim().equals("")){
                    doAction=false;
                    Toast.makeText(getApplicationContext(),"lead name can not be empty",Toast.LENGTH_SHORT).show();
                }

                if(lead_area.trim().equals("")){
                    doAction=false;
                    Toast.makeText(getApplicationContext(),"lead area can not be empty",Toast.LENGTH_SHORT).show();
                }

                if(pincode.trim().length() < 6){
                    doAction=false;
                    Toast.makeText(getApplicationContext(),"pincode can not be less than 6 digits",Toast.LENGTH_SHORT).show();
                }

                if(mobile.trim().length() < 10){
                    doAction=false;
                    Toast.makeText(getApplicationContext(),"mobile number can not be less than 10 digits",Toast.LENGTH_SHORT).show();
                }

                if(doAction) {
                    ContentValues values = new ContentValues();
                    values.put(DBMain.LEAD_NAME, lead_name);
                    values.put(DBMain.LEAD_AREA, lead_area);
                    values.put(DBMain.LEAD_TYPE, lead_type);
                    values.put(DBMain.PINCODE, pincode);
                    values.put(DBMain.AGENT_ID, agentId);
                    values.put(DBMain.MOBILE_NUMBER, mobile);
                    values.put(DBMain.STATUS,DBMain.STATUS_CALL_BACK);

                    if (isUpdateFlow) {
                        values.put(DBMain.DATE_UPDATED, getDateTime());
                        dbHelper.updateLead(values, String.valueOf(leadId));
                        Toast.makeText(getApplicationContext(), "Lead updated successfully" + agentId, Toast.LENGTH_SHORT).show();

                        String message = "Lead::Customer :" + lead_name + ";Area :" + lead_area + ";Type :" + lead_type + ";Mobile :" + mobile;
                        if (!agentNumber.equals(DBMain.WITH_OUT_AGENT)) {
                            sendSms(agentNumber, message);
                        } else {
                            Toast.makeText(getApplicationContext(), "No Agent assigned so no SMS sent", Toast.LENGTH_SHORT).show();
                        }

                        Intent i = new Intent(getApplicationContext(), ManageLeads.class);
                        startActivity(i);
                        finish();
                    } else {
                        values.put(DBMain.DATE_ADDED, getDateTime());
                        values.put(DBMain.DATE_UPDATED, getDateTime());
                        if(dbHelper.isLeadListHasThisNumber(mobile.trim())){
                            Toast.makeText(getApplicationContext(),"mobile number already added",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            dbHelper.createLead(values);
                            Toast.makeText(getApplicationContext(), "Lead Added with agent " + agentId, Toast.LENGTH_SHORT).show();

                            String message = "Lead::Customer :" + lead_name + ";Area :" + lead_area + ";Type :" + lead_type + ";Mobile :" + mobile;
                            if (!agentNumber.equals(DBMain.WITH_OUT_AGENT)) {
                                sendSms(agentNumber, message);
                            } else {
                                Toast.makeText(getApplicationContext(), "No Agent assigned so no SMS sent", Toast.LENGTH_SHORT).show();
                            }

                            nameEditText.setText("");
                            areaEditText.setText("");
                            pincodeEditText.setText("");
                            mobileEditText.setText("");
                            String[] items1 = new String[]{"Commercial", "Residential", "Business"};
                            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(addLeadActivity, android.R.layout.simple_spinner_item, items1);
                            leadTypeSpinner.setAdapter(adapter1);

                            String[] items2 = new String[]{DBMain.WITH_OUT_AGENT};
                            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(addLeadActivity, android.R.layout.simple_spinner_item, items2);
                            fieldAgentSpinner.setAdapter(adapter2);
                        }
                    }
                }
            }
        });
    }

    protected void onCreateWithOutLeadId(Bundle savedInstanceState){
        String[] items1 = new String[]{"Commercial", "Residential", "Business"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items1);
        leadTypeSpinner.setAdapter(adapter1);

        String[] items2 = new String[]{DBMain.WITH_OUT_AGENT};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items2);
        fieldAgentSpinner.setAdapter(adapter2);

    }

    protected void onCreateWithLeadId(Bundle savedInstanceState){
        Cursor c = dbHelper.getLeadDetails(this.leadId);
        nameEditText.setText(c.getString(c.getColumnIndexOrThrow(DBMain.LEAD_NAME)));
        areaEditText.setText(c.getString(c.getColumnIndexOrThrow(DBMain.LEAD_AREA)));
        pincodeEditText.setText(c.getString(c.getColumnIndexOrThrow(DBMain.PINCODE)));
        mobileEditText.setText(c.getString(c.getColumnIndexOrThrow(DBMain.MOBILE_NUMBER)));

        String[] items1 = new String[]{"Commercial", "Residential", "Business"};
        String leadTypePresentInDB = c.getString(c.getColumnIndexOrThrow(DBMain.LEAD_TYPE));
        for(int i=0;i<items1.length;i++){
            if(items1[i].equals(leadTypePresentInDB)){
                String temStr = items1[0];
                items1[0] = items1[i];
                items1[i] = temStr;
                break;
            }
        }
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items1);
        leadTypeSpinner.setAdapter(adapter1);

        String agentId = c.getString(c.getColumnIndexOrThrow(DBMain.AGENT_ID));
        String[] items2 = new String[]{dbHelper.getAgentMobileNumbersBasedOnId(agentId)};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items2);
        fieldAgentSpinner.setAdapter(adapter2);
    }
}
