package lnvappz.com.leadtracker.LeadDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dodda Venkata on 9/23/15.
 */

public class DBHelper {
    public DBMain dbInstance;
    public SQLiteDatabase writableDB,readableDB;

    // Logcat tag
    public static final String LOG = "DBMain";

    public DBHelper(Context context){
        dbInstance = DBMain.getInstance(context);
        writableDB = dbInstance.getWritableDatabase();
        readableDB = dbInstance.getReadableDatabase();
    }

    // Create a lead
    public long createLead(ContentValues values) {
        return writableDB.insertOrThrow(DBMain.TABLE_LEAD, null, values);
    }

    // Create a lead
    public long createTeleCaller(String teleCallerNewName) {
        ContentValues values = new ContentValues();
        values.put(DBMain.TELECALLER, teleCallerNewName);
        return writableDB.insertOrThrow(DBMain.TABLE_TELE_CALLER, null, values);
    }

    public void updateLead(ContentValues values,String id){
        writableDB.update(DBMain.TABLE_LEAD, values, DBMain.KEY_ID + "=" + id, null);
    }

    public void updateTelecallerName(String teleCallerNewName){
        ContentValues values = new ContentValues();
        values.put(DBMain.TELECALLER,teleCallerNewName);
        writableDB.update(DBMain.TABLE_TELE_CALLER, values, DBMain.KEY_ID + "=" + "1", null);
    }

    // Create a lead
    public long createAgent(ContentValues values) {
        return writableDB.insertOrThrow(DBMain.TABLE_FIELD_AGENT, null, values);
    }

    // Create a send to
    public long createSendTo(ContentValues values) {
        return writableDB.insertOrThrow(DBMain.TABLE_SENT_TO_LIST, null, values);
    }

    // Get all leads
    public Cursor getAllLeads() {
        String query = "SELECT * FROM " + DBMain.TABLE_LEAD+ " order by "+DBMain.STATUS+" desc, "+DBMain.DATE_UPDATED+" desc";
        Log.e(LOG, query);
        Cursor c = writableDB.rawQuery(query, null);
        return c;
    }

    // Get all agents
    public Cursor getAllAgents() {
        String query = "SELECT * FROM " + DBMain.TABLE_FIELD_AGENT;
        Log.e(LOG, query);
        Cursor c = readableDB.rawQuery(query, null);
        return c;
    }

    // Get all agents with pincode
    public Cursor getAllAgentsWithPincode() {
        String query = "SELECT * FROM " + DBMain.TABLE_FIELD_AGENT+" where "+DBMain.PINCODE+" is not null";
        Log.e(LOG, query);
        Cursor c = readableDB.rawQuery(query, null);
        return c;
    }

    // Get all reports
    public Cursor getAllSendToList() {
        String query = "SELECT * FROM " + DBMain.TABLE_SENT_TO_LIST;
        Log.e(LOG, query);
        Cursor c = readableDB.rawQuery(query, null);
        return c;
    }

    // Get all tele caller names
    public Cursor getAllTeleCallers() {
        String query = "SELECT * FROM " + DBMain.TABLE_TELE_CALLER;
        Log.e(LOG, query);
        Cursor c = readableDB.rawQuery(query, null);
        return c;
    }

    // delete a send to number
    public void deleteASendTo(int id){
        String whereClause = DBMain.KEY_ID+"=?";
        String[] whereArgs = new String[] { String.valueOf(id) };
        writableDB.delete(DBMain.TABLE_SENT_TO_LIST, whereClause, whereArgs);
    }

    // delete agent
    public void deleteAgent(int id){
        String whereClause = DBMain.KEY_ID+"=?";
        String[] whereArgs = new String[] { String.valueOf(id) };
        writableDB.delete(DBMain.TABLE_FIELD_AGENT, whereClause, whereArgs);
    }

    // delete agent
    public void deleteLead(int id){
        String whereClause = DBMain.KEY_ID+"=?";
        String[] whereArgs = new String[] { String.valueOf(id) };
        writableDB.delete(DBMain.TABLE_LEAD, whereClause, whereArgs);
    }

    // get lead details
    public Cursor getLeadDetails(int id){
        String[] selectedColumns = new String[] { DBMain.KEY_ID, DBMain.LEAD_NAME, DBMain.LEAD_AREA, DBMain.LEAD_TYPE,DBMain.PINCODE,DBMain.STATUS,DBMain.AGENT_ID,DBMain.MOBILE_NUMBER };
        String whereClause = DBMain.KEY_ID+"=?";
        String[] whereArgs = new String[] { String.valueOf(id) };
        Cursor c = readableDB.query(DBMain.TABLE_LEAD, selectedColumns, whereClause, whereArgs, null, null, null, null);
        if(c!=null){
            c.moveToFirst();
            return c;
        }
        return null;
    }

