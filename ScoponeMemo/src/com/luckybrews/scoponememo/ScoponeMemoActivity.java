package com.luckybrews.scoponememo;

import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.luckybrews.scoponememo.R;

public class ScoponeMemoActivity extends Activity  implements OnClickListener {
	
	Context mContext = this;
	String TAG = "ScoponeMemoActivity";
	Random rand = new Random();
	String[]seeds = {"c", "d", "b", "s"};
	
	
	//private static Handler mHandler = new Handler();
	private int iter = 1;
	
	static ArrayList<Integer> dispari = new ArrayList<Integer>();
	static ArrayList<Integer> coppie = new ArrayList<Integer>();
	static ArrayList<Integer> finite = new ArrayList<Integer>();
	static ArrayList<ArrayList<Integer>> usciti = new ArrayList<ArrayList<Integer>>();
	
	// default values
	//static String mazzo = "Napoletane";
	static int interval = 1; 
	static int rounds = 1;
	static int max_carte = 1;
	static String lname;
	
	// update thread
	private static Runnable mUpdateCardsTask = null;
	private static ArrayList<Integer> spariglio_corrente;
	private static CountDownTimer timer = null;
	private static TextView cd, tw;
	private static boolean running = false;
	static ImageView carta1, carta2, carta3, carta4, carta5;
	static LinearLayout ll3, ll4, ll5;
	static ArrayList<String> carte_attuali;
	
