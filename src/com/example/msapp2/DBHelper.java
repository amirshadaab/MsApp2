package com.example.msapp2;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/*
 * BPPVComment#34: provides SQLite DB support. Tables: reminders, workouts, incidents, 
 code based on: http://stackoverflow.com/questions/3366802/android-database-recreates-everytime-application-is-launched
 helpful http://www.vogella.com/articles/AndroidSQLite/article.html
 */
public class DBHelper {

	// BPPVComment#35: this is important. if an application extends SQLiteOpenHelper
	// a DB is automatically created for it. Pass on DB name to be created. 
	// DATABASE_VERSION is important -- 
	//	first time, there is no database, so onCreate is called
	// next time, if we upgrade the DB, bump the DATABASE_VERSION and onUpgrade will be called
	// 	--- here we can migrate data. 
	private static class OpenHelper extends SQLiteOpenHelper {

		OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		/*
		 * if new tables are added or DB changed: bump the DATABASE_VERSION
		 * and implement DB migration logic in onUpgrade  
		 *
		@Override
		public void onOpen(SQLiteDatabase db) {
			for (String s : TABLES) {
				db.execSQL("DROP TABLE IF EXISTS " + s);
			}
			db.execSQL(DBHelper.CREATE_REMINDERS_TABLE);
			db.execSQL(DBHelper.CREATE_WORKOUTS_TABLE);
			db.execSQL(DBHelper.CREATE_INCIDENTS_TABLE);
		} */

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i("OpenHelper::onCreate()", "Enter");
			db.execSQL(DBHelper.CREATE_REMINDERS_TABLE);
			db.execSQL(DBHelper.CREATE_WORKOUTS_TABLE);
			db.execSQL(DBHelper.CREATE_INCIDENTS_TABLE);
			db.execSQL(DBHelper.CREATE_PROFILE_TABLE);
			Log.i("OpenHelper::onCreate()", "Exit");
		}/**/

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("Example",
					"Upgrading database, this will drop tables and recreate.");
			for (String s : TABLES) {
				db.execSQL("DROP TABLE IF EXISTS " + s);
			}
			onCreate(db);
		}
	}
	
	private static final String DATABASE_NAME = "bppvTracker.db";
	private static final int DATABASE_VERSION = 3;

	// table for reminders
	public static final String TABLE_REMINDERS = "reminders";
	public static final String REMINDER_DATETIME = "dateTime";
	private static final String CREATE_REMINDERS_TABLE = " create table "
			+ TABLE_REMINDERS + " ( " + REMINDER_DATETIME
			+ " TEXT primary key); ";

	// table for exercise history
	public static final String TABLE_WORKOUTS = "workouts";
	public static final String WORKOUT_DATETIME_RESULT = "dateTimeResult";
	private static final String CREATE_WORKOUTS_TABLE = " create table "
			+ TABLE_WORKOUTS + " ( " + WORKOUT_DATETIME_RESULT
			+ " TEXT primary key ); ";

	// table for incidents
	public static final String TABLE_INCIDENTS = "incidents";
	public static final String INCIDENT_DATETIME = "dateTime";
	public static final String INCIDENT_DURATION = "duration";
	private static final String CREATE_INCIDENTS_TABLE = " create table "
			+ TABLE_INCIDENTS + " ( " + INCIDENT_DATETIME
			+ " TEXT primary key); ";

	// table for profile 
	public static final String TABLE_PROFILE = "profile";
	public static final String PROFILE_EMAIL = "email";
	private static final String CREATE_PROFILE_TABLE = " create table "
			+ TABLE_PROFILE + " ( " + PROFILE_EMAIL
			+ " TEXT primary key); ";

	
	private static final String[] TABLES = new String[] { TABLE_REMINDERS,
			TABLE_WORKOUTS, TABLE_INCIDENTS, TABLE_PROFILE};

	private Context context;
	private OpenHelper openHelper;

	public DBHelper(Context context) {
		//context.deleteDatabase(DATABASE_NAME);
		this.context = context;
		this.openHelper = new OpenHelper(this.context);
	}

	/* Methods for Exec History */
	public void addExercise(String dateTime) {

		String add = "insert into " + DBHelper.TABLE_WORKOUTS + " ( "
				+ DBHelper.WORKOUT_DATETIME_RESULT + " ) " + " values( '"
				+ dateTime + "' );";
		openHelper.getWritableDatabase().execSQL(add);
	}

	public void deleteExercise(String dateTime) {

		// usual delete query 
		String add = "delete from " + DBHelper.TABLE_WORKOUTS + " where "
				+ DBHelper.WORKOUT_DATETIME_RESULT + " = " + "'" + dateTime
				+ "' ;";
		openHelper.getWritableDatabase().execSQL(add);
	}

	public void clearWorkouts() {
		String sql = "delete * from " + DBHelper.TABLE_WORKOUTS + ";";
		Log.i("DBWrapper::clearExerciseHistory()", "Executing sql: " + sql);
		openHelper.getWritableDatabase().execSQL(sql);
	}

	public ArrayList<String> getWorkouts() {

		ArrayList<String> exercHistory = new ArrayList<String>();
		// cursor is like recordset: query -> get record set -> iterate 
		Cursor cursor = openHelper.getWritableDatabase().query(
				DBHelper.TABLE_WORKOUTS, null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				String his = cursor.getString(0);
				Log.i("DBWrapper::getExerciseHistory()",
						"Got an exercise history - " + his);
				exercHistory.add(his);
			} while (cursor.moveToNext());
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return exercHistory;
	}

	/* Methods for Reminders */
	public void addReminder(String reminder) {
		String add = "insert into " + DBHelper.TABLE_REMINDERS + " ( "
				+ DBHelper.REMINDER_DATETIME + ")" + " values( '" + reminder
				+ "' );";
		openHelper.getWritableDatabase().execSQL(add);
	}

	public void deleteReminder(String reminder) {
		String add = "delete from " + DBHelper.TABLE_REMINDERS + " where "
				+ DBHelper.REMINDER_DATETIME + " = " + " '" + reminder + "';";
		openHelper.getWritableDatabase().execSQL(add);
	}

	public void clearReminders() {
		String sql = "delete * from " + DBHelper.TABLE_REMINDERS + ";";
		Log.i("DBWrapper::clearReminders()", "Executing sql: " + sql);
		openHelper.getWritableDatabase().execSQL(sql);
	}

	public ArrayList<String> getReminders() {

		ArrayList<String> reminders = new ArrayList<String>();
		Cursor cursor = openHelper.getWritableDatabase().query(
				DBHelper.TABLE_REMINDERS, null, null, null, null, null, null);
		Log.i("DBWrapper::getReminders()", "move to first1");
		if (cursor.moveToFirst()) {
			Log.i("DBWrapper::getReminders()", "move to first");
			do {
				String rem = cursor.getString(0);
				Log.i("DBWrapper::getReminders()", "Got a reminder - " + rem);
				reminders.add(rem);
			} while (cursor.moveToNext());
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return reminders;
	}

	/* Methods for Incidents */
	public void addIncident(String incident) {
		String add = "insert into " + DBHelper.TABLE_INCIDENTS + " ( "
				+ DBHelper.INCIDENT_DATETIME + ")" + " values( '" + incident
				+ "' );";
		openHelper.getWritableDatabase().execSQL(add);
	}

	public void deleteIncident(String incident) {
		String add = "delete from " + DBHelper.TABLE_INCIDENTS + " where "
				+ DBHelper.INCIDENT_DATETIME + " = " + " '" + incident + "';";
		openHelper.getWritableDatabase().execSQL(add);
	}

	public void clearIncidents() {
		String sql = "delete * from " + DBHelper.TABLE_INCIDENTS + ";";
		Log.i("DBWrapper::clearIncidents()", "Executing sql: " + sql);
		openHelper.getWritableDatabase().execSQL(sql);
	}

	public ArrayList<String> getIncidents() {

		ArrayList<String> ins = new ArrayList<String>();
		Cursor cursor = openHelper.getWritableDatabase().query(
				DBHelper.TABLE_INCIDENTS, null, null, null, null, null, null);
		Log.i("DBWrapper::getIncidents()", "move to first1");
		if (cursor.moveToFirst()) {
			Log.i("DBWrapper::getIncidents()", "move to first");
			do {
				String in = cursor.getString(0);
				Log.i("DBWrapper::getIncidents()", "Got an incident - " + in);
				ins.add(in);
			} while (cursor.moveToNext());
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return ins;
	}

	// profile data 
	public void addEmail(String email) {

		String add = "insert into " + DBHelper.TABLE_PROFILE + " ( "
				+ DBHelper.PROFILE_EMAIL + " ) " + " values( '"
				+ email + "' );";
		openHelper.getWritableDatabase().execSQL(add);
	}

	public void deleteEmail(String email) {

		// usual delete query 
		String add = "delete from " + DBHelper.TABLE_PROFILE + " where "
				+ DBHelper.PROFILE_EMAIL + " = " + "'" + email
				+ "' ;";
		openHelper.getWritableDatabase().execSQL(add);
	}

	public String getEmail() {

		Cursor cursor = openHelper.getWritableDatabase().query(
				DBHelper.TABLE_PROFILE, null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				String in = cursor.getString(0);
				Log.i("DBWrapper::getEmail()", "Got email - " + in);
				return in; 
			} while (cursor.moveToNext());
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return null;
	}
	

	/* methods for storing user preferences */
	public void setWelcomeView(boolean b) {
		// TODO Auto-generated method stub
		
	}

	
}