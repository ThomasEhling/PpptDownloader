package com.example.thoma.pptdownloader;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.util.ArrayList;

/**
 * Created by thoma on 8/22/2017.
 */

public class PptAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<String> urls;
    private ArrayList<String> buttonState;

    private PptButtonAdapterListener onClickListener;

    public PptAdapter(ArrayList<String> urls, ArrayList<String> buttonState, PptButtonAdapterListener listener) {
        this.urls = urls;
        this.buttonState = buttonState;
        onClickListener = listener;
    }

    public interface PptButtonAdapterListener{
        void pptButtonOnClick(int position);
    }


    public class PpAdapterViewHolder extends RecyclerView.ViewHolder {

        private final TextView  pptTitle;
        private final Button pptButton;
        private final ProgressBar pptLoadingIndicator;

        public PpAdapterViewHolder(View view) {
            super(view);
            pptTitle  = (TextView) view.findViewById(R.id.ppt_title);
            pptButton = (Button) view.findViewById(R.id.ppt_action_button);
            pptLoadingIndicator = (ProgressBar) view.findViewById(R.id.ppt_loading_indicator);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem;
        layoutIdForListItem = R.layout.ppt_viewholder;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new PpAdapterViewHolder(view);
    }



    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final String url = urls.get(position);
        final PpAdapterViewHolder pptaAdapterViewHolder = (PpAdapterViewHolder) holder;

        pptaAdapterViewHolder.pptTitle.setText(url);

        if(buttonState.get(position)=="LOADING"){
            pptaAdapterViewHolder.pptButton.setVisibility(View.GONE);
            pptaAdapterViewHolder.pptLoadingIndicator.setVisibility(View.VISIBLE);
        }else{
            pptaAdapterViewHolder.pptButton.setVisibility(View.VISIBLE);
            pptaAdapterViewHolder.pptLoadingIndicator.setVisibility(View.GONE);
            pptaAdapterViewHolder.pptButton.setText(buttonState.get(position));
            pptaAdapterViewHolder.pptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.pptButtonOnClick(position);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if (null == urls) return 0;
        return urls.size();
    }

    public void clearData(){
        if (urls != null){
            urls.clear();
        }
    }

    public void setButtonState(ArrayList<String> list) {
        buttonState = list;
        notifyDataSetChanged();
    }

    public void setUrls(ArrayList<String> list) {
        urls = list;
        notifyDataSetChanged();
    }

}
