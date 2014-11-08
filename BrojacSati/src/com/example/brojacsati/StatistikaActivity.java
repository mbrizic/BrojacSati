package com.example.brojacsati;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class StatistikaActivity extends ActionBarActivity {
	
	BazaHelper bazaHelper;
	SQLiteDatabase baza;
	Cursor kursor;
	SimpleCursorAdapter adapter;
	ListView lv;
	static final String[] FROM = {"mjesec_id", "redovni", "nocni", "nedjelja", "blagdan"};
	static final int[] TO = {R.id.mjesec, R.id.tRedovni, R.id.tNocni, R.id.tNedjelja, R.id.tBlagdan};
	

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistika);
		
		lv = (ListView) findViewById(R.id.listView1);
		bazaHelper = new BazaHelper(this);
        baza = bazaHelper.getReadableDatabase();
        kursor = baza.query("sati", null, null, null, null, null, "mjesec_id");
        
        
        adapter = new StatsAdapter(this, R.layout.red, kursor, FROM, TO, 0);
        
        lv.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.statistika, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			startActivity(new Intent(this, PrefsActivity.class));
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	protected class StatsAdapter extends SimpleCursorAdapter{

		public StatsAdapter(Context context, int layout, Cursor c, String[] FROM, int[] TO, int flags) {
			super(context, layout, c, FROM, TO, flags);		
			
		}
		
		@Override //premošæujemo metodu da bismo mogli ispisati mjeseènu zaradu
		public void bindView(View red, Context context, Cursor c){
			super.bindView(red, context, c);
			
			SimpleDateFormat df1 = new SimpleDateFormat("MMyy");
			SimpleDateFormat df2 = new SimpleDateFormat("MM. yyyy.");
						
			String datum = c.getString(1);
			try {
				Date d = df1.parse(datum);
				datum = String.valueOf(df2.format(d));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			TextView mjesec = (TextView) red.findViewById(R.id.mjesec);
			mjesec.setText(datum);
			
			
			//IZRAÈUNAJ IZNOS
			double[] nove = {c.getDouble(2), c.getDouble(3), 
					c.getDouble(4),c.getDouble(5)};
			
			String text = MainActivity.Izracunaj(nove);
			TextView zarada = (TextView) red.findViewById(R.id.zarada);
			zarada.setText(text);
			
		}
		
		
		
		
	}
	
	
}
