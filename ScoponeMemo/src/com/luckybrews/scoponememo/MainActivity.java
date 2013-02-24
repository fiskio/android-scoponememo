package com.luckybrews.scoponememo;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.luckybrews.scoponememo.R;


public class MainActivity extends Activity {
	
	final String TAG = "MainActivity";
	public static final String PREFS_NAME = "ScoponePrefs";
	Context mContext = this;
	
	
	static ArrayList<ArrayList<Integer>> sparigli = new ArrayList<ArrayList<Integer>>();
	static boolean sparigli_loaded = false;
	
	// properties 
	static String mazzo;
	static long max_livello_sd, max_livello_cd, max_livello_sp, max_livello_cp, max_livello_sf, max_livello_cf;
	
	public static boolean coppieEnabled;
	public static boolean testaCoppie;
	public static boolean testaTutto;
	public static boolean testaSparigli;
	
	ImageView img;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    	
		// load properties
		//PropertiesLoader.loadProperties(mContext);
		
        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        mazzo = settings.getString("MAZZO", "Napoletane");
        
        max_livello_sd = settings.getLong("MAX_LIVELLO_SD", 1);
        max_livello_cd = settings.getLong("MAX_LIVELLO_CD", 1);
        
        max_livello_sp = settings.getLong("MAX_LIVELLO_SP", 1);
        max_livello_cp = settings.getLong("MAX_LIVELLO_CP", 1);
        		
        max_livello_sf = settings.getLong("MAX_LIVELLO_SF", 1);
        max_livello_cf = settings.getLong("MAX_LIVELLO_CF", 1);	
        
        coppieEnabled = settings.getBoolean("CoppieEnabled", false);
        
        testaSparigli = settings.getBoolean("testaSparigli", false);
        testaCoppie = settings.getBoolean("testaCoppie", false);
        testaTutto = settings.getBoolean("coppieTutto", true);
        
        // load sparigli
        loadFileSparigli("sparigli.txt");
        loadFileSparigli("sparigli_speciali.txt");
        sparigli_loaded = true;
        
        // load livelli
     	LivelliActivity.loadFileLivelli(mContext, settings);
        
        
     	// gioca
        TextView textGioca = (TextView) findViewById(R.id.textGioca);
        textGioca.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent memo = new Intent(mContext, ScoponeMemoActivity.class);
				memo.putExtra("livello", max_livello_sd-1);
				startActivity(memo);	
			}
        });
        // livelli
        TextView textLivelli = (TextView) findViewById(R.id.textLivelli);
        textLivelli.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent livelli = new Intent(mContext, LivelliActivity.class);
		    	startActivity(livelli);
			}
        });
        // teoria
        TextView textTeoria = (TextView) findViewById(R.id.textTeoria); 
        textTeoria.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent teoria = new Intent(mContext, TeoriaActivity.class);
		    	startActivity(teoria);
			}
        });
        
        // mazzo long press
        img = (ImageView) findViewById(R.id.imageView1);
        registerForContextMenu(img);
    
    }
    
    public void loadFileSparigli(String fn) {
    	
    	if (sparigli_loaded) {
    		return;
    	} 
    	
    	AssetManager am = mContext.getAssets();
    	DataInputStream in = null;
    	
    	try {
			in = new DataInputStream(new BufferedInputStream(am.open(fn)));
			
			while (in.available() != 0) {
	    		String str = in.readLine();
	    		Scanner s = new Scanner(str);
	    		ArrayList<Integer> spariglio = new ArrayList<Integer>();
	    		while (s.hasNext()) {
	    			int n = Integer.parseInt(s.next());
	    			spariglio.add(n);
	    		}
	    		sparigli.add(spariglio);
	    	}
			
		} catch (IOException e) {
			Log.e(TAG, "Error opening sparigli.txt");
			e.printStackTrace();
		}
    }
    
    public void showLivelli() {
    	Intent livelli = new Intent(this, LivelliActivity.class);
    	//finish();
    	startActivity(livelli);
    }
    

	// menu management
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.opt_menu, menu);
		
		return true;
	}
	
	 
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		super.onPrepareOptionsMenu(menu);
		MenuItem mitem = menu.findItem(R.id.menu_coppie);

		if (coppieEnabled) {
			mitem.setTitle("Disabilita coppie");
		} else {
			mitem.setTitle("Abilita coppie");
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		
		case R.id.menu_mazzo:
			openContextMenu(img);
			break;
			
		case R.id.menu_coppie:
			
			if (coppieEnabled) {
				coppieEnabled = false;
			} else {
				coppieEnabled = true;
			}
			break;	    
		}
		return super.onOptionsItemSelected(item);
		
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		

		inflater.inflate(R.menu.mazzi_menu, menu);
		
		MenuItem mitem = null;
		
		if (mazzo.equals("Napoletane")) {
			Log.d(TAG, "context menu Napoletane");
			mitem = menu.findItem(R.id.napoletane); 
			
		} else if (mazzo.equals("Trevisane")) {
			Log.d(TAG, "context menu Napoletane");
			mitem = menu.findItem(R.id.trevisane); 
			
		} else if (mazzo.equals("Bergamasche")) {
			Log.d(TAG, "context menu Bergamasche");
			mitem = menu.findItem(R.id.bergamasche); 
			
		} else if (mazzo.equals("Francitalia")) {
			Log.d(TAG, "context menu Francitalia");
			mitem = menu.findItem(R.id.francitalia); 
			
		} else if (mazzo.equals("Poker")) {
			Log.d(TAG, "context menu Poker");
			mitem = menu.findItem(R.id.poker); 
			
		} else if (mazzo.equals("Scartini")) {
			Log.d(TAG, "context menu Scartini");
			mitem = menu.findItem(R.id.scartini); 
			
		} else if (mazzo.equals("Siciliane")) {
			Log.d(TAG, "context menu Siciliane");
			mitem = menu.findItem(R.id.siciliane); 
			
		} else { 
			Log.e(TAG, "context menu NESSUN MAZZO!!");
			
		}
		
		mitem.setChecked(true);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.napoletane:
			mazzo = "Napoletane";
			return true;
			
		case R.id.trevisane:
			mazzo = "Trevisane";
			return true;
			
		case R.id.francitalia:
			mazzo = "Francitalia";
			return true;
			
		case R.id.bergamasche:
			mazzo = "Bergamasche";
			return true;
			
		case R.id.scartini:
			mazzo = "Scartini";
			return true;
			
		case R.id.siciliane:
			mazzo = "Siciliane";
			return true;
			
		case R.id.poker:
			mazzo = "Poker";
			return true;	
			
		case R.id.piacentine:
			mazzo = "Piacentine";
			return true;	
			
		default:
			return false;
		}
	}
}
