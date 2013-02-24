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
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.luckybrews.scoponememo.R;

public class RispostaFiniteActivity extends Activity implements OnClickListener {
	
	Context mContext = this;
	static final String TAG = "RispostaFiniteActivity";
	Random rand = new Random();
	ImageButton carta;
	ArrayList<Integer> selected = new ArrayList<Integer>();
	static boolean finito = false;
	static float durata = -1;
	static boolean pass = true;
	TextView aux;
	SharedPreferences settings;
   
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.risposta);
       
        settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        
        String seme = randomSeed();
         
        carta = (ImageButton) findViewById(R.id.uno);
        setCardImage(carta, 1, seme);
        carta.setOnClickListener(this);
        
        carta = (ImageButton) findViewById(R.id.due);
        setCardImage(carta, 2, seme);
        carta.setOnClickListener(this);
        
        carta = (ImageButton) findViewById(R.id.tre);
        setCardImage(carta, 3, seme);
        carta.setOnClickListener(this);
        
        carta = (ImageButton) findViewById(R.id.quattro);
        setCardImage(carta, 4, seme);
        carta.setOnClickListener(this);
        
        carta = (ImageButton) findViewById(R.id.cinque);
        setCardImage(carta, 5, seme);
        carta.setOnClickListener(this);
        
        carta = (ImageButton) findViewById(R.id.sei);
        setCardImage(carta, 6, seme);
        carta.setOnClickListener(this);
        
        carta = (ImageButton) findViewById(R.id.sette);
        setCardImage(carta, 7, seme);
        carta.setOnClickListener(this);
        
        carta = (ImageButton) findViewById(R.id.otto);
        setCardImage(carta, 8, seme);
        carta.setOnClickListener(this);
        
        carta = (ImageButton) findViewById(R.id.nove);
        setCardImage(carta, 9, seme);
        carta.setOnClickListener(this);
        
        carta = (ImageButton) findViewById(R.id.dieci);
        setCardImage(carta, 10, seme);
        carta.setOnClickListener(this);
        
        LinearLayout tw = (LinearLayout) findViewById(R.id.buttonBar);
        tw.setOnClickListener(this);
        
        aux = (TextView) findViewById(R.id.toprisp);
        aux.setText("Seleziona le carte finite	");
        
        if (finito) {
			evidenziaRisposta();
			if (pass) 
				aux.setText("Tempo totale: " + durata + " sec");
				
        } else {
        	//aux.setText("Tempo totale: " + durata + " sec");
        }
    }
    
    @Override 
    public void onPause() {
    	super.onPause();
    	//finito = false;
		//selected = new ArrayList<Integer>();
		//durata = -1;
    }
    
    @Override  
    public void onClick(View view) {  
        
    	switch(view.getId()){  
        
    		case R.id.uno:  
    			select(view, 1);
            	break;  
            	
    		case R.id.due:  
    			select(view, 2);		  
    			break;  
    			
    		case R.id.tre:  
    			select(view, 3);
            	break;  
            	
    		case R.id.quattro:  
    			select(view, 4);			  
    			break; 
    			
    		case R.id.cinque:  
    			select(view, 5);
            	break;  
            	
    		case R.id.sei:  
    			select(view, 6);			  
    			break;  
    		
    		case R.id.sette:  
    			select(view, 7);
            	break;  
            	
    		case R.id.otto:  	
    			select(view, 8);
    			break;  
    			
    		case R.id.nove:  
    			select(view, 9);
            	break;  
            	
    		case R.id.dieci: 
    			select(view, 10);
    			break;
    			
    		case R.id.buttonBar:  
    			//controllaRisposta();
    			if (!finito) {
    				evidenziaRisposta();
    			} else {
    				Intent risInt = new Intent(mContext, ScoponeMemoActivity.class);
    				risInt.putExtra("livello", ScoponeMemoActivity.livello);
    				finito = false;
    				selected = new ArrayList<Integer>();
    				durata = -1;
    				pass = true;
        			RispostaFiniteActivity.this.finish();
        			startActivity(risInt);
    			}
    				
    			break; 
    			
        }  
    } 
    
    public void setCardImage(ImageButton imageButton, int number, String seed) {
    	
    	AssetManager am = mContext.getAssets();
    	String str = "carte/" + MainActivity.mazzo + "/" + number+seed + ".png";
    	try {
    		BufferedInputStream buf = new BufferedInputStream(am.open(str));
    		Bitmap bitmap = BitmapFactory.decodeStream(buf);
    		imageButton.setImageBitmap(bitmap);
    		imageButton.setBackgroundColor(Color.TRANSPARENT);
    		imageButton.setPadding(10,10,10,10);
    		
    		Display display = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            int rotation = display.getOrientation();
            
            // fix margin
            if (rotation==Surface.ROTATION_90 || rotation==Surface.ROTATION_270) { // landscape
            	int topMargin = ((MarginLayoutParams) imageButton.getLayoutParams()).topMargin;
            	int bottomMargin = ((MarginLayoutParams) imageButton.getLayoutParams()).bottomMargin;
            	Log.d(TAG, "topM " + topMargin + " bottom: " + bottomMargin);
            	if (topMargin == 30) {
            		((MarginLayoutParams) imageButton.getLayoutParams()).topMargin = 5;
            	}
            	if (bottomMargin == 30) {
            		((MarginLayoutParams) imageButton.getLayoutParams()).bottomMargin = 5;
            	}
           
            } 
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
    public String randomSeed() {
    	
    	int n = rand.nextInt(4);
    	
    	if (n == 0)
    		return "s";
    	if (n == 1)
    		return "c";
    	if (n == 2)
    		return "d";
    	
    	return "b";   	
	}
    
    public void select(View view, int n) {
    	if (view.isSelected()) { 
			view.setBackgroundColor(Color.TRANSPARENT);
			view.setSelected(false);
			selected.remove(new Integer(n));
		} else {
			view.setBackgroundColor(Color.BLUE);
			view.setSelected(true);
			selected.add(n);
		}
    }
    
    public void setBackColor(int idx, int color) {
    	
    	ImageButton carta;
    	
    	switch(idx) {

    	case 1:
    		carta = (ImageButton) findViewById(R.id.uno);
    		carta.setBackgroundColor(color);
    		break;

    	case 2:
    		carta = (ImageButton) findViewById(R.id.due);
    		carta.setBackgroundColor(color);
    		break;

    	case 3:
    		carta = (ImageButton) findViewById(R.id.tre);
    		carta.setBackgroundColor(color);
    		break;

    	case 4:
    		carta = (ImageButton) findViewById(R.id.quattro);
    		carta.setBackgroundColor(color);
    		break;

    	case 5:
    		carta = (ImageButton) findViewById(R.id.cinque);
    		carta.setBackgroundColor(color);
    		break;
    		
    	case 6:
    		carta = (ImageButton) findViewById(R.id.sei);
    		carta.setBackgroundColor(color);
    		break;

    	case 7:
    		carta = (ImageButton) findViewById(R.id.sette);
    		carta.setBackgroundColor(color);
    		break;
    		
    	case 8:
    		carta = (ImageButton) findViewById(R.id.otto);
    		carta.setBackgroundColor(color);
    		break;

    	case 9:
    		carta = (ImageButton) findViewById(R.id.nove);
    		carta.setBackgroundColor(color);
    		break;
    		
    	case 10:
    		carta = (ImageButton) findViewById(R.id.dieci);
    		carta.setBackgroundColor(color);
    		break;
    	}
    }
    
    public void evidenziaRisposta() {
    	Log.d(TAG, selected.toString());
    	//boolean pass = true;

    	// size
    	if (ScoponeMemoActivity.finite.size() != selected.size()) {
    		pass = false;
    	}
    	
    	// check
    	for (Integer i : selected) {
    		if (!ScoponeMemoActivity.finite.contains(i)) {
    			// make red
    			setBackColor(i.intValue(), Color.RED);
    			pass = false;
    		} 
    	}
    	for (Integer i : ScoponeMemoActivity.finite) {		
    		// make green
    		setBackColor(i.intValue(), Color.GREEN);	
    	}
    	
    	// update button
    	TextView tw = (TextView) findViewById(R.id.bottonebasso);
    	finito = true;
    	if (pass) {	
    		getTempo();
    		aumentaLivello();
    		tw.setText("Bravo! Passa al livello successivo");
    	} else {
    		aux.setText("Risposta errata :-(");
    		tw.setText("Riprova");
    	}
    	
    }
    
    public void getTempo() {
    	
    	if (durata != -1)
    		return;
    	
    	durata = (ScoponeMemoActivity.tempo); //ms
    	//Log.d(TAG, "Tempo totale " + durata);
    	durata /= 1000;
    	
    	// get record
    	float record;
    	
    	if (MainActivity.coppieEnabled) {
    		record = settings.getFloat("RCF"+(ScoponeMemoActivity.livello+1), 0); // Record Coppie Finite
    	} else {
    		record = settings.getFloat("RSF"+(ScoponeMemoActivity.livello+1), 0); // Record Sparigli Finite
    	}
    	// check record   	
    	if (durata < record || record == 0) {
    		SharedPreferences.Editor editor = settings.edit();
    		if (MainActivity.coppieEnabled) {
    			editor.putFloat("RCF"+(ScoponeMemoActivity.livello+1), durata);
        	} else {
        		editor.putFloat("RSF"+(ScoponeMemoActivity.livello+1), durata);
        	}
        	//editor.putFloat(""+(ScoponeMemoActivity.livello+1), durata);
        	editor.commit();
        	aux.setText("Nuovo Record! " + durata + " sec");
        	//Log.d(TAG, "saved new record: " + ScoponeMemoActivity.livello + " " + durata);
    	} else {
    		aux.setText("Tempo totale: " + durata + " sec");
    	}
    	Log.d(TAG, "Tempo totale " + durata);
    }
    
    public void controllaRisposta() {

    	Log.d(TAG, selected.toString());
    	
    	// size
    	if (ScoponeMemoActivity.finite.size() != selected.size()) {
    		makeMessageNO();
    		return;
    	}
    	
    	// check
    	for (Integer i : selected) {
    		if (!ScoponeMemoActivity.finite.contains(i)) {
    			makeMessageNO();
    			return;
    		}
    	}
    	
    	// OK
    	makeMessageOK();
    }
    
    
    
    public void makeMessageOK() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("Risposta corretta :-)")
    	.setTitle("BRAVO!")
    	.setCancelable(false)
    	.setPositiveButton("Continua", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int id) {
    			
    			aumentaLivello();
    			
    			Intent risInt = new Intent(mContext, ScoponeMemoActivity.class);
    			RispostaFiniteActivity.this.finish();
				startActivity(risInt);
    			
    		}
    	})
    	.setNegativeButton("Esci", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int id) {
    			RispostaFiniteActivity.this.finish();
    		}
    	});
    	AlertDialog alert = builder.create();
    	alert.show();
    	Log.d(TAG, "YES");
    }
    
    public void makeMessageNO() {
    	
    	Collections.sort(ScoponeMemoActivity.finite);
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("Risposta corretta:\n" + ScoponeMemoActivity.finite.toString())
    	.setTitle("ERRORE!")
    	.setCancelable(false)
    	.setPositiveButton("Riprova", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int id) {
    			Intent risInt = new Intent(mContext, ScoponeMemoActivity.class);
    			RispostaFiniteActivity.this.finish();
				startActivity(risInt);
    			
    		}
    	})
    	.setNegativeButton("Esci", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int id) {
    			RispostaFiniteActivity.this.finish();
    		}
    	});
    	AlertDialog alert = builder.create();
    	alert.show();
    	Log.d(TAG, "NO");
    }

    public void makeMessageFINISHED() {

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("Hai completato tutti i livelli")
    	.setTitle("COMPLIMENTI!")
    	.setCancelable(false)
    	.setNeutralButton("OK", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int id) {
    			RispostaFiniteActivity.this.finish();
    		}
    	});
    	AlertDialog alert = builder.create();
    	alert.show();
    	Log.d(TAG, "FINISHED");
    }

    public long aumentaLivello() {

    	if (MainActivity.coppieEnabled) {
    		if (ScoponeMemoActivity.livello == LivelliActivity.livelli.size()-1) {
    			makeMessageFINISHED();
    			return -1;
    		} 
    		// aumenta livello
    		ScoponeMemoActivity.livello++;

    		// sblocca livello
    		SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
    		SharedPreferences.Editor editor = settings.edit();
    		editor.putLong("MAX_LIVELLO_CF", ScoponeMemoActivity.livello+1);
    		Log.d(TAG, "Sbloccato il livello " + (ScoponeMemoActivity.livello+1));
    		// Commit the edits!
    		editor.commit();
    		return ScoponeMemoActivity.livello;
    	} else {
    		if (ScoponeMemoActivity.livello == LivelliActivity.livelli.size()-1) {
    			makeMessageFINISHED();
    			return -1;
    		} 
    		// aumenta livello
    		ScoponeMemoActivity.livello++;

    		// sblocca livello
    		SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
    		SharedPreferences.Editor editor = settings.edit();
    		editor.putLong("MAX_LIVELLO_SF", ScoponeMemoActivity.livello+1);
    		Log.d(TAG, "Sbloccato il livello " + (ScoponeMemoActivity.livello+1));
    		// Commit the edits!
    		editor.commit();
    		return ScoponeMemoActivity.livello;
    	}
    	
    	
    }
    
    @Override
    public void onBackPressed() {
    	super.onBackPressed();
    	finito = false;
		selected = new ArrayList<Integer>();
		durata = -1;
		pass = true;
    }
    
    
    	
}
