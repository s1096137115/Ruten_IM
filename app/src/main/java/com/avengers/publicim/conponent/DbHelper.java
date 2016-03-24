package com.avengers.publicim.conponent;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.avengers.publicim.data.entities.ChatManager;
import com.avengers.publicim.data.entities.Message;
import com.avengers.publicim.data.entities.RosterEntry;
import com.avengers.publicim.data.entities.RosterManager;
import com.avengers.publicim.data.entities.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by D-IT-MAX2 on 2016/3/2.
 */
public class DbHelper extends SQLiteOpenHelper{
	final private static int _DB_VERSION = 1;
	final private static String _DB_DATABASE_NAME = "IMDatabase.db"; //todo "user name" + IMDatabase
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
		db.execSQL(RosterManager.CREATE_SQL);
		db.execSQL(ChatManager.CREATE_SQL);
		db.execSQL(Message.CREATE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public List<RosterEntry> getLocalRoster(){
		SQLiteDatabase db = getWritableDatabase();
		String SQL_SEL = String.format("SELECT * FROM %s ORDER BY %s DESC",
				RosterManager.TABLE_NAME, User.NAME);
		Cursor cursor = db.rawQuery(SQL_SEL, null);
		List<RosterEntry> roster = new ArrayList<>();
		while(cursor.moveToNext()){
			roster.add(RosterEntry.newInstance(cursor));
        }
		return roster;
	}

//	public ArrayList<Chat> getChats(){
//		SQLiteDatabase db = getWritableDatabase();
//		String SQL_SEL = String.format("SELECT * FROM %s WHERE %s = '%s' ORDER BY %s DESC",
//				ChatManager.TABLE_NAME, User.UID, rosterEntry.getUser().getUid(), Message.DATE);
//		Cursor cursor = db.rawQuery(SQL_SEL, null);
//		ArrayList<Chat> list = new ArrayList<>();
//        while(cursor.moveToNext()){
//            list.add(Message.newInstance(cursor));
//        }
//		return list;
//	}

	public ArrayList<Message> getMessages(RosterEntry rosterEntry){
		SQLiteDatabase db = getWritableDatabase();
		String SQL_SEL = String.format("SELECT * FROM %s WHERE %s = '%s' ORDER BY %s DESC",
				Message.TABLE_NAME, User.UID, rosterEntry.getUser().getUid(), Message.DATE);
		Cursor cursor = db.rawQuery(SQL_SEL, null);
		ArrayList<Message> list = new ArrayList<>();
//        while(cursor.moveToNext()){
//            list.add(Message.newInstance(cursor));
//        }
		cursor.moveToLast();
		if(!cursor.isBeforeFirst()){
			do {
				list.add(Message.newInstance(cursor));
			} while (cursor.moveToPrevious());
		}
		return list;
	}

	public void addChat(){

	}

	public void addMessage(Message msg){
		SQLiteDatabase db = this.getWritableDatabase();
		db.insert(Message.TABLE_NAME, null, msg.getContentValues());
	}


}
