package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database.DbHandler;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistantAccountDAO implements AccountDAO {
    private DbHandler dbHandler;

    public PersistantAccountDAO(DbHandler dbHandler) {
        this.dbHandler = dbHandler;
    }
    @Override
    public List<String> getAccountNumbersList() {
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        String query="SELECT " + DbHandler.ACC_NUM+ " FROM " + DbHandler.TABLE1_NAME;
        Cursor cursor = db.rawQuery(query,null);
        ArrayList<String> accountNumbers = new ArrayList<>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            accountNumbers.add(cursor.getString(0));
        }

        cursor.close();
        return accountNumbers;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accounts = new ArrayList<>();
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        String query = "SELECT * FROM "+DbHandler.TABLE1_NAME;

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
        cursor.close();
        return accounts;

    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DbHandler.TABLE1_NAME + " WHERE " + DbHandler.ACC_NUM + "=?;"
                , new String[]{accountNo});
        Account account;
        if (cursor != null && cursor.moveToFirst()) {
            account = new Account();
            account.setAccountNo(cursor.getString(0));
            account.setBankName(cursor.getString(1));
            account.setAccountHolderName(cursor.getString(2));
            account.setBalance(cursor.getDouble(3));

        } else {
            throw new InvalidAccountException("Account "+accountNo+" is Invalid");
        }
        cursor.close();
        return account;
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase sqLiteDatabase = dbHandler.getWritableDatabase();

        ContentValues contentValues=new ContentValues();

        contentValues.put(DbHandler.ACC_NUM,account.getAccountNo());
        contentValues.put(DbHandler.BANK_NAME,account.getBankName());
        contentValues.put(DbHandler.HOLDER,account.getAccountHolderName());
        contentValues.put(DbHandler.BALANCE,account.getBalance());

        //save to table
        sqLiteDatabase.insert(DbHandler.TABLE1_NAME,null,contentValues);
        // close database
        sqLiteDatabase.close();
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = dbHandler.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DbHandler.TABLE1_NAME + " WHERE " + DbHandler.ACC_NUM + "=?;"
                , new String[]{accountNo}
        );

        if (cursor.moveToFirst()) {
            db.delete(
                    DbHandler.TABLE1_NAME,
                    DbHandler.ACC_NUM + " = ?",
                    new String[]{accountNo}
            );
        } else {
            throw new InvalidAccountException("Account "+accountNo+" is Invalid");
        }
        cursor.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase db = dbHandler.getWritableDatabase();

        Account account = this.getAccount(accountNo);

        if (account != null) {

            switch (expenseType) {
                case EXPENSE:
                    account.setBalance(account.getBalance() - amount);
                    break;
                case INCOME:
                    account.setBalance(account.getBalance() + amount);
                    break;
            }

            db.execSQL(
                    "UPDATE " + DbHandler.TABLE1_NAME +
                            " SET " + DbHandler.BALANCE + " = ?" +
                            " WHERE " + DbHandler.ACC_NUM + " = ?",
                    new String[]{Double.toString(account.getBalance()), accountNo});

        } else {
            throw new InvalidAccountException("Account "+accountNo+" is Invalid");
        }

    }
}
