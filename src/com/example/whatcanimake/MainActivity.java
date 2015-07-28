package com.example.whatcanimake;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends Activity {

	public final static String API_KEY = "?key=78ab22c7e9953d445e0c7d5a034f9442";
	public final static String API_URL = "http://food2fork.com/api/";
	public final static String API_GET = API_URL + "get" + API_KEY;
	public final static String API_SEARCH = API_URL + "search" + API_KEY;
	public final static String EXTRA_MESSAGE = "com.example.whatcanimake.MESSAGE";
    @Override
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    public void getRecipe(View view) {

        EditText queryText = (EditText) findViewById(R.id.query);
        String query = queryText.getText().toString();

        if( query != null && !query.isEmpty()) {
        	String urlString = null; 
    		try {
    			urlString = API_SEARCH + "&q=" + URLEncoder.encode(query, "UTF-8");
    		} catch (UnsupportedEncodingException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}

           new CallAPI().execute(urlString); 

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private class CallAPI extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String urlString=params[0];
            String resultToDisplay;
            InputStream in = null;
            
            // HTTP Get
            String topRecipe = getTopRecipe(urlString);
             
            // Simple logic to determine if the email is dangerous, invalid, or valid
            if (topRecipe != null ) {
                resultToDisplay = topRecipe;
            }
            else {
              resultToDisplay = "Exception Occured";
            }

            return resultToDisplay;
        }

        protected void onPostExecute(String result) {
        	  Intent intent = new Intent(getApplicationContext(), ResultActivity.class);

        	  intent.putExtra(EXTRA_MESSAGE, result);
        	  
        	  startActivity(intent);
        }
        
    } 
    
    
	public static String getTopRecipe(String urlString) {
		String resultToDisplay;
        InputStream in = null;
        String result;
        String strContents = null; 
        String topRecipe = null;

        // HTTP Get
        try {
          URL url = new URL(urlString);
          HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
          in = new BufferedInputStream(urlConnection.getInputStream());
          byte[] contents = new byte[1024];

          int bytesRead=0;
          ByteArrayOutputStream outString = new ByteArrayOutputStream();
          while( (bytesRead = in.read(contents)) != -1){ 
        	  outString.write(contents, 0, bytesRead);               
          }
          strContents = new String(outString.toByteArray());

        } catch (Exception e ) {
          System.out.println(e.getMessage());
          return e.getMessage();
        }
        
        try {
			JSONObject json = new JSONObject(strContents);
			topRecipe = json.getJSONArray("recipes").getJSONObject(0).toString();
//			System.out.print(topRecipe);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        return topRecipe;

	}

    

}


