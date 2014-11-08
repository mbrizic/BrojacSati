package com.example.brojacsati;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class BazaHelper extends SQLiteOpenHelper {
	static final int DB_VERSION = 2;
	static final String DB_NAME = "baza.db";
	
	static final String C_ID = BaseColumns._ID;
	Context context;
	
	
	
	public BazaHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}



	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table sati (" + C_ID + " integer primary key autoincrement, mjesec_id text, redovni float, nocni float, nedjelja float, blagdan float)";
		String sql2 = "create table unosi (" + C_ID + " integer primary key autoincrement, datum text, tekst text)";
		
		db.execSQL(sql);
		db.execSQL(sql2);
		
		Log.d("BAZA", "Uspješno napravljena");
		
	}
	
	public void Destroy(SQLiteDatabase db){
		db.execSQL("drop table if exists sati");
		onCreate(db);
	}



	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("drop table if exists sati");
		db.execSQL("drop table if exists unosi");
		Log.d("BAZA", "srušeno");
		onCreate(db);
		
	}

}
