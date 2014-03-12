package com.icechen1.omnible.app;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * A fragment containing the modified fragment_webview
 */
public class BrowserFragment extends Fragment {
    String mAddress;
    public WebView wv;
    // This will handle downloading.(Gingerbread+)
    DownloadManager manager;

    public BrowserFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_webview, container, false);
        SharedPreferences settings = getActivity().getSharedPreferences("Settings", 0);
        String selectedURL = settings.getString("URL", "-1");
        if(selectedURL.equals("-1")){
            //No School Settings found! Redirect to selection fragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new SelectFragment())
                    .commit();
        }else{
            mAddress = selectedURL;
        }
        manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE); //Get the download manager

        // This is where downloaded files will be written (/Download)
        final File destinationDir = new File (Environment.getExternalStorageDirectory(), "/Download/");
        if (!destinationDir.exists()) {
            destinationDir.mkdir(); //make the directory if it's not there
        }

        wv = (WebView) rootView.findViewById(R.id.webView);
        //Sets settings for the webview
        WebSettings webSettings = wv.getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setDomStorageEnabled(true); //enable session storage
        wv.setWebChromeClient(new WebChromeClient());
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url)
            {
                getActivity().setProgressBarIndeterminateVisibility(false);
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                getActivity().setProgressBarIndeterminateVisibility(true);
            }
        });

        //Handles document download events
        wv.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                //odd bugfix if omnivox sends us malformed links
                url = url.replace("https%3A//", "https://");

                Uri link = Uri.parse(url);
                // Make a new download request pointing to the url
                DownloadManager.Request request = new DownloadManager.Request(link);
                // Pass the session using cookies from the WebView
                request.addRequestHeader("Cookie", CookieManager.getInstance().getCookie(url));
                request.setMimeType(mimetype);
                // Use current time as file name for the destination
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String currentDateandTime = sdf.format(new Date());
                File destinationFile = new File (destinationDir, currentDateandTime);
                request.setDestinationUri(Uri.fromFile(destinationFile));
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //api 11+
                    request.allowScanningByMediaScanner(); //allow android to index the file
                }

                // Add it to the download manager
                manager.enqueue(request);
            }
        });
        //Spoof the user agent to load the mobile site
        webSettings.setUserAgentString("Mozilla/5.0 (iPhone; CPU iPhone OS 5_0 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3");
        wv.loadUrl(mAddress);
        return rootView;
    }
}
