package com.ostrenkov.artem.vb42;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ostrenkov.artem.vb42.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public TextToSpeech TTS;
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private AudioManager am;
    private int aSoundVolume;
    public String sMyMessage = "Artem";
    public String sCurrentQUestion = "";
    public TextView tvMain = null;
    public WebView wbBrowse = null;
    public String sAnswer = "Что вы хотите подобрать?";
    public String sCORR_Messsage = "";
    public Button bPress = null;
    public String HTMLmessage = "<h3>" +sAnswer + "</h3>";
    public Handler mainHandler = null;
    public Runnable myRunnable = null;
    public TextView mTextStatus = null;
    public ScrollView mScrollView = null;
    private int tryagainlimit = 4;
    public int tryagain = tryagainlimit;
    public boolean flagStartafter = false;
    public String userID = "123";
    public Float fSPEED = 1.1f;
    public Float fPITCH = 1.3f;




    public void getparam(String name, String res){

        SharedPreferences sharedPref = getSharedPreferences(name, MODE_PRIVATE);
        String mySetting = sharedPref.getString(name, null);
        res = mySetting;

    }
    public String getparam(String name){

        SharedPreferences sharedPref = getSharedPreferences(name, MODE_PRIVATE);
        String mySetting = sharedPref.getString(name, null);
        return mySetting;

    }

    public void setparam(String name,  String res) {

        SharedPreferences sharedPref = getSharedPreferences(name, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(name, res);
        editor.commit();

    }

    public String AddToHtml ( String newString) {

        HTMLmessage = HTMLmessage + "<h3>" + newString + "</h3>";

        return HTMLmessage;
    }


    public String AddToHtmlleft ( String newString) {

        HTMLmessage = HTMLmessage + "<h3 align=left >" + newString + "</h3>";

        return HTMLmessage;
    }

    public String AddToHtmlRight ( String newString) {

        HTMLmessage = HTMLmessage + "<h3 ><font align=right color=blue>" + newString + "</font></h3>";

        return HTMLmessage;
    }

    public String AddToHtmlCM ( String newString) {

        HTMLmessage = HTMLmessage + "<h4 ><font align=right color=red>" + newString + "</font></h4>";

        return HTMLmessage;
    }

    public void getResultWord(ArrayList<String> matches) {
        int sadday = 0;
        String sspell;
        String sToast = "Ой все";

        //Toast.makeText(MainActivity.this, "Результат получил", Toast.LENGTH_SHORT).show();
        if (!matches.isEmpty()) {

            sToast = matches.get(0);
           // tvMain.setText(tvMain.getText()+" "+'\n');
           // tvMain.setText(tvMain.getText()+"                           << "+sToast);
            SendMessage(sToast);

           // Toast.makeText(MainActivity.this, tvMain.getText()+sToast+'\n', Toast.LENGTH_SHORT).show();
          //  tvMain.setText(tvMain.getText()+" "+'\n');
          //  tvMain.setText(tvMain.getText()+"                           << "+sToast);

        }
        //Toast.makeText(MainActivity.this, "I heard this: " + sToast, Toast.LENGTH_SHORT).show();


    }

    public void getResultPart(ArrayList<String> matches) {

        String sspell ="";
        String sToast = "Ой все";
        Log.d("MyTAG", "onPartialResults");
       /* for (String match : matches) {
            match = match.toLowerCase();
            sspell = sspell + match;
            Log.d("MyTAG", "onPartialResults : " + match);
        }
*/
        //Toast.makeText(MainActivity.this, sspell, Toast.LENGTH_SHORT).show();
        /*if (!matches.isEmpty()) {

            sToast = matches.get(0);
            //tvMain.setText(tvMain.getText()+" "+'\n');
            //tvMain.setText(tvMain.getText()+"                           << "+sToast);
            Toast.makeText(MainActivity.this, sToast, Toast.LENGTH_SHORT).show();
            Log.d("MyTAG", "onPartialResults : " + match);
            // Toast.makeText(MainActivity.this, tvMain.getText()+sToast+'\n', Toast.LENGTH_SHORT).show();
            //  tvMain.setText(tvMain.getText()+" "+'\n');
            //  tvMain.setText(tvMain.getText()+"                           << "+sToast);

        }*/
        //Toast.makeText(MainActivity.this, "I heard this: " + sToast, Toast.LENGTH_SHORT).show();


    }

    private void scrollToBottom()
    {
        mScrollView.post(new Runnable()
        {
            public void run()
            {
                mScrollView.smoothScrollTo(0, mTextStatus.getBottom());
            }
        });
    }

    public void SendMessageold(final String sMyMessageInternal){

        RequestQueue queue = Volley.newRequestQueue(this);
        //String url = "http://92.53.108.10:7888/api/v1/write";
        String url = "http://92.53.108.10:7888/api/v1/write";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //Toast.makeText(MainActivity.this, "Response is: " + response, Toast.LENGTH_SHORT).show();

                        //String sAnswer = "- Ничего не понимаю :(";
                        String sLink = "https://www.ulmart.ru/";
                        //////
                        JSONArray dataJsonArr = null;
                        JSONObject json = null;
                        JSONObject jsonMessage = null;
                        try {
                      //      Toast.makeText(MainActivity.this, "Json1", Toast.LENGTH_SHORT).show();

                            json = new JSONObject(response);
                            //jsonMessage =new JSONObject(json.getString("response"));
                            dataJsonArr = json.getJSONArray("response");

                            //Toast.makeText(MainActivity.this, "Json lenght: " + dataJsonArr.length(), Toast.LENGTH_SHORT).show();

                            for (int i = 0; i < dataJsonArr.length(); i++) {

                                JSONObject c = dataJsonArr.getJSONObject(i);
                                if (c.has("text"))  { sAnswer = c.getString("text");}

                                if (c.has("link"))  { sLink = c.getString("link").replace("Goods","");}

                            }

                           // tvMain.setText(tvMain.getText()+" "+'\n');
                           // tvMain.setText(tvMain.getText() + "-" + "Json2 "+ json.getString("response"));

                            //sAnswer = jsonMessage.getString("text");

                           // tvMain.setText(tvMain.getText() + "-" + "Json3 "+ jsonMessage.getString("text"));

                     //       Toast.makeText(MainActivity.this, "Json2"+ json.getString("response"), Toast.LENGTH_SHORT).show();


/*

                            dataJsonArr = json.getJSONArray("text");

                            Toast.makeText(MainActivity.this, "Json3", Toast.LENGTH_SHORT).show();
                            for (int i = 0; i < dataJsonArr.length(); i++) {

                                JSONObject c = dataJsonArr.getJSONObject(i);

                                Toast.makeText(MainActivity.this, "Json text is: " + c.getString("text"), Toast.LENGTH_SHORT).show();


                            }
*/

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();

                        }

                        // get the array of users

                        /////

                        //tvMain.setText(tvMain.getText()+" "+'\n');
                        //tvMain.setText(tvMain.getText() + ">> " + sAnswer);

                       // sAnswer.compareTo("Отменил выбор");
                        if (sAnswer.equals("Отменил выбор") == true) {
                            sAnswer = "Что вы бы хотели выбрать?";}
                      //  Log.d("MyTag",sAnswer.toLowerCase() + "Отменил выбор".toLowerCase());
                        tvMain.setText(sAnswer);
                        //wbBrowse.loadUrl(sLink);


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "That didn't work!", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                String mess ="{\"message\": \"" + sMyMessageInternal + "\", \"user\": \"124\"}";
                Log.d("GETBODYWRONG","UPS "+mess);
                return mess.getBytes(Charset.forName("UTF-8"));
            }
        };
        queue.add(stringRequest);




    }
    //////////

    public void SendMessage(final String sMyMessageInternal){

        tvMain.setVisibility(View.GONE);
        ((TextView) findViewById(R.id.textChat)).setText(Html.fromHtml(AddToHtmlRight(sMyMessageInternal)));

        scrollToBottom();

        RequestQueue queue = Volley.newRequestQueue(this);
        //String url = "http://92.53.108.10:7888/api/v1/write";
        String url = getparam("URL");
        //String url = "http://82.202.192.186:10500";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //Toast.makeText(MainActivity.this, "Response is: " + response, Toast.LENGTH_SHORT).show();

                        //String sAnswer = "- Ничего не понимаю :(";
                        String sLink = "http://yandex.ru";
                        //////
                        JSONArray dataJsonArr = null;
                        JSONObject json = null;
                        JSONObject jsonMessage = null;
                        try {
                            //      Toast.makeText(MainActivity.this, "Json1", Toast.LENGTH_SHORT).show();

                            json = new JSONObject(response);
                            //jsonMessage =new JSONObject(json.getString("response"));
                            //dataJsonArr = json.getJSONArray("response");

                            String resp = json.getString("response");
                            String corrected_message = json.getString("corrected_message");


                            Log.d("MyTag",resp);
                            Log.d("MyTag",corrected_message);

                            sCORR_Messsage  = corrected_message;
                            sAnswer = resp;

                            //Toast.makeText(MainActivity.this, "Json lenght: " + dataJsonArr.length(), Toast.LENGTH_SHORT).show();

                           /* for (int i = 0; i < dataJsonArr.length(); i++) {

                                JSONObject c = dataJsonArr.getJSONObject(i);
                                Log.d("MyTag",c.getString("response"));

                                *//*if (c.has("text"))  { sAnswer = c.getString("text");}
                                corrected_message
                                        message_id

                                response

                                if (c.has("link"))  { sLink = c.getString("link").replace("Goods","");}*//*

                            }
*/

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();

                        }

                             if (sAnswer.equals("Отменил выбор") == true) {
                            sAnswer = "Что вы бы хотели выбрать?";}
                        //  Log.d("MyTag",sAnswer.toLowerCase() + "Отменил выбор".toLowerCase());
                        tvMain.setText(sAnswer);

                        ((TextView) findViewById(R.id.textChat)).setText(Html.fromHtml(AddToHtmlCM(sCORR_Messsage)));
                        ((TextView) findViewById(R.id.textChat)).setText(Html.fromHtml(AddToHtmlleft(sAnswer)));

                        scrollToBottom();
                        StartSpeak(sAnswer);

                     ///   StartSpeak(sAnswer);
                        //wbBrowse.loadUrl(sLink);


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "That didn't work!", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
               // String mess ="{\"text\": \"" + sMyMessageInternal + "\", \"user_id\": \"123\"}";
                String mess ="{\"text\": \"" + sMyMessageInternal + "\", \"user_id\": \""+userID+"\"}";
                Log.d("GETBODY",mess);
                ///((TextView) findViewById(R.id.textChat)).setText(Html.fromHtml(AddToHtmlRight(sAnswer)));
                return mess.getBytes(Charset.forName("UTF-8"));
            }
        };
        queue.add(stringRequest);




    }


    /////////
    public void listenRebuild(){


        if (mSpeechRecognizer != null)
        {
            mSpeechRecognizer.destroy();
        }


        SpeechRecognitionListener listener = new SpeechRecognitionListener();
        mSpeechRecognizer.setRecognitionListener(listener);
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

    }


    public void onClickClear(View view) {

        flagStartafter = true;
        StartSpeak("Слушаю вас");
        //listenRebuild();
        //SendMessage("отмена");

        //StartSpeak("Я говорю по русски");
        //tvMain.setText("Что вы бы хотели выбрать?");
    }

    public void onbSend(View view) {

       String chatMess = ( (EditText) findViewById(R.id.eChatSend)).getText().toString();
        ( (EditText) findViewById(R.id.eChatSend)).setText("");
        //( (EditText) findViewById(R.id.eChatSend)).clearFocus();

      //  ( (EditText) findViewById(R.id.eChatSend)).sele


        if (chatMess.equals("42")) {

            Intent intent = new Intent(this, Options.class);
            startActivity(intent);
        } else SendMessage(chatMess);


    }

    //////////
    protected class SpeechRecognitionListener implements RecognitionListener
    {

        @Override
        public void onBeginningOfSpeech()
        {
            // Log.d("","qws");
          Log.d("MyTag", "onBeginingOfSpeech");
        }

        @Override
        public void onBufferReceived(byte[] buffer)
        {

        }

        @Override
        public void onEndOfSpeech()
        {
            Log.d("MyTag", "onEndOfSpeech");
        }

        @Override
        public void onError(int error)
        {
            // mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
          //  listenRebuild();

            tryagain = tryagain - 1;
            Log.d("Speech", "tryagain " + tryagain);
            if (tryagain > 0) {
                Log.d("Speech", "tryagain > 0 " + tryagain);

                switch (tryagain){

                    case 1 : speakWords("Говорите или отключаюсь");
                        break;
                    case 2 : speakWords("Говорите после сигнала");
                        break;
                    default:  speakWords("Повторите?");


                }


                //listenRebuild();
            }

            Log.d("MyTag", "error = " + error);
        }

        @Override
        public void onEvent(int eventType, Bundle params)
        {

        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            //getResultPart(partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION));

          ArrayList<String> matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
           /* for (String match : matches) {
                match = match.toLowerCase();
                Log.d("MyTag", "onPartialResults : " + match);
            }*/
           // Toast.makeText(MainActivity.this,matches.get(0), Toast.LENGTH_SHORT).show();
            if (!matches.get(0).equals("")) {
                tvMain.setVisibility(View.VISIBLE);
                tvMain.setText(matches.get(0));}
        }

        @Override
        public void onReadyForSpeech(Bundle params)
        {
            // Log.d(TAG, "onReadyForSpeech"); //$NON-NLS-1$

        }

        @Override
        public void onResults(Bundle results)
        {

           //TODO:послать в очередь

            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            if (!matches.get(0).equals("")) {

                if (matches.get(0).equals("стоп")) flagStartafter = false;
                else {
                    SendMessage(matches.get(0));
                    tryagain = tryagainlimit;
                    Log.d("Speech", "tryagain " + tryagain);

                }

            }
            else {

                tryagain = tryagain - 1;
                Log.d("Speech", "tryagain " + tryagain);
                if (tryagain > 0) {
                    Log.d("Speech", "tryagain > 0 " + tryagain);
                    listenRebuild();
                }

            }



        }

        @Override
        public void onRmsChanged(float rmsdB)
        {
        }
    }
    /////////

    //_______________________
    private class MyWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {

            view.loadUrl(url);
            return true;
        }
    }
    @Override
    public void onBackPressed() {
        if(wbBrowse.canGoBack()) {
            wbBrowse.goBack();
        } else {
            super.onBackPressed();
        }
    }
    //_______________________



    ////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //ActivityCompat.requestPermissions(MainActivity.this,
        //        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
        //        1);
        int MYRECORD_AUDIO = 1;

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},MYRECORD_AUDIO);

        }

        Log.d("REQUEST RESULT"," " + MYRECORD_AUDIO);

        SharedPreferences prefs = getSharedPreferences("com.ostrenkov.artem.seaspeak", MODE_PRIVATE);

        if (prefs.getBoolean("firstrun", true)) {

            setparam("URL","http://82.202.192.186:10500");
            setparam("URL1","http://82.202.192.186:10500");
            setparam("URL2","http://82.202.192.186:10500");
            setparam("URL3","http://82.202.192.186:10500");
            setparam("URL4","http://82.202.192.186:10500");

            setparam("SPEED","1.0");
            setparam("PITCH","1.0");


            int min = 65;
            int max = 500;

            Random r = new Random();
            int i1 = r.nextInt(max - min + 1) + min;

            setparam("userID","1"+i1);


            prefs.edit().putBoolean("firstrun", false).commit();
        }

        userID = getparam("userID");
        fPITCH = Float.parseFloat(getparam("PITCH"));
        fSPEED = Float.parseFloat(getparam("SPEED"));



            tvMain = (TextView) findViewById(R.id.tvHello);
        wbBrowse = (WebView) findViewById(R.id.webView);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        bPress = (Button) findViewById(R.id.bPress);

        mTextStatus = (TextView) findViewById(R.id.textChat);
        mScrollView = (ScrollView) findViewById(R.id.SCROLLER_ID);


        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float percent = 0.99f;
        int seventyVolume = (int) (maxVolume*percent);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, seventyVolume, 0);


        mainHandler = new Handler(this.getMainLooper());

        myRunnable = new Runnable() {
            @Override
            public void run() {
              if (flagStartafter)  listenRebuild();
            } // This is your code
        };





        bPress.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    sCurrentQUestion = tvMain.getText()+"";
                    listenRebuild();
                    tvMain.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
                    return true;
                }
                else if (event.getAction() == MotionEvent.ACTION_UP){
                    mSpeechRecognizer.stopListening();
                    String temp = tvMain.getText().toString();
                    tvMain.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    if (!temp.equals("")&!temp.equals(sCurrentQUestion)){
                        Log.d("MyTag","temp '"+temp +"'");
                        SendMessage(temp);
                    }
                    else {
                    tvMain.setText(sCurrentQUestion);
                    }
                    return true;
                }


                return false;


            }
        });




        wbBrowse.setWebViewClient(new MyWebViewClient());

        wbBrowse.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);

        wbBrowse.getSettings().setLoadWithOverviewMode(true);
        wbBrowse.getSettings().setUseWideViewPort(true);
        wbBrowse.getSettings().setJavaScriptEnabled(true);
        wbBrowse.getSettings().setBuiltInZoomControls(true);
        //wbBrowse.loadUrl("https://yandex.ru");
        wbBrowse.setBackgroundColor(0);

      /*

        curl 82.202.192.186:10500 -X POST
        --silent --header
        'content-type: application/json; charset=utf-8' -d '
        {"user_id": 1, "text": "кредит"}' | ascii2uni -a U -q


        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://92.53.108.10:7888/api/v1/write";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //Toast.makeText(MainActivity.this, "Response is: " + response, Toast.LENGTH_SHORT).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "That didn't work!", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                String mess ="{\"message\": \"" + sMyMessage + "\", \"user\": \"123\"}";
                return mess.getBytes(Charset.forName("UTF-8"));
            }
        };
        queue.add(stringRequest);

*/

        am = (AudioManager)getSystemService(this.AUDIO_SERVICE);

        am.setStreamVolume(am.STREAM_MUSIC, 5, 5);



        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        //mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Слушаю вас..");
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 10L);
       // mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,500L);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,2);

        SpeechRecognitionListener listener = new SpeechRecognitionListener();
        mSpeechRecognizer.setRecognitionListener(listener);
        //mSpeechRecognizer.startListening(mSpeechRecognizerIntent);


        StartSpeak("Я готова");


    }


    private void speakWords(String speech) {
        HashMap<String, String> myHashAlarm = new HashMap<String, String>();
        myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_ALARM));
        myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "SOME MESSAGE");

        //TTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
        Log.d("TTS","There are "+ TTS.getVoices().size());




        /*List<Voice> list = new ArrayList<Voice>(TTS.getVoices());
        Voice value = list.get(0);

        TTS.setVoice(value);*/

        TTS.speak(speech,TextToSpeech.QUEUE_FLUSH,myHashAlarm);

    }


    private void StartSpeak(final String data) {

         TTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int initStatus) {
                if (initStatus == TextToSpeech.SUCCESS) {
/////////////////////////////////

                    TTS.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onDone(String utteranceId) {
                           //listenRebuild();
                            Log.e("TTS", "Есть onDone");
                            mainHandler.post(myRunnable);
                            //listenRebuild();
                            Log.e("TTS", "Есть onDone");
                        }

                        public void onUtteranceCompleted (String utteranceId){
                            //listenRebuild();
                            Log.e("TTS", "Есть onUtteranceCompleted");

                        }
                        @Override
                        public void onError(String utteranceId) {
                            Log.e("TTS", "Есть onError");
                        }


                        @Override
                        public void onStart(String utteranceId) {
                            Log.e("TTS", "Есть onStart");
                        }
                    });
////////////////////////////////////

                    Locale locale = new Locale("ru");

                    int result = TTS.setLanguage(locale);



                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Извините, этот язык не поддерживается");
                    } else {
                        Log.e("TTS", "Есть контакт");
                    }


                    //if(TTS.isLanguageAvailable(Locale.US)==TextToSpeech.LANG_AVAILABLE) TTS.setLanguage(Locale.US);
                    fPITCH = Float.parseFloat(getparam("PITCH"));
                    fSPEED = Float.parseFloat(getparam("SPEED"));

                    TTS.setPitch(fPITCH);
                    TTS.setSpeechRate(fSPEED);

                    // start speak
                    speakWords(data);
                }
                else if (initStatus == TextToSpeech.ERROR) {
                    Toast.makeText(getApplicationContext(), "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
                }
            }


        });
    }



}
