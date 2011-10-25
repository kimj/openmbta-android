package com.kaja.openmbta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class OpenMBTA extends ListActivity {
	  public static String MY_PREFS = "OpenMBTA";
	  private MergeAdapter mAdapter=null;
	
	  private ArrayList<transItem> transItems;
	  private ArrayList<transItem> transItems2;
	  private ArrayList<transItem> transItemsBM;
	  private ListView myListView;
	  private ListView myListView2;
	  private transAdapter aa;
	  private transAdapter aa2;
	  private transAdapter bm_aa;  // adapter for bookmarks
	  private LinearLayout ll;
	  private HashMap bookmarkHash;
	  private hashFile hf;
	  private String bmFileName;
	  private int bmCount = 0;
	  
	  static final private int TRANS_DIALOG = 1;
	  String selectedTrans;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
    	super.onCreate(icicle);
    	
    }
    
    public void initActivity() {
    	try{
    		setContentView(R.layout.main);
    		bookmarkHash = getBookmarkHash();
        
    		// Get references to UI widgets
    		myListView = (ListView)findViewById(android.R.id.list);
    		int resID = R.layout.row;
        
    		mAdapter = new MergeAdapter();
   
    		mAdapter.addView(buildLabel("Bookmarks"));
        
        if (!bookmarkHash.isEmpty()){
        	// we have retrieved bookmarks.  need to display
        	 transItemsBM = new ArrayList<transItem>();
        	 bm_aa = new transAdapter(this, resID, transItemsBM);
        	 loadBookmarks();
        	 myListView.setAdapter(bm_aa);
             mAdapter.addAdapter(bm_aa);
        }
     
        mAdapter.addView(buildLabel("Transportation Options"));
        
       
        transItems = new ArrayList<transItem>();
     
     
        aa = new transAdapter(this, resID, transItems);
        loadItems();
        myListView.setAdapter(aa);
        mAdapter.addAdapter(aa);
        
        //Now put second header
        mAdapter.addView(buildLabel("Others"));
        myListView2 = (ListView)findViewById(android.R.id.list);
        transItems2 = new ArrayList<transItem>();
        aa2 = new transAdapter(this, resID, transItems2);
        loadOthers();
        myListView.setAdapter(aa2);
        mAdapter.addAdapter(aa2);
        
     setListAdapter(mAdapter);
    	}
    
    	catch (Exception e)
    	{
    		Log.e("Error", "Error in code:" + e.toString());
    		e.printStackTrace();
    	}
    }
    
    public void onResume() {
    	super.onResume();
    	initActivity();    	
    }

    public void onListItemClick(ListView parent, View v,
			int _index, long id) {
    	Log.d("test", String.valueOf(_index));
		int bmOffset = bmCount + 1; 

    		try { 
                if (_index < bmOffset){
                	// user has clicked on a bookmark.  Need to launch into schedule tab
                	Intent i = new Intent(OpenMBTA.this, ScheduleTab.class); 
                	// pass in some data to next Activity
                	
                	i.putExtra ("route", transItemsBM.get(_index - 1).getRoute());
                	i.putExtra ("headsign", transItemsBM.get(_index - 1).getDesc());
                	i.putExtra ("transtype", transItemsBM.get(_index - 1).getType());
                	
                	OpenMBTA.this.startActivity(i);
                	
                }
    			
    			
    			else if (_index==6 + bmOffset){
            		Intent i = new Intent(OpenMBTA.this, WebViewer.class); 
                	// pass in some data to next Activity
                	i.putExtra ("sURL", "http://openmbta.org/alerts.html");
                	i.putExtra("sTitle", "OpenMBTA: Alerts");
                	
            		OpenMBTA.this.startActivity(i);
            	}
            	else if (_index==7 + bmOffset){
            		Intent i = new Intent(OpenMBTA.this, WebViewer.class); 
                	// pass in some data to next Activity
                	i.putExtra ("sURL", "http://openmbta.org/tweets");
                	i.putExtra("sTitle", "OpenMBTA: Tweets");
            		OpenMBTA.this.startActivity(i); 
            	}
            	else {
            	
            	Intent i = new Intent(OpenMBTA.this, Routes.class); 
            	// pass in some data to next Activity
            	i.putExtra ("transtype", transItems.get(_index - 1 - bmOffset).getType());
            	OpenMBTA.this.startActivity(i);
            	}
            	}
            	catch (Exception e)
            	{
            		Log.e("Error", "Error in code:" + e.toString());
            		e.printStackTrace();
            	}
     }
    
    private void loadItems2(String title, String desc, String type)
    {	
    	transItem t3 = new transItem("", title, desc, type);
		aa.add(t3);

	    aa.notifyDataSetChanged();
		
    }
    private void loadItems2a(String title, String desc, String type)
    {	
    	transItem t3 = new transItem("", title, desc, type);
		aa2.add(t3);

	    aa2.notifyDataSetChanged();
		
    }
    
	private View buildLabel(String labelText) {
		TextView result=new TextView(this);
		result.setTextColor( 0xFFFFFFFF );
		result.setBackgroundColor(0xFF6E6E6E);
		//result.setTextColor(getResources().getColor(R.color.main_paper));
		result.setTextSize(2,14);
		
		//TextView result = (TextView)findViewById(R.id.label);
		result.setText("  " + labelText);
			
		return(result);
	}
	
    private void loadItems(){
    
    	loadItems2("Subway", "", "subway");
    	loadItems2("Bus", "", "bus");
    	loadItems2("Commuter Rail", "", "train");
    	loadItems2("Boat", "", "boat");
    	//loadItems2("T Alerts", "", "alert");
    	
    }

public void loadOthers() {
	loadItems2a("T Alerts", "", "alert");
	loadItems2a("Tweets", "", "tweet");
}

public void loadPreferences(){
	int mode = Activity.MODE_PRIVATE;
}

private HashMap getBookmarkHash(){
	HashMap<String, Integer> bmHash = new HashMap<String, Integer>();
	 	bmFileName = getString(R.string.bookmark_file);

	hf = new hashFile();
	//bookmarkHash = hf.doLoad(bmFileName);
    if (hf.doLoad(bmFileName, this) != null ) {
    	bmHash = hf.doLoad(bmFileName, this); 
    }
	 
	return bmHash;
}

private void loadBookmarks(){
	 Set set= bookmarkHash.entrySet (  ) ; 
     Iterator iter = set.iterator (  ) ; 
     bmCount=0;
     int i=1; 
     //HashMapIterator 
     
     while ( iter.hasNext ())  {  
  
       String [] transType;
       String tt = iter.next().toString();
       transType = tt.split("=");
       String [] routeDir;
       String rd = transType[0];
       routeDir = rd.split(";");
       
       
       //String rName = 
       transItem t4 = new transItem(routeDir[0],  routeDir[0],  routeDir[1], transType[1]);
       
       bm_aa.add(t4);
       i++; 
       bmCount++;
      }
    bm_aa.notifyDataSetChanged();
}
}