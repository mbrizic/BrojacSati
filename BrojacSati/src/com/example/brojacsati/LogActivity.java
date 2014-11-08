package com.example.brojacsati;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

public class LogActivity extends ActionBarActivity {
	
	BazaHelper bazaHelper;
	SQLiteDatabase baza;
	Cursor kursor;
	SimpleCursorAdapter adapter;
	ListView lv;
	static final String[] FROM = {"datum", "tekst"};
	static final int[] TO = {R.id.datum, R.id.tekst};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log);
		
		lv = (ListView) findViewById(R.id.listView1);
		bazaHelper = new BazaHelper(this);
        baza = bazaHelper.getReadableDatabase();
        kursor = baza.query("unosi", null, null, null, null, null, BazaHelper.C_ID + " DESC");
        
        adapter = new SimpleCursorAdapter(this, R.layout.zapis, kursor, FROM, TO, 0);
        lv.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.log, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.isprazni_bazu) {
			Podaci.IzbrišiSvePodatkeIzTablice("unosi");
			Toast.makeText(this, "Svi zapisi izbrisani", Toast.LENGTH_LONG).show();
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
