package lnvappz.com.leadtracker;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import lnvappz.com.leadtracker.Adapters.AgentCursorAdapter;
import lnvappz.com.leadtracker.LeadDB.DBHelper;
import lnvappz.com.leadtracker.LeadDB.DBMain;

/**
 * Created by i309948 on 9/24/15.
 */
public class AddAgent extends AppCompatActivity {
    EditText numberEditText, pincodeEditText;
    ImageButton pickContactButton, addAgentButton;
    ListView agentList;
    DBHelper dbHelper;
    AgentCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_agent);
        dbHelper = new DBHelper(getApplicationContext());

        numberEditText = (EditText) findViewById(R.id.add_agent_mobile_editText);
        pincodeEditText = (EditText) findViewById(R.id.add_agent_pincode_edittext);

        pickContactButton = (ImageButton) findViewById(R.id.add_agent_pick_mobile_button);
        addAgentButton = (ImageButton) findViewById(R.id.add_agent_add_button);

        agentList = (ListView) findViewById(R.id.agentListView);

        Cursor c;

        if(DBMain.isDebug) {
            c = dbHelper.getAllAgents();
        }
        else{
            c = dbHelper.getAllAgentsWithPincode();
        }

        cursorAdapter = new AgentCursorAdapter(this, getApplicationContext(), c);

        agentList.setAdapter(cursorAdapter);

        addAgentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String agent_number = numberEditText.getText().toString();
                String pincode = pincodeEditText.getText().toString();
                if(agent_number.trim().length()<10){
                    Toast.makeText(getApplicationContext(), "Mobile number can not be less than 10 digits", Toast.LENGTH_SHORT).show();
                }
                else if(pincode.trim().length() < 6){
                    Toast.makeText(getApplicationContext(), "pin code can not be less than 6 digits", Toast.LENGTH_SHORT).show();
                }
                else if(dbHelper.isAgentListHasThisNumber(agent_number.trim())){
                    Toast.makeText(getApplicationContext(), "Agent number already added", Toast.LENGTH_SHORT).show();
                }
                else{
                    addNumber(agent_number, pincode);
                }
            }
        });

        pickContactButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, 1);
            }
        });
    }

    public void addNumber(String number, String pincode) {

        ContentValues values = new ContentValues();
        values.put(DBMain.PINCODE, pincode);
        values.put(DBMain.MOBILE_NUMBER, number);

        dbHelper.createAgent(values);

        Cursor newC;
        if(DBMain.isDebug) {
            newC = dbHelper.getAllAgents();
        }
        else{
            newC = dbHelper.getAllAgentsWithPincode();
        }
        cursorAdapter.changeCursor(newC);
        cursorAdapter.notifyDataSetChanged();

        Toast.makeText(getApplicationContext(), "Agent Added", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri contactData = data.getData();
                Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
                cursor.moveToFirst();

                String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

                numberEditText.setText(number);
            }
        }
    }
}
