package com.icechen1.omnible.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.*;

import java.io.File;


public class MainActivity extends ActionBarActivity {

    private BrowserFragment bf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bf = new BrowserFragment();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, bf)
                    .commit();
        }

        if(getSharedPreferences("Settings", 0).getBoolean("showWarning", true)){
            SharedPreferences settings = getSharedPreferences("Settings", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("showWarning",false);
            editor.commit();
            //Show a warning on first launch
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.warning_title))
                    .setMessage(getResources().getString(R.string.warning))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // close
                        }
                    })
                    .show();
        }
        //requestWindowFeature(Window.FEATURE_NO_TITLE); //No ActionBar
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.selector, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new SelectFragment())
                    .commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && bf.wv.canGoBack()) {
            bf.wv.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Executed when app is closing
     */
    @Override
    protected void onStop() {
        super.onStop();
        //Clear localStorage and other cached files for increased security/privacy
        File dir = getDir("webview",0);
        deleteFile(new File(dir, "Web Data"));
        deleteFile(new File(dir, "Web Data-journal"));
        deleteFile(new File(dir, "Local Storage"));
    }
    void deleteFile(File file){
        //Delete all files in directory if file is a directory
        if (file.isDirectory()) {
            String[] children = file.list();
            for (int i = 0; i < children.length; i++) {
                new File(file, children[i]).delete();
            }
        }
        Log.d("Omnible", file.getPath());
        boolean deleted = file.delete();
        Log.d("Omnible", "Deletion status: " + deleted);
    }
}
