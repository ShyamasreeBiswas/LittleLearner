package com.littlelearner;

import android.view.Menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale; 
 
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;

public class MainMenuActivity extends Activity implements OnClickListener, OnInitListener {
	
	//voice recognition and general variables
	 
	//variable for checking Voice Recognition support on user device
	private static final int VR_REQUEST = 999;
	     
	//ListView for displaying suggested words
	private ListView wordList;
	     
	//Log tag for output information
	private final String LOG_TAG = "MainMenuActivity";//***enter your own tag here***
	
	private Button speechBtn;
	 
	//TTS variables
	 
	//variable for checking TTS engine data on user device
	private int MY_DATA_CHECK_CODE = 0;
	     
	//Text To Speech instance
	private TextToSpeech repeatTTS;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);	
		
		
		//gain reference to speak button
		//Button speechBtn = (Button) findViewById(R.id.speech_btn);
		speechBtn = (Button) findViewById(R.id.speech_btn);
		//gain reference to word list
		wordList = (ListView) findViewById(R.id.word_list);
		
		ArrayList<String> menulist = new ArrayList<String>();
        
        menulist.add("Shape");
        menulist.add("Alphabet");
        menulist.add("Number");
        //menulist.add("Canvas");
        
        wordList.setAdapter(new ArrayAdapter<String> (this, R.layout.word, menulist));
		
		//find out whether speech recognition is supported
		PackageManager packManager = getPackageManager();
		List<ResolveInfo> intActivities = packManager.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (intActivities.size() != 0) {
		    //speech recognition is supported - detect user button clicks
		    speechBtn.setOnClickListener(this);
		    
		  //prepare the TTS to repeat chosen words
		    Intent checkTTSIntent = new Intent();  
		    //check TTS data  
		    checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);  
		    //start the checking Intent - will retrieve result in onActivityResult
		    startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
		}
		else
		{
		    //speech recognition not supported, disable button and output message
		    speechBtn.setEnabled(false);
		    Toast.makeText(this, "Oops - Speech recognition not supported!", Toast.LENGTH_LONG).show();
		}
		
		
		//detect user clicks of suggested words
		wordList.setOnItemClickListener(new OnItemClickListener() {
		             
		    //click listener for items within list
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
		    {
		        
		    	//cast the view
		        TextView wordView = (TextView)view;
		        //retrieve the chosen word
		        String wordChosen = (String) wordView.getText();
		        //output for debugging
		        //Log.v(LOG_TAG, "chosen: "+wordChosen);
		        
		        if(wordChosen == "Alphabet"){
		        	startActivity(new Intent(MainMenuActivity.this, AlphaNumActivity.class));
		        }else if(wordChosen == "Shape"){
		        	startActivity(new Intent(MainMenuActivity.this, DrawShapeActivity.class));
		        }else if(wordChosen == "Number"){
		        	startActivity(new Intent(MainMenuActivity.this, ShowNumbersActivity.class));
		        }
		        //output Toast message
		        Toast.makeText(MainMenuActivity.this, "You selected: "+wordChosen, Toast.LENGTH_SHORT).show();//**alter for your Activity name***
		        
		      //speak the word using the TTS
		        repeatTTS.speak("You have selected: "+wordChosen, TextToSpeech.QUEUE_FLUSH, null);
		    }
		}); 
	}
	
	/**
	 * Called when the user presses the speak button
	 */
	public void onClick(View v) {
	    if (v.getId() == R.id.speech_btn) {
	        //listen for results
	        listenToSpeech();
	    }
	}
	
	/**
	 * Instruct the app to listen for user speech input
	 */
	private void listenToSpeech() {
	         
	    //start the speech recognition intent passing required data
	    Intent listenIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	    //indicate package
	    listenIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
	    //message to display while listening
	    listenIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say shape or alphabet or number");
	    //set speech model
	    listenIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	    //specify number of results to retrieve
	    listenIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);
	 
	    //start listening
	    startActivityForResult(listenIntent, VR_REQUEST);
	}
	
	/**
	 * onActivityResults handles:
	 *  - retrieving results of speech recognition listening
	 *  - retrieving result of TTS data check
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    //check speech recognition result 
	    if (requestCode == VR_REQUEST && resultCode == RESULT_OK) 
	    {
	        //store the returned word list as an ArrayList
	        ArrayList<String> suggestedWords = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	        
	        
	        //for (int i = 0; i < suggestedWords.size(); ++i) {
		        if(suggestedWords.get(0).contains("alphabet")){
		        	startActivity(new Intent(MainMenuActivity.this, AlphaNumActivity.class));
		        }else if(suggestedWords.get(0).contains("shape")){
		        	startActivity(new Intent(MainMenuActivity.this, DrawShapeActivity.class));
		        }else if(suggestedWords.get(0).contains("number")){
		        	startActivity(new Intent(MainMenuActivity.this, ShowNumbersActivity.class));
		        }else {
		        	repeatTTS.speak("Say shape or alphabet or number.", TextToSpeech.QUEUE_FLUSH, null);
		        	new Handler().postDelayed(new Runnable() {
	                    @Override
	                    public void run() {
	                        Log.i("Listening", "Started");
	                        speechBtn.setVisibility(View.VISIBLE);
	                        speechBtn.setOnClickListener(MainMenuActivity.this);                        
	                        listenToSpeech();
	                    }
	                }, 2000);
		        }
	        //set the retrieved list to display in the ListView using an ArrayAdapter
	        //}
	    }
	         
	    //tts code here
	    
	  //returned from TTS data check
	    if (requestCode == MY_DATA_CHECK_CODE) 
	    {  
	        //we have the data - create a TTS instance
	        if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS)  
	            repeatTTS = new TextToSpeech(this, this);  
	        //data not installed, prompt the user to install it  
	        else
	        {  
	            //intent will take user to TTS download page in Google Play
	            Intent installTTSIntent = new Intent();  
	            installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);  
	            startActivity(installTTSIntent);  
	        }  
	    }
	 
	    //call superclass method
	    super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.speech_repeat, menu);
		return true;
	}
	
	/**
	 * onInit fires when TTS initializes
	 */
	public void onInit(int initStatus) { 
	    //if successful, set locale
	    if (initStatus == TextToSpeech.SUCCESS)   
	        //repeatTTS.setLanguage(Locale.US);//***choose your own locale here***  
	    {
            int result = repeatTTS.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("error", "Language is not supported");
            } else {
                repeatTTS.speak("Select an option", TextToSpeech.QUEUE_FLUSH, null);
                //if(repeatTTS.isSpeaking()== false) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("Listening", "Started");
                        speechBtn.setVisibility(View.VISIBLE);
                        speechBtn.setOnClickListener(MainMenuActivity.this);                        
                        listenToSpeech();
                    }
                }, 2000);
                //}
            }
        } else {
            Log.e("error", "Failed  to Initilize!");
        }
	    //listenToSpeech();
	    
	    
	}
	
	

}

