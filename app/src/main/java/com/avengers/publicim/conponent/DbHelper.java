package com.avengers.publicim.conponent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.avengers.publicim.data.entities.Chat;
import com.avengers.publicim.data.entities.Contact;
import com.avengers.publicim.data.entities.Group;
import com.avengers.publicim.data.entities.Member;
import com.avengers.publicim.data.entities.Message;
import com.avengers.publicim.data.entities.RosterEntry;
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

	public static class IntBoolean {
		public static final int TRUE = 1;
		public static final int FALSE = 0;
	}

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
		db.execSQL(RosterEntry.CREATE_SQL);
		db.execSQL(Chat.CREATE_SQL);
		db.execSQL(Message.CREATE_SQL);
		db.execSQL(Group.CREATE_SQL);
		db.execSQL(Member.CREATE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public List<RosterEntry> getLocalRoster(){
		SQLiteDatabase db = getWritableDatabase();
		String SQL_SEL = String.format("SELECT * FROM %s GROUP BY %s ORDER BY %s ",
				RosterEntry.TABLE_NAME, User.NAME, User.NAME);
		Cursor cursor = db.rawQuery(SQL_SEL, null);
		List<RosterEntry> roster = new ArrayList<>();
		while(cursor.moveToNext()){
			roster.add(RosterEntry.newInstance(cursor));
        }
		cursor.close();
		return roster;
	}

	public Cursor getContentOfChats(){
		SQLiteDatabase db = getWritableDatabase();
		String SQL_SEL = String.format("SELECT * FROM (SELECT *,MAX(msg.date) last,SUM(msg.read = 0)unread " +
				"FROM %s msg GROUP BY msg.chat_id)info, %s c " +
				"WHERE info.chat_id = c.cid ORDER BY info.last DESC",
				Message.TABLE_NAME, Chat.TABLE_NAME);
		return db.rawQuery(SQL_SEL, null);
	}

	public ArrayList<Message> getMessages(Contact contact){
		SQLiteDatabase db = getWritableDatabase();
		String SQL_SEL = String.format("SELECT * FROM %s WHERE %s = '%s' ORDER BY %s DESC",
				Message.TABLE_NAME, Message.CHAT_ID,
				contact instanceof RosterEntry ? contact.getName():contact.getId(), Message.DATE);
		Cursor cursor = db.rawQuery(SQL_SEL, null);
		ArrayList<Message> list = new ArrayList<>();
		cursor.moveToLast();
		if(!cursor.isBeforeFirst()){
			do {
				list.add(Message.newInstance(cursor));
			} while (cursor.moveToPrevious());
		}
		cursor.close();
		return list;
	}

	public ArrayList<Group> getGroups(){
		SQLiteDatabase db = getWritableDatabase();
		String SQL_SEL = String.format("SELECT * FROM %s GROUP BY %s ORDER BY %s ",
				Group.TABLE_NAME, Group.GID, Group.NAME);
		Cursor cursor = db.rawQuery(SQL_SEL, null);
		ArrayList<Group> list = new ArrayList<>();
		while(cursor.moveToNext()){
			list.add(Group.newInstance(cursor));
		}
		cursor.close();
		return list;
	}

	public ArrayList<Member> getMembers(Group group){
		SQLiteDatabase db = getWritableDatabase();
		String SQL_SEL = String.format("SELECT * FROM %s WHERE %s = '%s' ORDER BY %s ",
				Member.TABLE_NAME, Member.GROUP_ID, group.getGid(), Member.USER);
		Cursor cursor = db.rawQuery(SQL_SEL, null);
		ArrayList<Member> list = new ArrayList<>();
		while(cursor.moveToNext()){
			list.add(Member.newInstance(cursor));
		}
		cursor.close();
		return list;
	}

	public long insertRoster(RosterEntry entry){
		SQLiteDatabase db = getWritableDatabase();
		return db.insert(RosterEntry.TABLE_NAME, null, entry.getContentValues());
	}

	public long insertChat(Chat chat){
		SQLiteDatabase db = getWritableDatabase();
		return db.insert(Chat.TABLE_NAME, null, chat.getContentValues());
	}

	public void insertMessage(Message msg){
		SQLiteDatabase db = this.getWritableDatabase();
		db.insert(Message.TABLE_NAME, null, msg.getContentValues());
	}

	public void insertGroup(Group group){
		SQLiteDatabase db = this.getWritableDatabase();
		db.insert(Group.TABLE_NAME, null, group.getContentValues());
	}

	public void insertMember(Member member){
		SQLiteDatabase db = this.getWritableDatabase();
		db.insert(Member.TABLE_NAME, null, member.getContentValues());
	}

	public void updateRoster(RosterEntry entry){
		SQLiteDatabase db = getWritableDatabase();
		String whereSql = String.format("%s = '%s' AND %s = '%s'",
				User.NAME, entry.getUser().getName(), User.UID, entry.getUser().getUid());
		db.update(RosterEntry.TABLE_NAME, entry.getContentValues(), whereSql, null);
	}

	public void updateChat(Chat chat){
		SQLiteDatabase db = getWritableDatabase();
		String whereSql = String.format("%s = '%s'", Chat.CID, chat.getCid());
		db.update(Chat.TABLE_NAME, chat.getContentValues(), whereSql, null);
	}

	public void updateMessage(Message message){
		SQLiteDatabase db = getWritableDatabase();
		String whereSql = String.format("%s = '%s'", Message.MID, message.getMid());
		db.update(Message.TABLE_NAME, message.getContentValues(), whereSql, null);
	}

	public void updateMessageofRead(Chat chat, int read){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Message.READ, read);
		String whereSql = String.format("%s = '%s'", Message.CHAT_ID, chat.getCid());
		db.update(Message.TABLE_NAME, values, whereSql, null);
	}

	public void updateGroup(Group group){
		SQLiteDatabase db = getWritableDatabase();
		String whereSql = String.format("%s = '%s'", Group.GID, group.getGid());
		db.update(Group.TABLE_NAME, group.getContentValues(), whereSql, null);
	}

	public void updateMember(Member member){
		SQLiteDatabase db = getWritableDatabase();
		String whereSql = String.format("%s = '%s' AND %s = '%s'",
				Member.USER, member.getUser(), Member.GROUP_ID, member.getGroupId());
		db.update(Member.TABLE_NAME, member.getContentValues(), whereSql, null);
	}

	public void deleteRoster(RosterEntry entry){
		deleteRoster(entry.getName());
	}

	public void deleteRoster(String userName){
		SQLiteDatabase db = this.getReadableDatabase();
		String where = String.format("%s = '%s'", User.NAME, userName);
		db.delete(RosterEntry.TABLE_NAME, where, null);
	}

	public void deleteChat(String chatId){
		SQLiteDatabase db = this.getReadableDatabase();
		String where = String.format("%s = '%s'", Chat.CID, chatId);
		db.delete(Chat.TABLE_NAME, where, null);
	}

	/**
	 * delete a message by mid
	 * @param message
	 */
	public void deleteMessage(Message message){
		SQLiteDatabase db = this.getReadableDatabase();
		String where = String.format("%s = '%s'", Message.MID, message.getMid());
		db.delete(Message.TABLE_NAME, where, null);
	}

	/**
	 * delete messages of same chatId
	 * @param chatId
	 */
	public void deleteMessages(String chatId){
		SQLiteDatabase db = this.getReadableDatabase();
		String where = String.format("%s = '%s'", Message.CHAT_ID, chatId);
		db.delete(Message.TABLE_NAME, where, null);
	}

	public void deleteGroup(Group group){
		deleteGroup(group.getGid());
	}

	public void deleteGroup(String gid){
		SQLiteDatabase db = this.getReadableDatabase();
		String where = String.format("%s = '%s'", Group.GID, gid);
		db.delete(Group.TABLE_NAME, where, null);
	}

	public void deleteMember(Member member){
		SQLiteDatabase db = this.getReadableDatabase();
		String where = String.format("%s = '%s' AND %s = '%s'",
				Member.USER, member.getUser(), Member.GROUP_ID, member.getGroupId());
		db.delete(Member.TABLE_NAME, where, null);
	}

}
