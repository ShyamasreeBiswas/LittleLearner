package com.littlelearner;


import android.view.Menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale; 
import java.util.Map;
import java.util.HashMap;
 
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
import android.widget.ImageView;

public class ShowNumbersActivity extends Activity implements OnClickListener, OnInitListener {
	
	//voice recognition and general variables
	 
	//variable for checking Voice Recognition support on user device
	private static final int VR_REQUEST = 999;
	private static final Map<String, Integer> TEXT_TO_IMAGE_ID;
	static {
		TEXT_TO_IMAGE_ID = new HashMap<String, Integer>();
		TEXT_TO_IMAGE_ID.put("1", 	R.drawable.one);
		TEXT_TO_IMAGE_ID.put("2", 	R.drawable.two);
		TEXT_TO_IMAGE_ID.put("3",	R.drawable.three);
		TEXT_TO_IMAGE_ID.put("4", 	R.drawable.four);
		TEXT_TO_IMAGE_ID.put("5",  R.drawable.five);
		TEXT_TO_IMAGE_ID.put("6",   R.drawable.six);
		TEXT_TO_IMAGE_ID.put("7", R.drawable.seven);
		TEXT_TO_IMAGE_ID.put("8", R.drawable.eight);
		TEXT_TO_IMAGE_ID.put("9",  R.drawable.nine);
		TEXT_TO_IMAGE_ID.put("10",   R.drawable.ten);
		
		
		
	}
	
	     
	//ListView for displaying suggested words
	//private ListView wordList;
	
	// ImageView for displaying the alphabet/number
	private ImageView imageAlphaNum;
	     
	//Log tag for output information
	private final String LOG_TAG = "AlphaNumActivity";//***enter your own tag here***
	 
	private Button speechBtn;
	//TTS variables
	 
	//variable for checking TTS engine data on user device
	private int MY_DATA_CHECK_CODE = 0;
	     
	//Text To Speech instance
	private TextToSpeech repeatTTS;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_num);
		
		//gain reference to speak button
		//Button speechBtn = (Button) findViewById(R.id.speech_btn);
		speechBtn = (Button) findViewById(R.id.speech_btn);
		//gain reference to word list
		//wordList = (ListView) findViewById(R.id.word_list);
		this.imageAlphaNum = (ImageView) findViewById(R.id.number_alphabet);
		
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
		
	}
	
	/**
	 * Called when the user presses the speak button
	 */
	public void onClick(View v) {
	    if (v.getId() == R.id.speech_btn) {
	    	Log.v(LOG_TAG, "Speech Button Clicked");
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
	    listenIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say some number!");
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
	        ArrayList<String> wordsToNumber = new ArrayList<String>();
	        // Identify the number in the suggested words
	        Integer resId = null;
	        for (int i = 0; i < suggestedWords.size(); ++i) {
	        	Log.v(LOG_TAG, "suggestedWord["+i+"]="+ suggestedWords.get(i));
	        	resId = TEXT_TO_IMAGE_ID.get(suggestedWords.get(i).toLowerCase(Locale.US));
	        	if (resId != null) {
	        		repeatTTS.speak("You said: "+ suggestedWords.get(i), TextToSpeech.QUEUE_FLUSH, null);
	        		break;
	        	}
	        }
	        //set the retrieved list to display in the ListView using an ArrayAdapter
	        // wordList.setAdapter(new ArrayAdapter<String> (this, R.layout.word, suggestedWords));
	        //  wordList.setAdapter(new ArrayAdapter<String> (this, R.layout.word, wordsToNumber));
	        if (resId != null) {
	        	this.imageAlphaNum.setImageResource(resId);
	        }
	        else {
	        	repeatTTS.speak("Please say any number, for example 1 or 2 within 1 to 2.", TextToSpeech.QUEUE_FLUSH, null);
	        	listenToSpeech();
	        }
	    }
	         
	    //tss code here
	    
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
                repeatTTS.speak("Please say any Number from 1 to 10.", TextToSpeech.QUEUE_FLUSH, null);
                //if(repeatTTS.isSpeaking()== false) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("Listening", "Started");
                        speechBtn.setVisibility(View.VISIBLE);
                        speechBtn.setOnClickListener(ShowNumbersActivity.this);                        
                        listenToSpeech();
                    }
                }, 2000);
                //}
            }
        } else {
            Log.e("error", "Failed  to Initilize!");
        }	
	}

}