    public ArrayList<String> getAgentMobileNumbersBasedOnPinCode(String pincode){
        ArrayList<String> agentsList = new ArrayList<String>();
        String[] selectedColumns = new String[] { DBMain.MOBILE_NUMBER };
        String whereClause = DBMain.PINCODE+"=? or "+DBMain.PINCODE+" is null";
        String[] whereArgs = new String[] { pincode };
        Cursor c = readableDB.query(DBMain.TABLE_FIELD_AGENT, selectedColumns, whereClause, whereArgs, null, null, null, null);
        if(c.getCount() == 1){
            String query = "SELECT "+DBMain.MOBILE_NUMBER+" FROM " + DBMain.TABLE_FIELD_AGENT;
            Log.e(LOG, query);
            c = readableDB.rawQuery(query, null);
        }
        c.moveToPosition(-1);
        while(c.moveToNext()){
            String mobile = c.getString(c.getColumnIndexOrThrow(DBMain.MOBILE_NUMBER));
            if(! mobile.trim().equals(DBMain.WITH_OUT_AGENT)){
                agentsList.add(mobile);
            }
        }
        agentsList.add(DBMain.WITH_OUT_AGENT);
        return agentsList;
    }

    public String getAgentMobileNumbersBasedOnId(String id){
        String agentMobileNumber = null;
        String[] selectedColumns = new String[] { DBMain.MOBILE_NUMBER };
        String whereClause = DBMain.KEY_ID+"=?";
        String[] whereArgs = new String[] { id };
        Cursor c = readableDB.query(DBMain.TABLE_FIELD_AGENT, selectedColumns, whereClause, whereArgs, null, null, null, null);
        if(c!=null){
            c.moveToFirst();
            agentMobileNumber = c.getString(c.getColumnIndexOrThrow(DBMain.MOBILE_NUMBER));
            return agentMobileNumber;
        }
        return agentMobileNumber;
    }

    public String getAgentBasedOnMobileNumber(String mobileNumber){
        String[] selectedColumns = new String[] { DBMain.KEY_ID };
        String whereClause = DBMain.MOBILE_NUMBER+"=?";
        String[] whereArgs = new String[] { mobileNumber };
        Cursor c = readableDB.query(DBMain.TABLE_FIELD_AGENT, selectedColumns, whereClause, whereArgs, null, null, null, null);
        if(c!=null){
            c.moveToFirst();
            String agentId = c.getString(c.getColumnIndexOrThrow(DBMain.KEY_ID));
            return agentId;
        }
        return  null;
    }

    public boolean isSendToListHasThisNumber(String mobileNumber){
        String[] selectedColumns = new String[] { DBMain.KEY_ID };
        String whereClause = DBMain.MOBILE_NUMBER+"=?";
        String[] whereArgs = new String[] { mobileNumber };
        Cursor c = readableDB.query(DBMain.TABLE_SENT_TO_LIST, selectedColumns, whereClause, whereArgs, null, null, null, null);
        int count = c.getCount();
        if(count==0){
            return false;
        }
        return true;
    }

    public boolean isLeadListHasThisNumber(String mobileNumber){
        String[] selectedColumns = new String[] { DBMain.KEY_ID };
        String whereClause = DBMain.MOBILE_NUMBER+"=?";
        String[] whereArgs = new String[] { mobileNumber };
        Cursor c = readableDB.query(DBMain.TABLE_LEAD, selectedColumns, whereClause, whereArgs, null, null, null, null);
        int count = c.getCount();
        if(count==0){
            return false;
        }
        return true;
    }

    public boolean isAgentListHasThisNumber(String mobileNumber){
        String[] selectedColumns = new String[] { DBMain.KEY_ID };
        String whereClause = DBMain.MOBILE_NUMBER+"=?";
        String[] whereArgs = new String[] { mobileNumber };
        Cursor c = readableDB.query(DBMain.TABLE_FIELD_AGENT, selectedColumns, whereClause, whereArgs, null, null, null, null);
        int count = c.getCount();
        if(count==0){
            return false;
        }
        return true;
    }

    public void updateLeadStatus(ContentValues values,String id){
        writableDB.update(DBMain.TABLE_LEAD, values, DBMain.MOBILE_NUMBER + " like " + id, null);
    }

    public Cursor getAllAgentMobiles() {
        Cursor c = readableDB.query(true, DBMain.TABLE_FIELD_AGENT, new String[] { DBMain.MOBILE_NUMBER }, null, null, null, null, null, null);
        return c;
    }

    public Cursor getAllLeadsWithCBStatus(){
        String sqlQuery = "select * from "+DBMain.TABLE_LEAD+" where status='"+DBMain.STATUS_CALL_BACK;
        Cursor c = readableDB.rawQuery(sqlQuery, null);
        return c;
    }

    public Cursor getAllLeadsWithCBStatusAndAgentNumber(){
        String my_query = "select a.* , b."+DBMain.MOBILE_NUMBER+
                " as AgentMobileNumber from "+
                DBMain.TABLE_LEAD+" a INNER JOIN "+DBMain.TABLE_FIELD_AGENT+
                " b ON a."+DBMain.AGENT_ID+"=b."+DBMain.KEY_ID+" where a."+DBMain.STATUS+"="+DBMain.STATUS_CALL_BACK;

        Cursor c = readableDB.rawQuery(my_query, null);
        return  c;
    }
}
