package lnvappz.com.leadtracker.LeadDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dodda Venkata on 9/23/15.
 */

public class DBMain extends SQLiteOpenHelper {
    private static DBMain dbInstance = null;

    // Logcat tag
    public static final String LOG = "DBMain";

    // DB Constants
    public static String DB_NAME = "LeadTracker.db";
    public static int DB_VERSION = 20;
    public static final String WITH_OUT_AGENT="NO AGENT";
    String[] status = {"C","NR","D","CB","F"};
    public static final int STATUS_CALL_BACK=5;
    public static final int STATUS_NO_RESPONSE=2;
    public static final int STATUS_DROPPED = 1;
    public static final int STATUS_CLOSED=3;
    public static final int STATUS_FIXED_APPOINTMENT=4;
    public static final boolean isDebug=false;

    // Table Names
    public static final String TABLE_FIELD_AGENT = "fieldagent";
    public static final String TABLE_LEAD = "lead";
    public static final String TABLE_SENT_TO_LIST = "send_to_list";
    public static final String TABLE_TELE_CALLER="telecaller";

    // Common column names
    public static final String KEY_ID = "_id";
    public static final String PINCODE = "pinCode";
    public static final String MOBILE_NUMBER = "mobileNumber";

    // Lead Table - column names
    public static final String AGENT_ID = "agentId";
    public static final String LEAD_NAME = "leadName";
    public static final String LEAD_AREA = "leadArea";
    public static final String LEAD_TYPE = "leadType";
    public static final String DATE_ADDED = "dateAdded";
    public static final String DATE_UPDATED = "dateUpdated";
    public static final String STATUS = "status";

    // Field Agent Table - column names

    // SEND_TO_LIST Table - column names are repeated so not mentioning seperately

    // Tele caller table - column names
    public static final String TELECALLER = "teleCallerName";

    // Constants for creating sql statements
    public static final String TEXT_TYPE = " TEXT";
    public static final String COMMA_SEP = ",";
    public static final String INTEGER_TYPE = " INTEGER";
    public static final String INTEGER_PRIMARY_TYPE = " INTEGER PRIMARY KEY";

    // Table Create Statements
    // LEAD table create statement
    public static final String CREATE_TABLE_LEAD = "CREATE TABLE "
            + TABLE_LEAD + "(" + KEY_ID + INTEGER_PRIMARY_TYPE + COMMA_SEP +
            AGENT_ID + INTEGER_TYPE + COMMA_SEP + LEAD_NAME + TEXT_TYPE + COMMA_SEP + LEAD_AREA +
            TEXT_TYPE + COMMA_SEP + LEAD_TYPE + TEXT_TYPE + COMMA_SEP + PINCODE + TEXT_TYPE +
            COMMA_SEP + MOBILE_NUMBER + TEXT_TYPE + COMMA_SEP + DATE_ADDED + INTEGER_TYPE + COMMA_SEP +
            DATE_UPDATED + INTEGER_TYPE + COMMA_SEP + STATUS + INTEGER_TYPE + COMMA_SEP+"FOREIGN KEY("+
            AGENT_ID + ") REFERENCES "+ TABLE_FIELD_AGENT + "(" + KEY_ID + ")"+")";

    // FIELD AGENT table create statement
    public static final String CREATE_TABLE_FIELD_AGENT = "CREATE TABLE " + TABLE_FIELD_AGENT +
            "(" + KEY_ID + INTEGER_PRIMARY_TYPE + COMMA_SEP +
            MOBILE_NUMBER + TEXT_TYPE + COMMA_SEP + PINCODE + TEXT_TYPE + ")";

    // SEND TO LIST table create statement
    public static final String CREATE_TABLE_SEND_TO_LIST = "CREATE TABLE " + TABLE_SENT_TO_LIST +
            "(" + KEY_ID + INTEGER_PRIMARY_TYPE + COMMA_SEP + MOBILE_NUMBER + TEXT_TYPE + ")";

    // tele caller table create statement
    public static final String CREATE_TABLE_TELE_CALLER = "CREATE TABLE " + TABLE_TELE_CALLER +
            "(" + KEY_ID + INTEGER_PRIMARY_TYPE + COMMA_SEP + TELECALLER + TEXT_TYPE + ")";

    private DBMain(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    public static synchronized DBMain getInstance(Context context) {
        if (dbInstance == null) {
            dbInstance = new DBMain(context.getApplicationContext());
        }
        return dbInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_LEAD);
        db.execSQL(CREATE_TABLE_FIELD_AGENT);
        db.execSQL(CREATE_TABLE_SEND_TO_LIST);
        db.execSQL(CREATE_TABLE_TELE_CALLER);
        onCreateInsertSomeStaticData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEAD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FIELD_AGENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENT_TO_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TELE_CALLER);

        // create new tables
        onCreate(db);

    }

    public void onCreateInsertSomeStaticData(SQLiteDatabase db){
        String sql = "INSERT INTO "+DBMain.TABLE_FIELD_AGENT+"("+DBMain.KEY_ID+DBMain.COMMA_SEP+
                DBMain.MOBILE_NUMBER+ COMMA_SEP+DBMain.PINCODE+")" + "VALUES ( 0,'"+DBMain.WITH_OUT_AGENT+"', null)";
        db.execSQL(sql);
    }
}
