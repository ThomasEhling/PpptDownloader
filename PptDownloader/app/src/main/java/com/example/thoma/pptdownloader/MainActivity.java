package com.example.thoma.pptdownloader;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PptAdapter.PptButtonAdapterListener {

    private ArrayList<String> urls;
    private ArrayList<String> buttonState;
    private ArrayList<Long>   downloadId;
    private PptAdapter        pptAdapter;
    private RecyclerView      pptRecyclerView;
    private DownloadManager   downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //this array list contain all the urls
        urls = new ArrayList<>();
        initUrls();

        //for each url in the url array, we create a String and a id in the corresponding arrays
        buttonState = new ArrayList<>();
        downloadId  = new ArrayList<>();
        for(int ind=0; ind<urls.size(); ind++){
            buttonState.add("DOWNLOAD");
            downloadId.add(null);
        }

        //Creation and initialisation of the Adapater and recycler view
        pptAdapter = new PptAdapter(urls, buttonState, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        pptRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        pptRecyclerView.setLayoutManager(layoutManager);
        pptRecyclerView.setHasFixedSize(true);
        pptRecyclerView.addItemDecoration(mDividerItemDecoration);
        pptRecyclerView.setAdapter(pptAdapter);

        //Creation of a downloadManager
        downloadManager=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);

        //we add a registerReceiver to check when the download is fnished
        registerReceiver(onComplete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private void initUrls(){
        //TODO : Change here with a function to fetch the urls
        urls.add("http://www.engr.iupui.edu/~stevens/cpt106/datafiles/exploringpowerpoint/chapter%202%20practice%205%20-%20hidden%20slides%20-%20a%20card%20trick.ppt");
        urls.add("https://doc.lagout.org/operating%20system%20/android/Android.ppt");
        urls.add("https://www.google.com.sg/url?sa=t&rct=j&q=&esrc=s&source=web&cd=7&cad=rja&uact=8&ved=0ahUKEwi0q_LrwPHVAhXJLo8KHXzuC5gQFghXMAY&url=http%3A%2F%2Fpeople.apache.org%2F~tonywu%2FRoadshow10%2Ftrack2%2FThe%2520introduction%2520to%2520Android.ppt&usg=AFQjCNGzqo3XT5xC0l-fXoCwhYfYPxKNig");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onComplete);
    }

    BroadcastReceiver onComplete=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            //This function is call when a download is complete
            Toast.makeText(MainActivity.this, "complete", Toast.LENGTH_SHORT).show();

            //we fetch the id of the download request that just finished
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            //we get the index of the URl and update the state of the button
            int ind = downloadId.indexOf(referenceId);
            buttonState.set(ind, "OPEN");
            pptAdapter.setButtonState(buttonState);

        }
    };

    public void startDownload(int position, String mid) {

        String url = urls.get(position);

        //we create a path for the file
        final File file = new File(this.getExternalFilesDir(null), mid);

        //if the file alreadyexist, we delete it to doxnload it again
        if (file.exists()) { file.delete(); }

        //we request a download for the file
        final DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle(mid);
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalFilesDir(this, null, mid);

        //we enqueue the download request and get a unique reference
        final long downloadReference = downloadManager.enqueue(request);

        //We store this reference in our Id array at the position of the url
        downloadId.set(position, downloadReference);

    }

    public void openFile(String mid, int position) {

        if(buttonState.indexOf("LOADING")==-1){
            //I retrieve the file
            final File releaseFile = new File(this.getExternalFilesDir(null), mid );

            if(releaseFile.exists()){
                //If it exist we open it
                Intent intent = new Intent(Intent.ACTION_VIEW);
                //TODO: change the data type to handle other doculent than powerpoint
                intent.setDataAndType(Uri.fromFile(releaseFile), "application/vnd.ms-powerpoint");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }else{
                //if the file isnt found, we change the button to download it again
                Toast.makeText(this, "FILE NOT FOUND", Toast.LENGTH_SHORT).show();
                buttonState.set(position, "DOWNLOAD");
                pptAdapter.setButtonState(buttonState);
            }
        }else{
            //A download is in progress we can't open a file right now
            Toast.makeText(this, "Wait for all download to finish", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void pptButtonOnClick(int position) {
        //TODO : change the name of the file according to the URL, it has to be unic
        String mid = "ppt"+position+".ppt";

        //Here is the code that handle the download process
        if(buttonState.get(position)=="DOWNLOAD"){
            startDownload(position, mid);
            buttonState.set(position,"LOADING");
            pptAdapter.setButtonState(buttonState);
        }else{
            openFile(mid,position);
        }
    }
}