	// tempo
	static long tempo;
	//static long livello_sd, livello_cd, livello_sp, livello_cp, livello_sf, livello_cf;
	static long livello;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spariglio);
        
        carta1 = (ImageView) findViewById(R.id.imageView1);
        carta2 = (ImageView) findViewById(R.id.imageView2);
        carta3 = (ImageView) findViewById(R.id.imageView3);
        carta4 = (ImageView) findViewById(R.id.imageView4);
        carta5 = (ImageView) findViewById(R.id.imageView5);
        ll3 = (LinearLayout) findViewById(R.id.ll3);
        ll4 = (LinearLayout) findViewById(R.id.ll4);
        ll5 = (LinearLayout) findViewById(R.id.ll5);
        cd = (TextView) findViewById(R.id.cronometro);
        tw = (TextView) findViewById(R.id.iter);
    
        if (running) {
        	Log.d(TAG, "Task already running");
        	// display
			displaySpariglio(carte_attuali);
			//setLivello();
			setTop();
        	return;
        }
        Bundle bundle = getIntent().getExtras();
        livello = bundle.getLong("livello");
       
        dispari.clear();
        coppie.clear();
        finite.clear();
        
        setLivello();
        
        showLivello();
        
       
        tempo = System.currentTimeMillis();
    }
    
    @Override 
    public void onPause() {
    	super.onPause();
    	running = false;
    	if (timer != null)
    		timer.cancel();
    }

    /**
     * Set a card image
     * 
     * @param imageView image view to be set
     * @param number of the card
     * @param seed of the card
     */
    public void setCardImage(ImageView imageView, String carta) {
    	
    	AssetManager am = mContext.getAssets();
    	String str = "carte/" + MainActivity.mazzo + "/" + carta + ".png";
    	try {
    		BufferedInputStream buf = new BufferedInputStream(am.open(str));
    		Bitmap bitmap = BitmapFactory.decodeStream(buf);
    		imageView.setImageBitmap(bitmap);
    		buf.close();
    		
    	} catch (Exception e) {
    		Log.e(TAG, "Error loading card image: " + str);
    		e.printStackTrace();
    	}
    }
    
    /**
     * Returns a random seed for cards
     * 
     * @return
     */
    public ArrayList<String> randomSeeds(ArrayList<Integer> s) {
    	ArrayList<String> ret = new ArrayList<String>();
    	
    	for (int i=0; i<s.size(); i++) {
    		String str;
    		do {
    			int n = rand.nextInt(4);
    			str = s.get(i) + seeds[n];
    		} while (ret.contains(str));
    		
    		ret.add(str);
    	}
    	
    	return ret;   	
    }
    
    public ArrayList<Integer> selectCoppia() {
    	ArrayList<Integer> ret = new ArrayList<Integer>();
   
    	// check 
    	if (finite.size() == 10) {
    		//makeMessage("Non esistono piu' coppie");
    		Log.e(TAG, "Non esistono piu' coppie!!!");
    		return ret;
    	}
    	
    	int coppia; 
    	do {
    		coppia = rand.nextInt(10)+1;
    	} while (finite.contains(coppia)); // controllo che non sia finita
    	
    	ret.add(new Integer(coppia));
    	ret.add(new Integer(coppia));
    	
    	carte_attuali = randomSeeds(ret);
    	
    	return ret;
    }
    
    public ArrayList<Integer> selectSpariglio() {
	
    	ArrayList<Integer> ret;
    	do {
    		int idx = rand.nextInt(MainActivity.sparigli.size());
    		ret = MainActivity.sparigli.get(idx);
    		
    		if (usciti.contains(ret)) {
    			continue;
    		}
    		usciti.add(ret);
    		
    		for (Integer i : ret) {
    			
    			// controllo finite
    			if (finite.contains(i)) {
    				continue;
    			}
    			
    			// controllo coppie
    			ArrayList<Integer> dd = new ArrayList<Integer>();
    			dd.add(i);
    			dd.add(i);
    			if (ret.containsAll(dd)) {
    				// doppio!
    				if (coppie.contains(i) && dispari.contains(i)) {
    					continue;
    				}
    			}
    			
    		}
    	} while (ret.size() > max_carte);
    	
    	carte_attuali = randomSeeds(ret);
    	
    	return ret;
    }
    
    public void displaySpariglio(ArrayList<String> spariglio) {
    	Log.d(TAG, "DisplaySpariglio");
    	
    	//ArrayList<String> seeds = randomSeeds(s);
    	
    	// 3 is minimum
    	//ImageView carta1 = (ImageView) findViewById(R.id.imageView1);
    	setCardImage(carta1, spariglio.get(0));
    	carta1.setVisibility(View.VISIBLE);
    	carta1.setOnClickListener(this);
        
    	//ImageView carta2 = (ImageView) findViewById(R.id.imageView2);

    	setCardImage(carta2, spariglio.get(1));
    	carta2.setVisibility(View.VISIBLE);
    	carta2.setOnClickListener(this);
    	
        //ImageView carta3 = (ImageView) findViewById(R.id.imageView3);
        setCardImage(carta3, spariglio.get(2));
        carta3.setVisibility(View.VISIBLE);
        carta3.setOnClickListener(this);
        ll3.setVisibility(View.VISIBLE);
        
        // 4th
        if (spariglio.size() > 3) {
        	//LinearLayout ll4 = (LinearLayout) findViewById(R.id.ll4);
        	ll4.setVisibility(View.VISIBLE);
        	
        	//ImageView carta4 = (ImageView) findViewById(R.id.imageView4);
        	setCardImage(carta4, spariglio.get(3));
        	carta4.setVisibility(View.VISIBLE);
        	carta4.setOnClickListener(this);
        	
        } else {
        	Log.d(TAG, "Hiding 4th card");
            //LinearLayout ll = (LinearLayout) findViewById(R.id.ll4);
            ll4.setVisibility(View.GONE);
        }
        
        // 5th
        if (spariglio.size() > 4) {
        	//LinearLayout ll5 = (LinearLayout) findViewById(R.id.ll5);
        	ll5.setVisibility(View.VISIBLE);
        	
        	//ImageView carta5 = (ImageView) findViewById(R.id.imageView5);
        	setCardImage(carta5, spariglio.get(4));
        	carta5.setVisibility(View.VISIBLE);
        	carta5.setOnClickListener(this);
        	
        } else {
        	Log.d(TAG, "Hiding 5th card");
           // LinearLayout ll5 = (LinearLayout) findViewById(R.id.ll5);
            ll5.setVisibility(View.GONE);
        }
 
    }
    
    public void displayCoppia(ArrayList<String> coppia) {
    	Log.d(TAG, "DisplayCoppia");
    	
    	//ArrayList<String> seeds = randomSeeds(s);
    	
    	// 3 is minimum
    	//ImageView carta1 = (ImageView) findViewById(R.id.imageView1);
    	setCardImage(carta1, coppia.get(0));
    	carta1.setVisibility(View.VISIBLE);
    	carta1.setOnClickListener(this);
        
    	//ImageView carta2 = (ImageView) findViewById(R.id.imageView2);
    	setCardImage(carta2, coppia.get(1));
    	carta2.setVisibility(View.VISIBLE);
    	carta2.setOnClickListener(this);
    	
    	ll3.setVisibility(View.GONE);
    	ll4.setVisibility(View.GONE);
    	ll5.setVisibility(View.GONE);
    
        // 3th
        if (coppia.size() > 3) {
        	//LinearLayout ll4 = (LinearLayout) findViewById(R.id.ll4);
        	ll4.setVisibility(View.VISIBLE);
        	
        	//ImageView carta4 = (ImageView) findViewById(R.id.imageView4);
        	setCardImage(carta4, coppia.get(3));
        	carta4.setVisibility(View.VISIBLE);
        	carta4.setOnClickListener(this);
        	
        } else {
        	Log.d(TAG, "Hiding 4th card");
            //LinearLayout ll = (LinearLayout) findViewById(R.id.ll4);
            ll4.setVisibility(View.GONE);
        }
        // 4th
        if (coppia.size() > 3) {
        	//LinearLayout ll4 = (LinearLayout) findViewById(R.id.ll4);
        	ll4.setVisibility(View.VISIBLE);
        	
        	//ImageView carta4 = (ImageView) findViewById(R.id.imageView4);
        	setCardImage(carta4, coppia.get(3));
        	carta4.setVisibility(View.VISIBLE);
        	carta4.setOnClickListener(this);
        	
        } else {
        	Log.d(TAG, "Hiding 4th card");
            //LinearLayout ll = (LinearLayout) findViewById(R.id.ll4);
            ll4.setVisibility(View.GONE);
        }
        
        // 5th
        if (coppia.size() > 4) {
        	//LinearLayout ll5 = (LinearLayout) findViewById(R.id.ll5);
        	ll5.setVisibility(View.VISIBLE);
        	
        	//ImageView carta5 = (ImageView) findViewById(R.id.imageView5);
        	setCardImage(carta5, coppia.get(4));
        	carta5.setVisibility(View.VISIBLE);
        	carta5.setOnClickListener(this);
        	
        } else {
        	Log.d(TAG, "Hiding 5th card");
           // LinearLayout ll5 = (LinearLayout) findViewById(R.id.ll5);
            ll5.setVisibility(View.GONE);
        }
 
    }
    
    
    public void updateUsciti(ArrayList<Integer> s) {
    	
    	Log.d(TAG, "Spariglio: " + s.toString());
    	for (Integer i : s) {
    		if (dispari.contains(i)) {
    			dispari.remove(i);
    			ArrayList<Integer> aux = new ArrayList<Integer>();
    			aux.add(i);
    			updateCoppia(aux);
    		} else {
    			dispari.add(i);
    		}
    		
    	}
    
    }
    
    public void updateCoppia(ArrayList<Integer> s) {
    	
    	//

    	Integer i = s.get(0);

    	if (coppie.contains(i)) {
    		coppie.remove(i);
    		finite.add(i); // finite
    	} else {
    		coppie.add(i);
    	}

    }
    
    public void dumpAll() {
    	
    	Collections.sort(dispari);
    	Collections.sort(coppie);
    	Collections.sort(finite);
    	
    	String log = "DISPARI: " + dispari + "\n";
    	log += 		 "COPPIE:  " + coppie + "\n";
    	log +=       "FINITE:  " + finite + "\n";
    	Log.d(TAG, log);
    }
    
    
    public void showLivello() {
    	
    	final AlertDialog ad = new AlertDialog.Builder(ScoponeMemoActivity.this).create();
		ad.setTitle("Livello " + lname);
		ad.setCancelable(false);

		if (MainActivity.coppieEnabled) {
			ad.setMessage("Sparigli: " + (rounds/2) + "\nCoppie: " + (rounds/2) + "\nIntervallo: " + interval + " secondi\nMax " + max_carte + " carte");
		} else {
			ad.setMessage("Sparigli: " + rounds + "\nIntervallo: " + interval + " secondi\nMax " + max_carte + " carte");	
		}
		
		ad.setButton("Ok", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				
				
					startThread();
					
					//mHandler.removeCallbacks(mUpdateCardsTask);
					//mHandler.post(mUpdateCardsTask);
					
					ad.dismiss();
					Toast.makeText(ScoponeMemoActivity.this, "Tocca una carta per passare subito allo spariglio successivo", Toast.LENGTH_SHORT).show();
				
			}
		});
		ad.show();
		
    }
    
    private void startThread() {
    	mUpdateCardsTask = new Runnable() {	

    		@Override
    		public void run() {
    			Log.d(TAG, "started mUpdateCardsTask");
    			if (iter <= rounds) {
    				
    				dumpAll();
    				if (MainActivity.coppieEnabled && iter%2 == 0) {

    					Log.d(TAG, "Mostra coppia");
    					
    					// select
    					spariglio_corrente = selectCoppia();
    					
    					Log.d(TAG, "Coppia: " + spariglio_corrente);
    					
    					// display
    					displayCoppia(carte_attuali);
    					
    					// update 
    					updateCoppia(spariglio_corrente);
    					 
    					
    				
    				} else {
    					
    					Log.d(TAG, "Mostra spariglio");
    					
    					// select
    					spariglio_corrente = selectSpariglio();

    					// display
    					displaySpariglio(carte_attuali);

    					// update 
    					updateUsciti(spariglio_corrente);
    					
    				}
    				dumpAll();
    				// set top
    				setTop();
    				running = true;
    				iter++;
    			} else {
    				Intent risInt = new Intent(mContext, RispostaSparigliActivity.class);
    				tempo = System.currentTimeMillis() - tempo;
    				ScoponeMemoActivity.this.finish();
    				startActivity(risInt);
    			}
    		}
    	};
    	mUpdateCardsTask.run();
    }
    
    
    public void setLivello() {

		/*
    	if (MainActivity.coppieEnabled) {
    		if (MainActivity.testaCoppie) {
    			livello = livello_cp;
    		}
    		if (MainActivity.testaTutto) {
    			livello = livello_cf;
    		}
    		if (MainActivity.testaSparigli) {
    			livello = livello_cd;
    		}
    	} else {
    		if (MainActivity.testaCoppie) {
    			livello = livello_sp;
    		}
    		if (MainActivity.testaTutto) {
    			livello = livello_sf;
    		}
    		if (MainActivity.testaSparigli) {
    			livello = livello_sd;
    		}
    	}
    	*/
    	
    	LivelloData ld = LivelliActivity.livelli.get((int)livello);
    	
    	//int [] liv = lista_livelli[livello];
    	if (MainActivity.coppieEnabled) {
    		Log.d(TAG, "COPPIE ABILITATE!");
    		rounds = ld.num_spa*2;
    	} else {	
    		rounds = ld.num_spa;
    	}
    	
    	interval = ld.sec;
    	max_carte = ld.max_carte;
    	lname = ld.liv;
    }
    
    public void setTop() { 
    	// mostra livello
    	//TextView tw = (TextView) findViewById(R.id.iter);
    	tw.setText(iter + "/" + rounds);

    	// mostra cronometro
    	//cd = (TextView) findViewById(R.id.cronometro);
    	//cd.setText("0:00");
    	if (!running) {
    		timer = new CountDownTimer(interval*1000, 100) {

    			public void onTick(long millisUntilFinished) {
    				//cd.setText("0:"+(millisUntilFinished/100));
    				int sec = 1+Math.round(millisUntilFinished/1000);
    				cd.setText("0:0"+sec);
    			}

    			public void onFinish() {
    				//mTextField.setText("done!");
    				running = false;
    				startThread();
    			}
    		}.start();  
    	}

    }


	@Override
	public void onClick(View v) {
		timer.cancel();
		timer.onFinish();
	}
    
    /*
    void saveLivello() {
    	AssetManager am = mContext.getAssets();
    	FileOutputStream fout = new FileOutputStream(am.open("livello.txt"));
    }
    */
}