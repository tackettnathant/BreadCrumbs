package com.n8sqrd.breadcrumbs.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;


import com.n8sqrd.breadcrumbs.persistence.Crumb;
import com.n8sqrd.breadcrumbs.persistence.Path;
import com.n8sqrd.breadcrumbs.utils.UtilityMethods;
import com.squareup.picasso.Picasso;


import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.n8sqrd.breadcrumbs.R.*;

/**
 * Created by ntackett on 2/10/2018.
 */

public class CrumbAdapter extends RecyclerView.Adapter<CrumbAdapter.ViewHolder> {
    private List<Crumb> crumbs;
    private Context context;
    private CrumbViewModel viewModel;

    public CrumbAdapter(Context context,CrumbViewModel viewModel,Path path) {
        this.context=context;
        this.viewModel=viewModel;
        this.crumbs=path==null?new ArrayList<Crumb>():path.getCrumbs();
    }

    public void updateData(List<Crumb> crumbs){
        this.crumbs=crumbs;
        this.notifyDataSetChanged();
    }
    @Override
    public CrumbAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context ctx = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(layout.crumb,parent,false);

        return new CrumbAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (crumbs!=null && crumbs.size()>0&& position<=crumbs.size()) {
            holder.bind(crumbs.get(position),this.context);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO
                }
            });
        }

    }


    @Override
    public int getItemCount() {
        return crumbs==null?0:crumbs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView crumbText;
        TextView crumbTimestamp;
        ImageView crumbImage;
        private final SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
        public ViewHolder(View itemView) {
            super(itemView);
            crumbText = itemView.findViewById(id.crumb_text);
            crumbImage = itemView.findViewById(id.crumb_image);
            crumbTimestamp = itemView.findViewById(id.crumb_timestamp);
            int measuredWidth=0;
            //View.MeasureSpec.makeMeasureSpec(measuredWidth,View.MeasureSpec.EXACTLY);
        }

        public void bind(Crumb crumb,Context context){
            StringBuilder sb = new StringBuilder();
            sb.append(crumb.getLocation()==null?"Somewhere...":crumb.getLocation());
            crumbText.setText(sb.toString());
            crumbTimestamp.setText(df.format(crumb.getTimestamp()));
            if (crumb.getURI()!=null) {
                Uri uri = Uri.parse(crumb.getURI());
                Picasso.with(context).load(uri).into(crumbImage);
//                try (InputStream image = context.getContentResolver().openInputStream(uri)) {
//
//                    Bitmap bm = BitmapFactory.decodeStream(image);
//                    //UtilityMethods.scaleAndRotateImage(uri.getPath());
//                    int ori = UtilityMethods.getOrientation(context,uri);
//                    //UtilityMethods.setScaledImage(crumbImage,image);
//                    crumbImage.setImageBitmap(bm);
//                 } catch (Exception e) {
//                    e.printStackTrace();
//                }
                //crumbImage.setImageURI(uri);
            }
        }

        @Override
        public void onClick(View view) {

        }
    }
}
