package com.example.thoma.pptdownloader;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PptAdapter.PptButtonAdapterListener {

    private ArrayList<String> urls;
    private ArrayList<String> buttonState;
    private PptAdapter pptAdapter;
    private RecyclerView pptRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pptRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        urls = new ArrayList<>();
        urls.add("https://drive.google.com/file/d/0B14qFEPVmToVRGlzQkxPSmx2bDQ/view");
        urls.add("https://drive.google.com/file/d/0B14qFEPVmToVRzhLN2gwWkdQbEk/view");
        urls.add("https://drive.google.com/file/d/0B14qFEPVmToVWXJDNWVGdlZEeDA/view");

        buttonState = new ArrayList<>();
        for(int ind=0; ind<urls.size(); ind++){
            buttonState.add("DOWNLOAD");
        }

        pptAdapter = new PptAdapter(urls, buttonState, this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());

        pptRecyclerView.setLayoutManager(layoutManager);
        pptRecyclerView.setHasFixedSize(true);
        pptRecyclerView.addItemDecoration(mDividerItemDecoration);
        pptRecyclerView.setAdapter(pptAdapter);

    }

    public void downloadOrOpen(int position){
        final int pos = position;
//        Toast.makeText(this, "HEY :"+urls.get(pos), Toast.LENGTH_SHORT).show();
        if(buttonState.get(pos)!="OPEN"){
//            SavePpt savePpt = new SavePpt();
//            savePpt.execute(urls.get(position));
//            savePpt.onFinish(new SavePpt.OnTaskCompleted() {
//                @Override
//                public void onTaskCompleted() {
//                    Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
//                    buttonState.set(pos,"OPEN");
//                    pptAdapter.setButtonState(buttonState);
//                }
//
//                @Override
//                public void onError() {
//                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
//                    buttonState.set(pos,"OPEN");
//                    pptAdapter.setButtonState(buttonState);
//                }
//            });
            String googleDocsUrl = "http://docs.google.com/viewer?url="+urls.get(position);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(googleDocsUrl ), "text/html");
            startActivity(intent);
            buttonState.set(pos,"OPEN");
            pptAdapter.setButtonState(buttonState);
        }
    }


    @Override
    public void pptButtonOnClick(int position) {
        if(buttonState.get(position)!="OPEN"){
            buttonState.set(position,"LOADING");
            pptAdapter.setButtonState(buttonState);
        }
        downloadOrOpen(position);
    }
}
