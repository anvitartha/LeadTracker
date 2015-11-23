package lnvappz.com.leadtracker.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import lnvappz.com.leadtracker.LeadDB.DBHelper;
import lnvappz.com.leadtracker.LeadDB.DBMain;
import lnvappz.com.leadtracker.R;

/**
 * Created by i309948 on 9/24/15.
 */
public class AgentCursorAdapter extends CursorAdapter {
    DBHelper dbHelper;
    ListView listView;
    Activity mainActivity;

    public AgentCursorAdapter(Activity mainActivity1,Context context,Cursor c){
        super(context,c,0);
        this.mainActivity = mainActivity1;
        listView = (ListView) mainActivity.findViewById(R.id.agentListView);
        dbHelper = new DBHelper(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.agent_list_row, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        final int position = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(DBMain.KEY_ID)));

        // Find fields to populate in inflated template
        TextView agentNumber = (TextView) view.findViewById(R.id.agent_number);
        TextView pinCode = (TextView) view.findViewById(R.id.agent_pincode);
        Button agentDeleteBtn = (Button) view.findViewById(R.id.agent_delete);


        // Extract properties from cursor
        String cAgentId = cursor.getString(cursor.getColumnIndexOrThrow(DBMain.KEY_ID));
        final String cAgentMobileNumber = cursor.getString(cursor.getColumnIndexOrThrow(DBMain.MOBILE_NUMBER));
        String cAgentPinCode = cursor.getString(cursor.getColumnIndexOrThrow(DBMain.PINCODE));

        // Populate fields with extracted properties
        agentNumber.setText("Agent id:"+cAgentId+"\n"+"Number: "+cAgentMobileNumber);
        pinCode.setText("Pin code: "+cAgentPinCode);

        agentDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE: // Yes button clicked
                                dbHelper.deleteAgent(position);
                                Cursor newCursor;
                                if(DBMain.isDebug) {
                                    newCursor = dbHelper.getAllAgents();
                                }
                                else{
                                    newCursor = dbHelper.getAllAgentsWithPincode();
                                }
                                changeCursor(newCursor);
                                notifyDataSetChanged();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE: // No button clicked // do nothing
                                Toast.makeText(mainActivity.getApplicationContext(), "No Clicked", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                builder.setMessage("Are you sure, you want to delete this agent with number : "+cAgentMobileNumber+"?")
                        .setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
            }
        });
    }
}
