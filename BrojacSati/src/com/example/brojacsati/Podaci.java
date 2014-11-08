package com.example.brojacsati;



import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Podaci {

	static BazaHelper bazaHelper;
	static SQLiteDatabase baza;
	static Cursor cursor;
	
	static String[] nazivi = {" redovni, ", " noæni, ", " nedjelja, ", " praznici, "};
	
	
	
	static double[] DohvatiSveZaMjesec(Context context, String mjesecId){
		bazaHelper = new BazaHelper(context);
		baza = bazaHelper.getWritableDatabase();
		
    	cursor = baza.query("sati", null , "mjesec_id == '" + mjesecId + "'", null, null, null, null);
    	Log.e("dohvaæanje", String.valueOf(cursor.getCount()));
    	
    	if(cursor.getCount() == 0){
    		return new double[]{0,0,0,0};
    	}else{
    		cursor.moveToFirst();
        	double[] nove = {cursor.getDouble(2), cursor.getDouble(3), 
        								cursor.getDouble(4),cursor.getDouble(5)};
        	return nove;
    	}
	}
	
	static void Unesi(Context context, String MjesecId, double[] vrijednosti, boolean noviRed){
		
		baza = bazaHelper.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.clear();
		
		Log.e("b", MainActivity.IspisiNiz(vrijednosti));
		
		cv.put("mjesec_id", MjesecId);
    	cv.put("redovni", vrijednosti[0]);
    	cv.put("nocni", vrijednosti[1]);
    	cv.put("nedjelja", vrijednosti[2]);
    	cv.put("blagdan", vrijednosti[3]);
    	
    	if(noviRed == true){
    		baza.insertOrThrow("sati", null, cv);
        	Log.e("BAZA", "Dodan novi red");    		
    	}
    	else{
    		baza.update("sati", cv, "mjesec_id == '" + MjesecId + "'", null);
    		Log.e("BAZA", "dodano na veæ postojeæi red");
    	}
    	
        	
    	baza.close();
    	
		
	}
	
	static void IzbrišiSvePodatkeIzTablice(String tablica){
		bazaHelper.getWritableDatabase().delete(tablica, null, null);
	}
	
	
	
	static void Logiraj(SQLiteDatabase baza, double[] niz){
		//NAPRAVI LOG
		
		String report = StvoriTekst(niz);
		if(report.length() != 0){
			ContentValues cv = new ContentValues();
	    	cv.clear();
	    	cv.put("datum", new SimpleDateFormat("dd. MM. yyyy. - HH:mm").format(new Date()));
	    	cv.put("tekst", StvoriTekst(niz));
	    	baza.insertOrThrow("unosi", null, cv);			
		}
		
		
	}
	
	static String StvoriTekst(double[] niz){		
		String izlaz = "";
		double broj = 0.0;
		
		for(int i=0;i<4; i++){
			broj = niz[i];
			if(broj != 0.0){
				
				if(broj % 1 == 0.0){
					String broj2 = Double.toString(broj); 
					broj2 = broj2.substring(0, broj2.indexOf('.'));
					izlaz += String.valueOf(broj2);
				}else{
					izlaz += String.valueOf(broj);
				}
				
				izlaz += "h" + nazivi[i];
			}			
		}
		
		
		if(izlaz.length() == 0){ //fix za nešto
			return "";
		}else{
			return izlaz.substring(0, izlaz.length()-2);
		}
		
		
	}
	
	
	
}
