package com.OrxtraDev.TitoApp.notification;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by hazem on 12/24/2016.
 * this class responsable to send notification to specific user with his token
 *
 */

public class DownstreamMessage extends AsyncTask<String,String,String>
{
//    AsyncResponse delegate = null;
    int responseCode;
    @Override
    protected String doInBackground(String... params)
    {
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
        String server_key = "key=AAAAXKG0oDE:APA91bFNrxuhH679CWPkDIEYwArMCNC60HH8joKC8kA1y0vExx0zPbwBtV7JC8TE27l2BXXoXelsHis9jWILu2W1-5TKj0a2GrW5K8jpZVVubv7OzZv2pbiETJarcZ_d8MltZiKgEYuc";
        String client_key;
        String content;
        String content_json_string;
        String user_id;
        String favor_id;


        try
        {
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            client_key = params[0];
            user_id=params[1];
            favor_id=params[2];


            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            httpURLConnection.setRequestProperty("Authorization", server_key);
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.connect();





            JSONObject notification_json_object = new JSONObject();
            try
            {
                notification_json_object.put("body","Someone book your trip tab to see who");
            }
            catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            JSONObject data=new JSONObject();

            try
            {

                data.put("sender_id",user_id);
                data.put("favor_id",favor_id);
            }
            catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            JSONObject content_json_object = new JSONObject();
            try
            {
                content_json_object.put("to",client_key);
//                content_json_object.put("notification",notification_json_object);
                content_json_object.put("data",data);

            }
            catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            content_json_string = content_json_object.toString();

            OutputStream output = httpURLConnection.getOutputStream();
            output.write(content_json_string.getBytes());
            output.flush();
            output.close();

            responseCode = httpURLConnection.getResponseCode();


        }
        catch (ProtocolException e)
        {

        }
        catch (IOException e)
        {

        }

        return "" + responseCode;
    }

    @Override
    protected void onPostExecute(String result)
    {
//        delegate.processFinish(result);
        Log.d("hazem",result);
    }
}
