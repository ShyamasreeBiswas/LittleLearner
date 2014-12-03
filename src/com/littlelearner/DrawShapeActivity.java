package com.littlelearner;


import android.view.Menu;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale; 
import java.util.Map;
import java.util.HashMap;
 
import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.FillType;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;

public class DrawShapeActivity extends Activity implements OnClickListener, OnInitListener {
	
	//voice recognition and general variables
	 
	//variable for checking Voice Recognition support on user device
	private static final int VR_REQUEST = 999;
	
	     
	//ListView for displaying suggested words
	//private ListView wordList;
	
	// ImageView for displaying the alphabet/number
	private ImageView imageShape;
	private Bitmap myBitmap;
	//private Paint myPaint;
	     
	//Log tag for output information
	private final String LOG_TAG = "DrawShapeActivity";//***enter your own tag here***
	 
	private Button speechBtn;
	//TTS variables
	 
	//variable for checking TTS engine data on user device
	private int MY_DATA_CHECK_CODE = 0;
	     
	//Text To Speech instance
	private TextToSpeech repeatTTS;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_draw_shape);
		
		//gain reference to speak button
		//Button speechBtn = (Button) findViewById(R.id.speech_btn);
		speechBtn = (Button) findViewById(R.id.speech_btn);
		//Button speechBtn1 = (Button) findViewById(R.id.speech_btn2);
		//gain reference to word list
		//wordList = (ListView) findViewById(R.id.word_list);
		this.imageShape = (ImageView) findViewById(R.id.draw_shape);
		
		//setContentView(new MyViewRect(this));
		
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
	    listenIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a word!");
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
	        
        	AssetManager assetManager=this.getAssets();
        	InputStream is = null;
			try {
				is = assetManager.open("whitebackground.jpg");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	myBitmap=BitmapFactory.decodeStream(is);
            //Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
            Bitmap tempBitmap = Bitmap.createBitmap(150, 200, Bitmap.Config.RGB_565);
            Canvas tempCanvas = new Canvas(tempBitmap);
            tempCanvas.drawBitmap(myBitmap, 0, 0, null);
            
            Paint myPaint = new Paint();
            myPaint.setColor(Color.BLACK);
            myPaint.setStyle(Paint.Style.STROKE);
            myPaint.setStrokeWidth(5);
            myPaint.setAlpha(60);
            
            
            imageShape.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
	        
	        
	        if(suggestedWords.get(0).contains("circle") || suggestedWords.get(1).contains("circle")){

	            	            
	        	if(suggestedWords.get(0).contains("red")){
	            	myPaint.setColor(Color.RED);
	                myPaint.setStyle(Paint.Style.FILL);
	            }else if(suggestedWords.get(0).contains("blue")){
	            	myPaint.setColor(Color.BLUE);
	                myPaint.setStyle(Paint.Style.FILL);
	            }else if(suggestedWords.get(0).contains("green")){
	            	myPaint.setColor(Color.GREEN);
	                myPaint.setStyle(Paint.Style.FILL);
	            }else if(suggestedWords.get(0).contains("cyan")){
	            	myPaint.setColor(Color.CYAN);
	                myPaint.setStyle(Paint.Style.FILL);
	            }else if(suggestedWords.get(0).contains("yellow")){
	            	myPaint.setColor(Color.YELLOW);
	                myPaint.setStyle(Paint.Style.FILL);
	            }else if(suggestedWords.get(0).contains("magenta")){
	            	myPaint.setColor(Color.MAGENTA);
	                myPaint.setStyle(Paint.Style.FILL);
	            }
	            
	            tempCanvas.drawCircle(80,90, 50, myPaint);
	            
	            if(suggestedWords.get(1).contains("")){
	            	
	            	repeatTTS.speak("You said: "+suggestedWords.get(0), TextToSpeech.QUEUE_FLUSH, null);
	            }else {
	            	repeatTTS.speak("You said: "+suggestedWords.get(0)+" "+suggestedWords.get(1), TextToSpeech.QUEUE_FLUSH, null);
	            }

	        	
	        }else if(suggestedWords.get(0).contains("rectangle") || suggestedWords.get(1).contains("rectangle")){	        	

	        	if(suggestedWords.get(0).contains("red")){
	            	myPaint.setColor(Color.RED);
	                myPaint.setStyle(Paint.Style.FILL);
	            }else if(suggestedWords.get(0).contains("blue")){
	            	myPaint.setColor(Color.BLUE);
	                myPaint.setStyle(Paint.Style.FILL);
	            }else if(suggestedWords.get(0).contains("green")){
	            	myPaint.setColor(Color.GREEN);
	                myPaint.setStyle(Paint.Style.FILL);
	            }else if(suggestedWords.get(0).contains("cyan")){
	            	myPaint.setColor(Color.CYAN);
	                myPaint.setStyle(Paint.Style.FILL);
	            }else if(suggestedWords.get(0).contains("yellow")){
	            	myPaint.setColor(Color.YELLOW);
	                myPaint.setStyle(Paint.Style.FILL);
	            }else if(suggestedWords.get(0).contains("magenta")){
	            	myPaint.setColor(Color.MAGENTA);
	                myPaint.setStyle(Paint.Style.FILL);
	            }
	        	
	        	tempCanvas.drawRect(30, 80, 120, 140, myPaint);
	        	
	        	if(suggestedWords.get(1).contains("")){
	            	
	            	repeatTTS.speak("You said: "+suggestedWords.get(0), TextToSpeech.QUEUE_FLUSH, null);
	            }else {
	            	repeatTTS.speak("You said: "+suggestedWords.get(0)+" "+suggestedWords.get(1), TextToSpeech.QUEUE_FLUSH, null);
	            }

	        	
	        }else if(suggestedWords.get(0).contains("square") || suggestedWords.get(1).contains("square")){	        	

	        	if(suggestedWords.get(0).contains("red")){
	            	myPaint.setColor(Color.RED);
	                myPaint.setStyle(Paint.Style.FILL);
	            }else if(suggestedWords.get(0).contains("blue")){
	            	myPaint.setColor(Color.BLUE);
	                myPaint.setStyle(Paint.Style.FILL);
	            }else if(suggestedWords.get(0).contains("green")){
	            	myPaint.setColor(Color.GREEN);
	                myPaint.setStyle(Paint.Style.FILL);
	            }else if(suggestedWords.get(0).contains("cyan")){
	            	myPaint.setColor(Color.CYAN);
	                myPaint.setStyle(Paint.Style.FILL);
	            }else if(suggestedWords.get(0).contains("yellow")){
	            	myPaint.setColor(Color.YELLOW);
	                myPaint.setStyle(Paint.Style.FILL);
	            }else if(suggestedWords.get(0).contains("magenta")){
	            	myPaint.setColor(Color.MAGENTA);
	                myPaint.setStyle(Paint.Style.FILL);
	            }
	        	
	        	tempCanvas.drawRect(30, 60, 90, 120, myPaint);
	        	
	        	if(suggestedWords.get(1).contains("")){
	            	
	            	repeatTTS.speak("You said: "+suggestedWords.get(0), TextToSpeech.QUEUE_FLUSH, null);
	            }else {
	            	repeatTTS.speak("You said: "+suggestedWords.get(0)+" "+suggestedWords.get(1), TextToSpeech.QUEUE_FLUSH, null);
	            }

	        	
	        }else if(suggestedWords.get(0).contains("triangle") || suggestedWords.get(1).contains("triangle")){	        	
	        	
	        	if(suggestedWords.get(0).contains("red")){
	            	myPaint.setColor(Color.RED);
	                myPaint.setStyle(Paint.Style.FILL);
	            }else if(suggestedWords.get(0).contains("blue")){
	            	myPaint.setColor(Color.BLUE);
	                myPaint.setStyle(Paint.Style.FILL);
	            }else if(suggestedWords.get(0).contains("green")){
	            	myPaint.setColor(Color.GREEN);
	                myPaint.setStyle(Paint.Style.FILL);
	            }else if(suggestedWords.get(0).contains("cyan")){
	            	myPaint.setColor(Color.CYAN);
	                myPaint.setStyle(Paint.Style.FILL);
	            }else if(suggestedWords.get(0).contains("yellow")){
	            	myPaint.setColor(Color.YELLOW);
	                myPaint.setStyle(Paint.Style.FILL);
	            }else if(suggestedWords.get(0).contains("magenta")){
	            	myPaint.setColor(Color.MAGENTA);
	                myPaint.setStyle(Paint.Style.FILL);
	            }
	        	
	            Point a = new Point(40, 100);
	            Point b = new Point(70, 70);
	            Point c = new Point(100, 60);
	            
	            Path path = new Path();
	            
	            path.setFillType(FillType.EVEN_ODD);
	            //Log.v(LOG_TAG, "step4");
	            
	            path.moveTo(120, 100);
	            path.lineTo(a.x, a.y);
	            path.lineTo(b.x, b.y);
	            //path.lineTo(c.x, c.y);
	            
	            path.close();

	            tempCanvas.drawPath(path, myPaint);
	            
	            if(suggestedWords.get(1).contains("")){
	            	
	            	repeatTTS.speak("You said: "+suggestedWords.get(0), TextToSpeech.QUEUE_FLUSH, null);
	            }else {
	            	repeatTTS.speak("You said: "+suggestedWords.get(0)+" "+suggestedWords.get(1), TextToSpeech.QUEUE_FLUSH, null);
	            }

	        	
	        }else {
	        	repeatTTS.speak("Please say circle or rectangle or triangle or square", TextToSpeech.QUEUE_FLUSH, null);
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
                repeatTTS.speak("Please say any shape.", TextToSpeech.QUEUE_FLUSH, null);
                //if(repeatTTS.isSpeaking()== false) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("Listening", "Started");
                        speechBtn.setVisibility(View.VISIBLE);
                        speechBtn.setOnClickListener(DrawShapeActivity.this);                        
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

