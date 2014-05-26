package com.khoa.quach.norcalcaltraintimetable;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class GetRouteDetailClient extends AsyncTask<String, Integer, HttpResponse> {

	private Context context;

    public GetRouteDetailClient (Context context) 
    {
        this.context = context;     
    }
    
    @Override
    protected HttpResponse doInBackground(String... params) {
        HttpResponse response = null;
        HttpGet httpget = new HttpGet(params[0]);

        try {
            HttpClient client = new DefaultHttpClient();
            response = client.execute(httpget); 

        } catch (ClientProtocolException e) {
            Toast.makeText(this.context, "Encountered error", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this.context, "Encountered error", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this.context, "Encountered error", Toast.LENGTH_SHORT).show();                  
        }

        return response;

    }
}
