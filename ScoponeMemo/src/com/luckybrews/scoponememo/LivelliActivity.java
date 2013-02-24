package com.luckybrews.scoponememo;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.luckybrews.scoponememo.R;

public class LivelliActivity extends ListActivity {

	private LayoutInflater mInflater;
	static Vector<LivelloData> livelli; 

	private Context mContext = this;
	static final String TAG = "LivelliActivity";
	
	private ListView mListView;
	static SharedPreferences settings;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.livelli);
		
		// shared prefs
	    settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
		
		mInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		
		// read livelli
		loadFileLivelli(mContext, settings);
		
		MainActivity.max_livello_sd = settings.getLong("MAX_LIVELLO", 1);
		MainActivity.max_livello_cd = settings.getLong("MAX_LIVELLO_COPPIE", 1);
	    
		// display
	    CustomAdapter adapter = new CustomAdapter(this, R.layout.livelli, R.id.livello, livelli);
	    setListAdapter(adapter);        
	    getListView().setTextFilterEnabled(true);
	    
	    // set click listener
	    mListView = (ListView) findViewById(android.R.id.list); 
	    mListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent memo = new Intent(mContext, ScoponeMemoActivity.class);
				memo.putExtra("livello", id);
				finish();
				startActivity(memo);
			}
		});
	    
	  
	  
	}
	
	
	
	private class CustomAdapter extends ArrayAdapter<LivelloData> {

        public CustomAdapter(Context context, int resource, int textViewResourceId, List<LivelloData> objects) {
                super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder = null;

                //widgets displayed by each item in your list
                TextView item = null;
                TextView description = null;
                TextView record = null;
                RatingBar ratingbar = null;
                
                //data from your adapter
                LivelloData rowData = getItem(position);


                //we want to reuse already constructed row views...
                if(null == convertView){
                        convertView = mInflater.inflate(R.layout.livello, null);
                        holder = new ViewHolder(convertView);
                        convertView.setTag(holder);
                }
                
                //  set data
                holder = (ViewHolder) convertView.getTag();
                
                item = holder.getItem();
                item.setText(rowData.getLivello()); 

                description = holder.getDescription();          
                description.setText(rowData.getDescription()); 
                
                record = holder.getRecord();        
                record.setText(rowData.getRecord()); 
                
                ratingbar = holder.getRating();
                ratingbar.setRating(rowData.getRating()); 
                
                if (!isEnabled(position)) {
                	convertView.setBackgroundColor(Color.GRAY);
                	item.setText(rowData.getLivello() + " (non sbloccato)");
                	ratingbar.setVisibility(View.GONE);
                	record.setVisibility(View.GONE);
                } else {
                	convertView.setBackgroundColor(Color.BLACK);
                	item.setText(rowData.getLivello());
                	ratingbar.setVisibility(View.VISIBLE);
                	record.setVisibility(View.VISIBLE);
                }

                return convertView;
        }
        
        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }
        
        @Override
        public boolean isEnabled(int position) {
            if (!MainActivity.coppieEnabled && position < MainActivity.max_livello_sd) 
            	return true;
            
            if (MainActivity.coppieEnabled && position < MainActivity.max_livello_cd) 
            	return true;
            
            return false;
        }
	}
	
	/**
	 * Wrapper for row data.
	 *
	 */
	private class ViewHolder {      
	    private View mRow;
	    private TextView description = null;
	    private TextView item = null;
	    private TextView record = null;
	    private RatingBar ratingbar = null;

	        public ViewHolder(View row) {
	        mRow = row;
	        }

	        public TextView getDescription() {
	                if(null == description){
	                        description = (TextView) mRow.findViewById(R.id.specifichelivello);
	                }
	                return description;
	        }

	        public TextView getItem() {
	                if(null == item){
	                        item = (TextView) mRow.findViewById(R.id.nomelivello);
	                }
	                return item;
	        }   
	        
	        public TextView getRecord() {
                if(null == record){
                	record = (TextView) mRow.findViewById(R.id.record);
                }
                return record;
	        }
	        
	        public RatingBar getRating() {
                if(null == ratingbar){
                	ratingbar = (RatingBar) mRow.findViewById(R.id.ratingbar);
                }
                return ratingbar;
	        }    
	       
	}
	
	public static void loadFileLivelli(Context context, SharedPreferences ss) {
		
		// only once
		//if (livelli != null)
			//return;
		
		// load
		livelli = new Vector<LivelloData>();
    	AssetManager am = context.getAssets();
    	DataInputStream in = null;
    	
    	try {
			in = new DataInputStream(new BufferedInputStream(am.open("livelli.dat")));
			in.readLine(); // skip first line
			
			while (in.available() != 0) {
	    		String str = in.readLine().trim();
	    		
	    		if (str.equals(""))
	    			continue;
	    		
	    		Scanner s = new Scanner(str);
	    		ArrayList<String> livello = new ArrayList<String>();	    		
	    		while (s.hasNext()) {
	    			livello.add(s.next());
	    		}
	  
	    		LivelloData liv = new LivelloData(livello, ss);
	    		livelli.add(liv);
	    	}
			
		} catch (IOException e) {
			Log.e(TAG, "Error opening livelli.dat");
			e.printStackTrace();
		}
    }
}

class LivelloData {
	
		static final String TAG = "LivelloData";
		static SharedPreferences settings;
		
		String liv;
		int liv_idx;
		int sec;
		int num_spa;
		int max_carte;
		float record;
		
		LivelloData(ArrayList<String> data, SharedPreferences s) {
			settings = s;
			liv_idx = Integer.parseInt(data.get(0));
			liv = data.get(1);
			num_spa = Integer.parseInt(data.get(2));
			sec = Integer.parseInt(data.get(3));
			max_carte = Integer.parseInt(data.get(4));
			//record = Float.parseFloat(data.get(5));
			if (MainActivity.coppieEnabled) {
				record = settings.getFloat("RC"+liv_idx, 0);
			} else {
				record = settings.getFloat("RS"+liv_idx, 0);
			}
			Log.d(TAG, "Read record: " + liv_idx + " " + record);
		}
		
		int getLivelloIdx() {
			return liv_idx;
		}
		
		String getLivello() {
			return "Livello " + liv;
		}
		
		String getDescription() {
			if (!MainActivity.coppieEnabled) {
				return num_spa + " sparigli, " + sec + " sec, Max " + max_carte + " carte";
			} else {
				return num_spa + " sparigli, " + num_spa + " coppie, " + sec + " sec, Max " + max_carte + " carte";
			}
		}
		
		float getRating() {
			//float rating = ((float)(num_spa*sec))/record;
			float rating = 6 - 5*record/((float)(num_spa*sec));
			if (rating > 5) 
				rating = 5;
			if (record == 0)
				rating = 0;
			Log.d(TAG, "num_spa*sec=" + num_spa*sec +", record="+record+", rating: " + rating);
			return rating;
		}
		
		String getRecord() {
			if (record == 0){
				return "Record: nessuno";
			}
			return "Record: " + record + " sec	";
		}
		
		
	} 


