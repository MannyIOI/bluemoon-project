package org.tensorflow.lite.examples.detection;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextToSpeech textToSpeech;
    private static final int RECOGNIZE_SPEECH_REQUEST_CODE = 100;
    private boolean SHUTDOWN = false;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    @Override
    protected void onPause() {
        super.onPause();
        textToSpeech.shutdown();
    }

    @Override
    protected void onResume() {
        super.onResume();
//       Reinitialize the recognizer and tts engines upon resuming from background such as after openning the browser
        startReading();
    }



    public void startReading() {
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {

                    int lang = textToSpeech.setLanguage(Locale.ENGLISH);
                    String s = "What do you want?";
                    int speech = textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null);
                    Log.d("On beginning of speech", s);
                    startListening();
                }
            }
        });
    }

    public void startListening() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "am_ET");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "ምን ትፈልጋለህ?");
        try {
            startActivityForResult(intent, RECOGNIZE_SPEECH_REQUEST_CODE);

        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RECOGNIZE_SPEECH_REQUEST_CODE: {
                if (resultCode == RESULT_OK && data != null) {
                    //get text array from speech after talking
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //print the first text for now
                    Log.d("Speech text", result.get(0));
                    String[] text = result.get(0).split(" ");
                    if( text.length == 2 && text[1].equals("ፈልግ")){
                        Intent intent = new Intent(this, DetectorActivity.class);
                        String command = translate(text[0], "am-en");
                        //HashMap<String, String> map = new Gson().fromJson(command, new TypeToken<HashMap<String, String>>(){}.getType());
                        intent.putExtra("COMMAND",command.toLowerCase());
                        startActivity(intent);
                    }
//                    else{
//                        //startListening();
//                        textToSpeech.shutdown();
//                    }
                }
            }
            break;

        }
    }

    public String translate(String textToBeTranslated,String languagePair){

        TranslatorBackgroundTask translatorBackgroundTask= new TranslatorBackgroundTask(context);
        try {
            AsyncTask<String, Void, String> translationResult = translatorBackgroundTask.execute(textToBeTranslated, languagePair); // Returns the translated text as a String
            Log.d("Translation Result", translationResult.get()); // Logs the result in Android Monitor
            return translationResult.get();
        }
        catch(Exception ed){
            Log.d("Translation Result",ed.getMessage()); // Logs the result in Android Monitor
        }
        return "book";



    }
}
