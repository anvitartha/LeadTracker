package lnvappz.com.leadtracker.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import lnvappz.com.leadtracker.LeadDB.DBHelper;
import lnvappz.com.leadtracker.LeadDB.DBMain;
import lnvappz.com.leadtracker.R;

/**
 * Created by i309948 on 10/1/15.
 */
public class ReportCursorAdapter extends CursorAdapter{
    DBHelper dbHelper;
    ListView listView;
    Activity mainActivity;

    public ReportCursorAdapter(Activity mainActivity1,Context context,Cursor c){
        super(context,c,0);
        this.mainActivity = mainActivity1;
        listView = (ListView) mainActivity.findViewById(R.id.report_manage_number_list);
        dbHelper = new DBHelper(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.report_manage_list_row, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        final int position = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(DBMain.KEY_ID)));

        // Find fields to populate in inflated template
        TextView mrNumberTextView = (TextView) view.findViewById(R.id.report_manage_list_number_texview);
        ImageButton mrDeleteNumberButton = (ImageButton) view.findViewById(R.id.report_manage_list_number_delete_button);


        // Extract properties from cursor
        String mrKey = cursor.getString(cursor.getColumnIndexOrThrow(DBMain.KEY_ID));
        final String mrMobileNumber = cursor.getString(cursor.getColumnIndexOrThrow(DBMain.MOBILE_NUMBER));

        // Populate fields with extracted properties
        mrNumberTextView.setText("\n"+mrMobileNumber);

        mrDeleteNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE: // Yes button clicked
                                Toast.makeText(mainActivity.getApplicationContext(), "Number Deleting", Toast.LENGTH_LONG).show();
                                dbHelper.deleteASendTo(position);
                                Cursor newCursor = dbHelper.getAllSendToList();
                                changeCursor(newCursor);
                                notifyDataSetChanged();
                                Toast.makeText(mainActivity.getApplicationContext(), "Number Deleted", Toast.LENGTH_LONG).show();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE: // No button clicked // do nothing
                                Toast.makeText(mainActivity.getApplicationContext(), "No Action Done", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                builder.setMessage("Are you sure? Report will no longer sent to this Number " + mrMobileNumber).setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
            }
        });
    }
}
