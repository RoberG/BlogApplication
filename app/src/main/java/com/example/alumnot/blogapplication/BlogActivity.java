package com.example.alumnot.blogapplication;

import android.app.ListActivity;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class BlogActivity extends ListActivity {

    public final static int NUMBER_OF_POST = 20;
    public final static String TAG = BlogActivity.class.getSimpleName();
    public final static String URL_JSON = "http://itvocationalteacher.blogspot.com/feeds/posts/default?alt=json";
    public static String[] mBlogPostTitles;
    public JSONObject mBlogData;
    public TextView texto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);
        //Resources resources=getResources();
        //mBlogPostTitles =resources.getStringArray(R.array.android_names);

        texto = (TextView) findViewById(R.id.textView);


        if (isNetworkAvaible()) {
            JSONObject mBlogData;
            GetBlogPostsTask getBlogPostsTask = new GetBlogPostsTask();
            getBlogPostsTask.execute();
        } else {
            Toast.makeText(this, "Paga a telefonica", Toast.LENGTH_LONG).show();
        }




        /*
        if(mBlogPostTitles !=null){

            texto.setVisibility(View.INVISIBLE);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mBlogPostTitles);
        setListAdapter(adapter);*/
    }


    public boolean isNetworkAvaible() {
        boolean isAvaible = false;
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            isAvaible = true;
        }
        return isAvaible;
    }

    private class GetBlogPostsTask extends AsyncTask<Object, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Object[] params) {

            JSONObject jsonResponse = null;
            int responseCode = -1;
            try {
                URL blogFeedURL = new URL(BlogActivity.URL_JSON);
                HttpURLConnection connection = (HttpURLConnection) blogFeedURL.openConnection();
                connection.connect();
                responseCode = connection.getResponseCode();
                if (responseCode == connection.HTTP_OK) {
                    InputStream is = connection.getInputStream();
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(is, "UTF-8"));

                    StringBuilder sb = new StringBuilder();
                    String cadena;


                    while ((cadena = br.readLine()) != null) {
                        sb.append(cadena);

                        if (!br.ready()) {
                            break;
                        }

                    }


                    String texto = sb.toString();
                    Log.e(BlogActivity.TAG, texto);
                    jsonResponse = new JSONObject(texto);

                } else {
                    Log.e(BlogActivity.TAG, "No se ha podido conectar: " + responseCode);
                }
                Log.i(BlogActivity.TAG, " Code: " + responseCode);
            } catch (MalformedURLException e) {
                Log.e(BlogActivity.TAG, "exception caught:", e);
            } catch (IOException e) {
                Log.e(BlogActivity.TAG, "exception caught:", e);
            } catch (Exception e) {
                Log.e(BlogActivity.TAG, "exception caught:", e);
            }

            //return "Code: "+responseCode;
            return jsonResponse;
        }
        @Override
        protected void onPostExecute(JSONObject result) {
            mBlogData = result;
            updateList();
        }

    }

    private void updateList() {
        if (mBlogData != null) {
            try {
                JSONObject feed = mBlogData.getJSONObject("feed");
                JSONArray jsonAentry = feed.getJSONArray("entry");
                mBlogPostTitles = new String[jsonAentry.length()];
                for (int i = 0; i < jsonAentry.length(); i++) {
                    JSONObject objeto = jsonAentry.getJSONObject(i);
                    JSONObject title = objeto.getJSONObject("title");
                    mBlogPostTitles[i] = Html.fromHtml(title.getString("$t")).toString();
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mBlogPostTitles);
                if (mBlogPostTitles != null) {
                    texto.setVisibility(View.INVISIBLE);
                    setListAdapter(adapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.blog, menu);
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
}
