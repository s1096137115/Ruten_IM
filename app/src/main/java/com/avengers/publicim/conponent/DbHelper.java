package com.avengers.publicim.conponent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by D-IT-MAX2 on 2016/3/2.
 */
public class DbHelper extends SQLiteOpenHelper{
	final private static int _DB_VERSION = 1;
	final private static String _DB_DATABASE_NAME = "IMDatabase.db";
	private static DbHelper instance = null;

	public DbHelper(Context context) {
		super(context, _DB_DATABASE_NAME, null, _DB_VERSION);
	}

	public static synchronized DbHelper getInstance(Context context){
		if(instance == null){
			instance = new DbHelper(context);
		}
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}


}
