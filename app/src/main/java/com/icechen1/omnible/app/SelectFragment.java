package com.icechen1.omnible.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * A fragment containing the school selection screen
 */
public class SelectFragment extends ListFragment {

    public SelectFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, SchoolValues.names);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // save the url and launch the fragment
        int idx = position;
        SharedPreferences settings = getActivity().getSharedPreferences("Settings", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("URL", SchoolValues.urls[idx]);
        editor.commit();
        //Redirect
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new BrowserFragment(),"browser")
                .commit();
    }
}