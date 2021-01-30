package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;

public class DbHandler extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DB_NAME = "180537C";
    private static final String TABLE1_NAME = "Account";

    // Column names
    private String ACC_NUM="accountNo";
    private String BANK_NAME="bankName";
    private String HOLDER="accountHolderName";
    private String BALANCE= "balance";


    public DbHandler(@Nullable Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String TABLE_CREATE_QUERY = "CREATE TABLE "+TABLE1_NAME+" " +
                "("
                +ACC_NUM+" TEXT PRIMARY KEY ,"
                +BANK_NAME + " TEXT,"
                +HOLDER + " TEXT,"
                +BALANCE+" DOUBLE" +
                ");";


        db.execSQL(TABLE_CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String DROP_TABLE_QUERY = "DROP TABLE IF EXISTS "+ TABLE1_NAME;
        // Drop older table if existed
        db.execSQL(DROP_TABLE_QUERY);
        // Create tables again
        onCreate(db);
    }

    // Add a single ACCOUNT
    public void addAccount(Account account){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues contentValues=new ContentValues();

        contentValues.put(ACC_NUM,account.getAccountNo());
        contentValues.put(BANK_NAME,account.getBankName());
        contentValues.put( HOLDER,account.getAccountHolderName());
        contentValues.put(BALANCE,account.getBalance());

        //save to table
        sqLiteDatabase.insert(TABLE1_NAME,null,contentValues);
        // close database
        sqLiteDatabase.close();
    }
    // Get all accounts into a list
    public List<Account> getAllAccounts(){

        List<Account> accounts = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM "+TABLE1_NAME;

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){
            do {
                // Create new Account object
                Account account = new Account();
                account.setAccountNo(cursor.getString(0));
                account.setBankName(cursor.getString(1));
                account.setAccountHolderName(cursor.getString(2));
                account.setBalance(cursor.getDouble(3));


                accounts.add(account);
            }while (cursor.moveToNext());
        }
        return accounts;
    }
}
