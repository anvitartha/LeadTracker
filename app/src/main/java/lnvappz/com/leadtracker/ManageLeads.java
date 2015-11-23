package lnvappz.com.leadtracker;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import lnvappz.com.leadtracker.Adapters.LeadCursorAdapter;
import lnvappz.com.leadtracker.LeadDB.DBHelper;

/**
 * Created by i309948 on 9/28/15.
 */
public class ManageLeads extends AppCompatActivity {
    ListView leadList;
    DBHelper dbHelper;
    LeadCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lead_list);
        dbHelper = new DBHelper(getApplicationContext());


        leadList = (ListView) findViewById(R.id.lead_list);

        Cursor c = dbHelper.getAllLeads();

        cursorAdapter = new LeadCursorAdapter(this,getApplicationContext(),c);

        leadList.setAdapter(cursorAdapter);



    }
}
