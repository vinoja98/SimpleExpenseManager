package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;


public class DbHandler extends SQLiteOpenHelper {
    public static final int VERSION = 8;
    public static final String DB_NAME = "180537C";
    public static final String TABLE1_NAME = "Account";
    public static final String TABLE2_NAME = "Trans";

    // Column names of Account table
    public static final String ACC_NUM="accountNo";
    public static final String BANK_NAME="bankName";
    public static final String HOLDER="accountHolderName";
    public static final String BALANCE= "balance";

    //Column names of Trans table
    public static final String DATE="date";
    public static final String EXPENSE_TYPE="expenseType";
    public static final String AMOUNT="amount";


    public DbHandler(@Nullable Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String TABLE_CREATE_QUERY = "CREATE TABLE "+TABLE1_NAME+" " +
                "("
                +ACC_NUM+" TEXT PRIMARY KEY, "
                +BANK_NAME + " TEXT NOT NULL, "
                +HOLDER + " TEXT NOT NULL, "
                +BALANCE+" DOUBLE NOT NULL CHECK ("+ BALANCE+" > 0)"+
                ");";


        db.execSQL(TABLE_CREATE_QUERY);
        String TABLE_CREATE_QUERY2 = "CREATE TABLE "+TABLE2_NAME+" " +
                "("
                +DATE + " TEXT NOT NULL, "
                +ACC_NUM+" TEXT NOT NULL, "
                +EXPENSE_TYPE + " TEXT NOT NULL, "
                +AMOUNT+" DOUBLE NOT NULL CHECK ("+ AMOUNT+" > 0),"
                + " FOREIGN KEY ("+ACC_NUM+") REFERENCES "+TABLE1_NAME+"("+ACC_NUM+")ON DELETE CASCADE\n" +
                "ON UPDATE CASCADE);";
        db.execSQL(TABLE_CREATE_QUERY2);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String DROP_TABLE_QUERY = "DROP TABLE IF EXISTS "+ TABLE1_NAME;
        // Drop older table if existed
        db.execSQL(DROP_TABLE_QUERY);
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE2_NAME + "'");
        // Create tables again
        onCreate(db);
    }


}
