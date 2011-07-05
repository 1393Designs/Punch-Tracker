package com.designatum_1393.punchtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import java.lang.Integer;

public class subsDbAdapter 
{

	public static final String KEY_NAME = "name"; 
	public static final String KEY_NUM_PUNCH = "punches"; 
	public static final String KEY_ROWID = "_id"; // row's id


	private static final String TAG = "Tracker: subsDbAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	private static final String DATABASE_CREATE =
		"create table tracker (_id integer primary key autoincrement, "
		+ "name text not null, punches text not null);";

	private static final String DATABASE_NAME = "data";
	private static final String DATABASE_TABLE = "tracker";
	private static final int DATABASE_VERSION = 1;

	private final Context mCtx;
	
	public subsDbAdapter(Context ctx)
	{
		this.mCtx = ctx;
		
	}

	private static class DatabaseHelper extends SQLiteOpenHelper
	{		
		DatabaseHelper(Context context) 
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			db.execSQL("DROP TABLE IF EXISTS tracker");
			onCreate(db);
		}
	}
	
	public subsDbAdapter open() throws SQLException
	{
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close()
	{
		mDbHelper.close();
	}

	public long createSub(String name)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_NUM_PUNCH, "0");
		
		if (mDb.query(DATABASE_TABLE, new String[] {KEY_NUM_PUNCH}, KEY_NAME +"=? and " +KEY_NUM_PUNCH +"=?", new String[] {name, "0"}, null, null, KEY_NAME).getCount() != 0)	
		{
			return -1;
		}
		else
			return mDb.insert(DATABASE_TABLE, null, initialValues);
	}

	public boolean deleteSub(String oldName, String oldNum)
	{
		String whereClause = KEY_NUM_PUNCH +"='" +oldNum.replace("'", "''") +"'" +" AND "
					+KEY_NAME +"='" +oldName.replace("'", "''") +"'";
	
		return mDb.delete(DATABASE_TABLE, whereClause, null) > 0;
	}
	
	public Cursor fetchAllSubs()
	{
		return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME,
			KEY_NUM_PUNCH}, null, null, null, null, KEY_NAME);
	}

	public Cursor fetchSub(long rowId) throws SQLException
	{
		Cursor mCursor =
			mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
					KEY_NAME, KEY_NUM_PUNCH}, KEY_ROWID + "=" + rowId, null,
					null, null, null, null);
		if (mCursor != null)
		{
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public boolean updateSub(String oldName, String oldNum, int choice)
	{
		String whereClause = KEY_NUM_PUNCH +"='" +oldNum.replace("'", "''") +"'" +" AND "
					+KEY_NAME +"='" +oldName.replace("'", "''") +"'";

		int newNum = 0;
		if(choice == 1)
			newNum = Integer.parseInt(oldNum) + 1;
		else if(Integer.parseInt(oldNum) > 0)
			newNum = Integer.parseInt(oldNum) - 1;
			
		ContentValues args = new ContentValues();
		args.put(KEY_NAME, oldName);
		args.put(KEY_NUM_PUNCH, Integer.toString(newNum));

		if (mDb.query(DATABASE_TABLE, new String[] {KEY_NUM_PUNCH}, KEY_NAME +"=? and " +KEY_NUM_PUNCH +"=?", new String[] {oldName, Integer.toString(newNum)}, null, null, KEY_NAME).getCount() != 0)
		{
			return false;
		}
		else
			return mDb.update(DATABASE_TABLE, args, whereClause, null) > 0;
	}
}
