package lnvappz.com.leadtracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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
import lnvappz.com.leadtracker.Adapters.ReportCursorAdapter;
import lnvappz.com.leadtracker.LeadDB.DBHelper;
import lnvappz.com.leadtracker.LeadDB.DBMain;

/**
 * Created by i309948 on 10/1/15.
 */

public class ReportManage extends AppCompatActivity {
    EditText addNumberEditText,telecallerEditText;
    ImageButton pickContactButton, addNumberButton, telecallerSaveButton;
    Button reportGenerateButton;
    ListView numberList;
    DBHelper dbHelper;
    ReportCursorAdapter cursorAdapter;
    Context context;
    Activity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_manage);
        context = this;
        dbHelper = new DBHelper(context);
        mainActivity = this;

        telecallerEditText = (EditText) findViewById(R.id.report_manage_telecaller_edittext);
        telecallerSaveButton = (ImageButton) findViewById(R.id.report_manage_save_telecaller_button);
        addNumberEditText = (EditText) findViewById(R.id.report_manage_add_number_edit_text);
        pickContactButton = (ImageButton) findViewById(R.id.report_manage_pick_contact_button);
        addNumberButton = (ImageButton) findViewById(R.id.report_manage_add_number_button);
        reportGenerateButton = (Button) findViewById(R.id.report_manage_generate_report_button);
        numberList = (ListView) findViewById(R.id.report_manage_number_list);

        Cursor c = dbHelper.getAllSendToList();
        Cursor c2 = dbHelper.getAllTeleCallers();

        cursorAdapter = new ReportCursorAdapter(this, getApplicationContext(), c);

        numberList.setAdapter(cursorAdapter);

        int teleCallerCount = c2.getCount();
        if(teleCallerCount==1){
            c2.moveToFirst();
            telecallerEditText.setText(c2.getString(c2.getColumnIndexOrThrow(DBMain.TELECALLER)));
        }

        addNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String send_to_number = addNumberEditText.getText().toString();
                if(send_to_number.trim().length() < 10){
                    Toast.makeText(mainActivity,"number can not be less than 10 digits",Toast.LENGTH_SHORT).show();
                }
                else{
                    addNumber(send_to_number);
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

        telecallerSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String telecallerName = telecallerEditText.getText().toString().trim();

                Cursor c = dbHelper.getAllTeleCallers();
                int count = c.getCount();

                if(telecallerName.trim().equals("")){
                    Toast.makeText(getApplicationContext()," Please enter some telecaller name",Toast.LENGTH_SHORT).show();
                }
                else{
                    if(count == 0){
                        dbHelper.createTeleCaller(telecallerName);
                        Toast.makeText(getApplicationContext(),"Tele caller name saved successfully",Toast.LENGTH_SHORT).show();
                    }
                    else if (count == 1){
                        c.moveToFirst();
                        String teleCallerNameAlreadyEntered = c.getString(c.getColumnIndexOrThrow(DBMain.TELECALLER));

                        if(telecallerName.equals(teleCallerNameAlreadyEntered)){
                            Toast.makeText(getApplicationContext(),"Tele caller name already saved",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            ConfirmTeleCallerUpdate(telecallerName,teleCallerNameAlreadyEntered);
                        }
                    }
                }
            }
        });

        reportGenerateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendReport reporter = new SendReport(context);
                reporter.generateReport();
                reporter.sendReport();
            }
        });
    }

    public void addNumber(String number){
        boolean numberAlreadThere = dbHelper.isSendToListHasThisNumber(number);

        if(numberAlreadThere){
            Toast.makeText(context,"This number is already added",Toast.LENGTH_SHORT).show();
        }
        else {
            ContentValues values = new ContentValues();
            values.put(DBMain.MOBILE_NUMBER, number);

            dbHelper.createSendTo(values);

            Cursor newC = dbHelper.getAllSendToList();
            cursorAdapter.changeCursor(newC);
            cursorAdapter.notifyDataSetChanged();

            Toast.makeText(getApplicationContext(), "New Number Added", Toast.LENGTH_SHORT).show();
        }
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

                addNumber(number);
            }
        }
    }

    public void ConfirmTeleCallerUpdate(final String telecallerName,final String teleCallerNameAlreadyEntered){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE: // Yes button clicked
                        Toast.makeText(getApplicationContext(), "Yes Clicked", Toast.LENGTH_SHORT).show();
                        dbHelper.updateTelecallerName(telecallerName);
                        Toast.makeText(getApplicationContext(), "Tele caller name successfully updated", Toast.LENGTH_SHORT).show();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE: // No button clicked // do nothing
                        Toast.makeText(getApplicationContext(), "No Clicked", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Do you want to update the old telecaller name " + teleCallerNameAlreadyEntered + "?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
    }
}

