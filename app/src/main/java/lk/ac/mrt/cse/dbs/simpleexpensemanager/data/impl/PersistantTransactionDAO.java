package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.text.ParseException;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database.DbHandler;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistantTransactionDAO implements TransactionDAO {
    private DbHandler dbHandler;
    private DateFormat dateFormat;
    public PersistantTransactionDAO(DbHandler dbHandler) {
        this.dbHandler = dbHandler;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db= dbHandler.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DbHandler.DATE, this.dateFormat.format(date));
        contentValues.put(DbHandler.ACC_NUM, accountNo);
        contentValues.put(DbHandler.EXPENSE_TYPE, String.valueOf(expenseType));
        contentValues.put(DbHandler.AMOUNT, amount);

        //insert new row to Trans table
        db.insert(DbHandler.TABLE2_NAME, null, contentValues);

    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        String query="SELECT * FROM " + DbHandler.TABLE2_NAME + " ORDER BY " + DbHandler.DATE+ " DESC ";

        Cursor cursor = db.rawQuery(query,null);

        ArrayList<Transaction> transactions = new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                // Create new Transaction object
                Transaction transaction= new Transaction();
                try {
                    transaction.setDate(this.dateFormat.parse(cursor.getString(0)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                transaction.setAccountNo(cursor.getString(1));
                transaction.setExpenseType(ExpenseType.valueOf(cursor.getString(2).toUpperCase()));
                transaction.setAmount(cursor.getDouble(3));


                transactions.add(transaction);
            }while (cursor.moveToNext());
        }

        cursor.close();
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {

        SQLiteDatabase db = dbHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DbHandler.TABLE2_NAME + " ORDER BY " + DbHandler.DATE + " DESC " +
                        " LIMIT ?;"
                , new String[]{Integer.toString(limit)}
        );

        ArrayList<Transaction> transactions = new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                // Create new Transaction object
                Transaction transaction= new Transaction();
                try {
                    transaction.setDate(this.dateFormat.parse(cursor.getString(0)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                transaction.setAccountNo(cursor.getString(1));
                transaction.setExpenseType(ExpenseType.valueOf(cursor.getString(2).toUpperCase()));
                transaction.setAmount(cursor.getDouble(3));


                transactions.add(transaction);
            }while (cursor.moveToNext());
        }

        cursor.close();
        return transactions;
    }
}
