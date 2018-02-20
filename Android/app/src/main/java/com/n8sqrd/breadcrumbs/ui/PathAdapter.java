package com.n8sqrd.breadcrumbs.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.n8sqrd.breadcrumbs.R;
import com.n8sqrd.breadcrumbs.activities.BreadCrumbActivity;
import com.n8sqrd.breadcrumbs.persistence.Path;
import com.n8sqrd.breadcrumbs.utils.Constants;

import java.util.List;
import io.reactivex.Observable;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.n8sqrd.breadcrumbs.R.*;

/**
 * Created by ntackett on 1/6/2018.
 */

public class PathAdapter extends RecyclerView.Adapter<PathAdapter.ViewHolder> {
    private List<Path> paths;
    private Context context;
    private PathViewModel viewModel;
    private CrumbViewModel crumbViewModel;
    public PathAdapter(Context context,PathViewModel viewModel,CrumbViewModel crumbViewModel,List<Path> paths) {
        this.context = context;
        this.paths = paths;
        this.viewModel=viewModel;
        this.crumbViewModel=crumbViewModel;
    }

    public void updateData(List<Path> paths){
        this.paths=paths;
        this.notifyDataSetChanged();
    }

    public void removePath(int position) {
        if (position<=paths.size()) {
            paths.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void restorePath(Path path, int position) {
        viewModel.insertUpdatePath(path)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Log.i(Constants.TAG,"Path inserted. ID: " + path.getId());
                    Observable.fromIterable(path.getCrumbs())
                            .flatMapCompletable(crumb -> {
                                return crumbViewModel.insertUpdateCrumb(crumb);
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> {
                                paths.add(position,path);
                                notifyItemInserted(position);
                            });

                });
    }

    @Override
    public PathAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layout.path_item,parent,false);

        return new PathAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PathAdapter.ViewHolder holder, int position) {
        if (paths!=null && position<=paths.size()) {
            holder.bind(paths.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    if (paths.get(position)!=null) {
                        Intent intent = new Intent(context, BreadCrumbActivity.class);
                        intent.putExtra(view.getContext().getString(string.path_id), paths.get(position).getId());
                        context.startActivity(intent);
                    } else {
                        //TODO: Error
                    }
                }
            });
//            holder.itemView.setLongClickable(true);
//            holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
//
//                @Override
//                public boolean onLongClick(View view) {
//                    return false;
//                }
//            });
        }
    }

    @Override
    public int getItemCount() {
        return paths==null?0:paths.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView pathView;
        PathClickListener pathListener;
        RelativeLayout foreground,background;
        public ViewHolder(View itemView) {
            super(itemView);

            pathView = itemView.findViewById(id.path_name);
            foreground = itemView.findViewById(id.view_foreground);
            background = itemView.findViewById(id.view_background);
        }

        public void bind(Path path) {
            if (path!=null) {
                pathView.setText(path.getName());
            }
        }

        @Override
        public void onClick(View view) {
            pathListener.onPathClick(view,getLayoutPosition());
        }
        public void setPathListener(PathClickListener listener){
            this.pathListener=listener;
        }
    }
}
