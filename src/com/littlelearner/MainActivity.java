package com.littlelearner;

import android.view.Menu;

import android.view.Menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale; 
 
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;


public class MainActivity extends Activity implements OnClickListener, OnInitListener {
	
	private static final int VR_REQUEST = 999;
	private TextView welcometext;
	
	private Button startBtn, exittBtn, helpbtn;
	     
	//Log tag for output information
	private final String LOG_TAG = "MainActivity";//***enter your own tag here***
	 
	//TTS variables
	 
	//variable for checking TTS engine data on user device
	private int MY_DATA_CHECK_CODE = 0;
	
	private TextToSpeech repeatTTS;
	
	HashMap<String, String> map = new HashMap<String, String>();
	     
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		startBtn = (Button) findViewById(R.id.start_btn); 
		exittBtn = (Button) findViewById(R.id.exit_btn);
		helpbtn = (Button) findViewById(R.id.help_btn);
		
		welcometext = (TextView) findViewById(R.id.welcometext);
		welcometext.setSingleLine(false);
		welcometext.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		welcometext.setText("Welcome to the \nLittle Learner App!\n"
							+ "\n Click on 'Start' \n to choose from:\n\nShapes, Aplhabet and Numbers.\n\n"
						    + " Have fun!\n");
		//"\n"+"\n"+"You will have three sections of learning\n"+"For that you will get three options\n"+
		//		"Otions are:\n"+"1. Shape\n"+"2. Alphabet\n"+"3. Numbers\n"+"You can choose anyoption to learn."+
		//"To go to any option, you can click on that menu or you can speak the name of the menu. "+
		//		"Inside each option click the speak button to learn different shapes, alphabet and numbers 1 to 10");
       
		PackageManager packManager = getPackageManager();
		List<ResolveInfo> intActivities = packManager.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (intActivities.size() != 0) {
		    //speech recognition is supported - detect user button clicks
			//startBtn.setOnClickListener(this);
		    
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
			//startBtn.setEnabled(false);
		    Toast.makeText(this, "Oops - Speech recognition not supported!", Toast.LENGTH_LONG).show();
		}
		
		startBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                    	startActivity(new Intent(MainActivity.this, MainMenuActivity.class));
                    }
                });
		
		
		exittBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            	finish();
            }
        });
		
		helpbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            	AlertDialog.Builder alertbox = new AlertDialog.Builder(MainActivity.this);
	            // set the message to display
	            alertbox.setTitle("About The LittleLearner");
	            alertbox.setMessage("LittleLearner will help you to learn shape, alphabet and numbers" +
	            		" To start the application you will have two choices." +
	            		"Say start or Click the button start to start." +
	            		"In main menu you will have 3 options." +
	            		"Say 'Shape' for learning Shapes. " +
	            		"Say 'Alphabet' for learning Alphabet. " +
	            		"Say 'Numbers' for learning Numbers. " +
	            		"Inside 'Shapes' say 'Triangle' or 'Rectangle' or 'Circle'." +
	            		"Click speak button for mutiple commands. " +
	            		"Inside 'Shapes' you can also say colored 'Triangle' or 'Rectangle' or 'Circle'."+
	            		"Inside 'Alphabets' say differnt alphabets and learn it."+
	            		"Inside 'Numbers' say any number from 1 to 10 to learn it.");
	            alertbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	                
	                	public void onClick(DialogInterface intf, int intfother) {
		                	intf.dismiss();
		                	listenToSpeech();
		                }
	               
	            });
	            
	            alertbox.show();
            }
        });
		
	 
	}
	
		
	private void listenToSpeech() {
        
	    //start the speech recognition intent passing required data
	    Intent listenIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	    //indicate package
	    listenIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
	    //message to display while listening
	    listenIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say start");
	    //set speech model
	    listenIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	    //specify number of results to retrieve
	    listenIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);
	 
	    //start listening
	    startActivityForResult(listenIntent, VR_REQUEST);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    //check speech recognition result 
	    if (requestCode == VR_REQUEST && resultCode == RESULT_OK) 
	    {
	        //store the returned word list as an ArrayList
	        ArrayList<String> suggestedWords = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	        
	        
	        //for (int i = 0; i < suggestedWords.size(); ++i) {
		        if(suggestedWords.get(0).contains("start")){
		        	startActivity(new Intent(MainActivity.this, MainMenuActivity.class));
		        }else {
		        	repeatTTS.speak("Say start.", TextToSpeech.QUEUE_FLUSH, null);
		        	new Handler().postDelayed(new Runnable() {
	                    @Override
	                    public void run() {
	                        Log.i("Listening", "Started");
	                                                
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
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * onInit fires when TTS initializes
	 */
	
	public void onInit(int initStatus) { 
	    //if successful, set locale
	    if (initStatus == TextToSpeech.SUCCESS) {
            int result = repeatTTS.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("error", "Language is not supported");
            } else {
                repeatTTS.speak("Welcome to Little Learner, say start or click start to start.", TextToSpeech.QUEUE_FLUSH, null);
                //if(repeatTTS.isSpeaking()== false) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("Listening", "Started");
                                                
                        listenToSpeech();
                    }
                }, 3000);
                //}
            }
        } else {
            Log.e("error", "Failed  to Initilize!");
        }
		
	}
	
		@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	

}

