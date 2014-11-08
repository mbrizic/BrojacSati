package com.example.brojacsati;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


@SuppressLint("SimpleDateFormat")
public class MainActivity extends ActionBarActivity implements OnSharedPreferenceChangeListener {
	BazaHelper bazaHelper;
	SQLiteDatabase baza;
	Cursor cursor;
	String MjesecId;
	SharedPreferences prefs;
	
	EditText txt1;
	EditText txt2;
	EditText txt3;
	EditText txt4;
	
	TextView iznos;	
	
	static double[] sati;
	static int[] satnice;
	static double[] podaci;
	static String valuta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);        
        
        bazaHelper = new BazaHelper(this);
        
        //UNIŠTAVANJE BAZE
        //bazaHelper.Destroy(bazaHelper.getWritableDatabase());
                
    	txt1 = (EditText) findViewById(R.id.txt1);
    	txt2 = (EditText) findViewById(R.id.txt2);
    	txt3 = (EditText) findViewById(R.id.txt3);
    	txt4 = (EditText) findViewById(R.id.txt4);    	
    	iznos = (TextView) findViewById(R.id.zarada);
    	
    	
    	prefs = PreferenceManager.getDefaultSharedPreferences(this); 
    	prefs.registerOnSharedPreferenceChangeListener(this);    	
    	
    	//onResume
    	
    }


    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		satnice = new int[]{
    			Integer.valueOf(prefs.getString("redovni", "20")),
    			Integer.valueOf(prefs.getString("nocni", "25")),
    			Integer.valueOf(prefs.getString("nedjelja", "30")),
    			Integer.valueOf(prefs.getString("blagdan", "40"))
    			};		//metoda getInt() iz nekog razloga ne radi kako bi trebala pa je ovo najelegantnije rješenje
		
		valuta = " " + prefs.getString("valuta", "kn");
		
		MjesecId = OdrediId();		
		
		podaci = Podaci.DohvatiSveZaMjesec(this, MjesecId);
    	iznos.setText(Izracunaj(podaci));
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        
        switch(id){
        	case R.id.action_settings:
        		startActivity(new Intent(this, PrefsActivity.class));
        		break;
        	case R.id.detaljno:
        		startActivity(new Intent(this, StatistikaActivity.class));
        		break;
        	case R.id.povijest:
        		startActivity(new Intent(this, LogActivity.class));
        		break;
        	case R.id.oAplikaciji:
        		startActivity(new Intent(this, AboutActivity.class));
        		break;        
        }
        
        
        return super.onOptionsItemSelected(item);
    }
    
    public void Dodaj(View v){
    	
    	sati = new double[]{Dodijeli(txt1), Dodijeli(txt2), Dodijeli(txt3), Dodijeli(txt4)};
    	
    	if(sati[0] == 0.0 && sati[1] == 0.0 &&  sati[2] == 0.0 &&  sati[3] == 0.0 ){
    		Toast.makeText(this, "Niste unijeli sate", Toast.LENGTH_LONG).show();
    		return;
    	}
    	
    	
    	
    	baza = bazaHelper.getWritableDatabase();
    	cursor = baza.query("sati", null, "mjesec_id == '" + MjesecId + "'", null, null, null, null);    	
    	
    	
    	if(cursor.getCount() == 0){ //DODAJ NOVI RED U TABLICU	
        	Podaci.Unesi(this, MjesecId, sati, true);        	
        	iznos.setText(Izracunaj(sati));		
    		
    	}else{ //INAÈE UPDATEAJ STARU    		
    		cursor.moveToFirst();
        	double[] nove = {cursor.getDouble(2) +	 sati[0], cursor.getDouble(3) + sati[1], cursor.getDouble(4) + sati[2], cursor.getDouble(5) + sati[3]}; 	
        	
        	Podaci.Unesi(this, MjesecId, nove, false);        	
        	iznos.setText(Izracunaj(nove));
    		
    	}
    	
    	Podaci.Logiraj(baza, sati);
    	PocistiUnos();
    	Toast.makeText(this, "Sati uspješno dodani.", Toast.LENGTH_SHORT).show();
    	
    	
    }
    
    public static double Dodijeli(EditText e){
    	String sadrzaj = e.getText().toString();
    	Double rez = 0.00;    	
    	
    	if(sadrzaj.length() != 0)
    		rez = Double.parseDouble(sadrzaj);
    	
    	rez = Math.round(rez*100.0)/100.0; 
    	Log.e("BROJ", rez.toString());
    	
    	return rez;
    }
    
    public static String Izracunaj(double[] niz){
    	double zbroj=0;
    	
    	for(int i=0; i<4; i++)
    		zbroj+= niz[i] * satnice[i];
    	
    	zbroj = Math.round(zbroj*100.0)/100.0;    	
    	return String.valueOf(zbroj) + valuta;
    }
    
    public static String IspisiNiz(double[] niz){
    	String a = "";
    	
    	for(double d : niz)
    		a += String.valueOf(d);
    	
    	return a;
    }
    
    public String OdrediId(){
    	DateFormat df = new SimpleDateFormat("MMyy");
    	String a = df.format(Calendar.getInstance().getTime());    
    	Log.e("ID mjeseca odreðen", a);
    	return a;
    }
        
    public void PocistiUnos(){
    	txt1.setText("");
    	txt2.setText("");
    	txt3.setText("");
    	txt4.setText("");
    	
    }
    
    public void Statistika(View v){
    	startActivity(new Intent(this, StatistikaActivity.class));    	
    }
    
    public void OtvoriLogActivity(View v){
    	startActivity(new Intent(this, LogActivity.class));  
    }
    
    public void Pomoc(View v){
    	Intent intent = new Intent(this, AboutActivity.class);
    	startActivity(intent);
    }


	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
}
