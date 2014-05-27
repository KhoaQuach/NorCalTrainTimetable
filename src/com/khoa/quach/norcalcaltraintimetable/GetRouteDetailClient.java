package com.khoa.quach.norcalcaltraintimetable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.Toast;

public class GetRouteDetailClient extends AsyncTask<String, Integer, String> {
	
	public OnFinishedGetDetailData  listener;
	
    public GetRouteDetailClient (OnFinishedGetDetailData listener) 
    {
        this.listener = listener;     
    }
    
    @Override
    protected void onPreExecute()
    {
    	super.onPreExecute();
    };     
    
    @Override
    protected String doInBackground(String... params) {
        HttpResponse response = null;
        StringBuilder builder = new StringBuilder();
        
        try {
        	HttpGet httpget = new HttpGet(params[0]);
            HttpClient client = new DefaultHttpClient();
            response = client.execute(httpget); 
            
			StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(content));
                    String line;
                    while ((line = reader.readLine()) != null) {
                            builder.append(line);
                    }
            } 
            
            httpget = new HttpGet(params[1]);
            client = new DefaultHttpClient();
            response = client.execute(httpget); 
            
			statusLine = response.getStatusLine();
            statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(content));
                    String line;
                    while ((line = reader.readLine()) != null) {
                            builder.append(line);
                    }        
            } 
        } catch (IllegalStateException e) {
		} catch (IOException e) {
        } catch (Exception e) {                
        }

        return builder.toString();
    }
    
    @Override
    protected void onPostExecute(String xmlData)
    {
        super.onPostExecute(xmlData);
        listener.OnFinishedGetDetailData(xmlData);
    };
    
}
