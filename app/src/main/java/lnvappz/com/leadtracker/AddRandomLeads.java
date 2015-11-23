package lnvappz.com.leadtracker;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import lnvappz.com.leadtracker.LeadDB.DBHelper;
import lnvappz.com.leadtracker.LeadDB.DBMain;

/**
 * Created by i309948 on 10/3/15.
 */
public class AddRandomLeads extends AppCompatActivity {
    EditText nameEditText, areaEditText, pincodeEditText, mobileEditText,dateEditText;
    Spinner leadTypeSpinner,fieldAgentSpinner;
    Button addLeadButton;
    DBHelper dbHelper;
    /**
     * get datetime
     */
    private long getDateTime() {
        final Calendar cal = Calendar.getInstance();
        long now = cal.getTimeInMillis();
        return now;
    }

    public static long diff(long time, int field)
    { long fieldTime = getFieldInMillis(field);
        Calendar cal = Calendar.getInstance();
        long now = cal.getTimeInMillis();
        return (time/fieldTime - now / fieldTime);
    }

    private static final long getFieldInMillis(int field)
    { // TODO cache values
        final Calendar cal = Calendar.getInstance();
        long now = cal.getTimeInMillis(); cal.add(field, 1);
        long after = cal.getTimeInMillis();
        return after - now;
    }

    public static final long getOldTimeStamp(int numberOfDaysOld){
        final Calendar cal = Calendar.getInstance();
        long now = cal.getTimeInMillis();
        long fieldTime = getFieldInMillis(Calendar.DAY_OF_YEAR);
        long old = now - (fieldTime*numberOfDaysOld);
        return old;
    }

    public String getRandomNumberOfLenght(int n){
        Random randomCreator = new Random();
        String randomStr = "";
        for(int i=0;i<n;i++){
            int randomInt = randomCreator.nextInt(10);
            randomStr=randomStr+String.valueOf(randomInt);
        }
        return randomStr;
    }

    public void createAndSaveRandomLead(){

        Random randomCreator = new Random();


        final String randomStr = getRandomNumberOfLenght(6);
        ContentValues values = new ContentValues();


        if(nameEditText.getText().toString().trim().equals("")) {
            values.put(DBMain.LEAD_NAME, "Name" + randomStr);
        }
        else{
            values.put(DBMain.LEAD_NAME, nameEditText.getText().toString().trim());
        }

        if(areaEditText.getText().toString().trim().equals("")) {
            values.put(DBMain.LEAD_AREA, "area" + randomStr);
        }
        else{
            values.put(DBMain.LEAD_AREA, areaEditText.getText().toString().trim());
        }

        values.put(DBMain.LEAD_TYPE, leadTypeSpinner.getSelectedItem().toString());

        if(pincodeEditText.getText().toString().trim().equals("")){
            values.put(DBMain.PINCODE, randomStr);
        }
        else {
            values.put(DBMain.PINCODE, pincodeEditText.getText().toString().trim());
        }

        if(dateEditText.getText().toString().trim().equals("")){
            values.put(DBMain.DATE_ADDED, getDateTime());
            values.put(DBMain.DATE_UPDATED, getDateTime());
        }else{
            values.put(DBMain.DATE_ADDED, getOldTimeStamp(Integer.valueOf(dateEditText.getText().toString().trim())));
            values.put(DBMain.DATE_UPDATED, getOldTimeStamp(Integer.valueOf(dateEditText.getText().toString().trim())));
        }

        if(mobileEditText.getText().toString().trim().equals("")) {
            values.put(DBMain.MOBILE_NUMBER, getRandomNumberOfLenght(10));
        }else{
            values.put(DBMain.MOBILE_NUMBER, mobileEditText.getText().toString().trim());
        }

        String[] status = {"C","NR","D","CB","F"};
        int randomStatus = randomCreator.nextInt(4);
        values.put(DBMain.STATUS,status[randomStatus]);


        String agentId="1";


        Cursor c = dbHelper.getAllAgents();
        c.moveToFirst();

        int numberOfAgents = c.getCount();
        int randomAgent = randomCreator.nextInt(numberOfAgents-1);
        for(int i=0;i<randomAgent;i++){
            c.moveToNext();
        }
        agentId = c.getString(c.getColumnIndexOrThrow(DBMain.KEY_ID));
        values.put(DBMain.AGENT_ID, agentId);


        dbHelper.createLead(values);
        Toast.makeText(getApplicationContext(), "Lead Added with agent " + agentId, Toast.LENGTH_SHORT).show();
    }

    private void updateLabel(){
        final Calendar myCalendar = Calendar.getInstance();
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateEditText.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.random_leads_addition);
        dbHelper = new DBHelper(getApplicationContext());

        nameEditText = (EditText) findViewById(R.id.random_lead_name_edittext);
        areaEditText = (EditText) findViewById(R.id.random_lead_area_edittext);
        pincodeEditText = (EditText) findViewById(R.id.random_lead_pincode_edittext);
        mobileEditText = (EditText) findViewById(R.id.random_lead_mobile_edittext);
        dateEditText = (EditText) findViewById(R.id.random_lead_date_edittext);

        leadTypeSpinner = (Spinner) findViewById(R.id.random_lead_area_type_spinner);
        fieldAgentSpinner = (Spinner) findViewById(R.id.random_lead_agent_list_spinner);

        addLeadButton = (Button) findViewById(R.id.random_lead_add_lead_button);

        String[] items1 = new String[]{"Commercial", "Residential", "Business"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items1);
        leadTypeSpinner.setAdapter(adapter1);

        String[] items2 = new String[]{DBMain.WITH_OUT_AGENT};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items2);
        fieldAgentSpinner.setAdapter(adapter2);

        addLeadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 1; i++) {
                    createAndSaveRandomLead();
                }
            }
        });
    }
}
