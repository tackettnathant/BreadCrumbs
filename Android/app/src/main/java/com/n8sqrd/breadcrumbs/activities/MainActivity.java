package com.n8sqrd.breadcrumbs.activities;

import android.Manifest;
import android.app.DialogFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.n8sqrd.breadcrumbs.R;
import com.n8sqrd.breadcrumbs.fragments.CreatePathFragment;
import com.n8sqrd.breadcrumbs.persistence.BreadcrumbDatabase;
import com.n8sqrd.breadcrumbs.persistence.Crumb;
import com.n8sqrd.breadcrumbs.persistence.Path;
import com.n8sqrd.breadcrumbs.ui.CrumbViewModel;
import com.n8sqrd.breadcrumbs.ui.PathAdapter;
import com.n8sqrd.breadcrumbs.ui.PathTouchHelper;
import com.n8sqrd.breadcrumbs.ui.PathViewModel;
import com.n8sqrd.breadcrumbs.ui.ViewModelFactory;
import com.n8sqrd.breadcrumbs.utils.Constants;
import com.n8sqrd.breadcrumbs.utils.UtilityMethods;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements PathTouchHelper.PathTouchHelperListener, CreatePathFragment.CreatePathListener{
    private PathAdapter mPathAdapter;
    private RecyclerView mPathList;
    private ViewModelFactory mModelFactory;
    private PathViewModel mPathViewModel;
    private CrumbViewModel mCrumbViewModel;
    private List<Path> mPaths = new ArrayList<Path>();
    private CoordinatorLayout mainPageLayout;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;
    private Location lastLocation;
    private static final int  PERM_REQ_LOCATION = 1;
    public static final String LAT_KEY="latitude";
    public static final String LONG_KEY="longitude";
    private final CompositeDisposable mDisposable = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilityMethods.logStart("MainActivity:onCreate");
        populateFromState(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeClassFields();

        setupToolbar();
        setupPathRecyclerView();
        setupPathMonitor();
        setupFAB();
        setupPermissions();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            getLocation();
        }
        UtilityMethods.logEnd("MainActivity.onCreate");
    }

    private void setupFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.start_crumbing_fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                createPath();
            }

        });
    }

    private void setupPathRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mPathList = findViewById(R.id.path_list);
        mPathList.setLayoutManager(layoutManager);
        mPathList.setHasFixedSize(true);
        mPathList.setItemAnimator(new DefaultItemAnimator());
        mPathList.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mPathAdapter = new PathAdapter(this, mPathViewModel,mCrumbViewModel,mPathViewModel.mAllPaths);
        mPathList.setAdapter(mPathAdapter);

        ItemTouchHelper.SimpleCallback touchCallback = new PathTouchHelper(0,ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(touchCallback).attachToRecyclerView(mPathList);
    }

    private void initializeClassFields() {
        mainPageLayout = findViewById(R.id.main_page_layout);
        mModelFactory = new ViewModelFactory(BreadcrumbDatabase.getInstance(this));
        mPathViewModel = ViewModelProviders.of(this,mModelFactory).get(PathViewModel.class);
        mCrumbViewModel = ViewModelProviders.of(this,mModelFactory).get(CrumbViewModel.class);
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    protected void onResume() {
        UtilityMethods.logStart("MainActivity.onResume");
        super.onResume();
        UtilityMethods.logEnd("MainActivity.onResume");
    }

    private void setupPathMonitor() {
        Log.i(Constants.TAG, "MainActivity.onCreate: create subscription");
        mDisposable.add(
                mPathViewModel.getAllPaths()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(pp -> {
                    Log.i(Constants.TAG, "MainActivity.onCreate.subscribe: Received paths. Count: " + (pp==null?0:pp.size()));
                    if (mPathAdapter!=null) {
                        mPathAdapter.updateData(pp);
                    }
                })
        );
    }

    @Override
    protected void onPause() {
        UtilityMethods.logStart("MainActivity.onPause");
        super.onPause();
        UtilityMethods.logEnd("MainActivity.onPause");
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG,"in destroy");
        mDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        UtilityMethods.logStart("MainActivity.onStop");
        super.onStop();

        UtilityMethods.logEnd("MainActivity.onStop");
    }
    private void setupPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            UtilityMethods.logStart("MainActivity:setupPermissions");
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERM_REQ_LOCATION);
            UtilityMethods.logEnd("MainActivity:setupPermissions");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERM_REQ_LOCATION:{
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    //TODO: Disable location component?
                }
            }
        }
    }

    private void getLocation() {
        UtilityMethods.logStart("MainActivity:getLocation");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    UtilityMethods.logStart("MainActivity.setupPermissions.onSuccessListener");
                    currentLocation = location;
                    if (currentLocation!=null && lastLocation!=null){
                        float distance = lastLocation.distanceTo(currentLocation);
                        Log.i(Constants.TAG, "MainActivity.setupPermissions.onSuccess: Distance " + distance);
                        //TODO: Some prompt if different?
                    }
                    UtilityMethods.logEnd("MainActivity.setupPermissions.onSuccessListener");
                }
            });
        }
        UtilityMethods.logEnd("MainActivity:getLocation");
    }
    public void createPath() {
        CreatePathFragment frag = new CreatePathFragment();
        frag.show(getFragmentManager(),"create_path");
    }
    public void startCrumbing(Path path) {
        //TODO: Make sure there is a crumb
        UtilityMethods.logStart("MainActivity.startCumbing");
        Log.i(Constants.TAG, "MainActivity.startCrumbing: setting up insert observable");
        mDisposable.add(
            mPathViewModel.insertUpdatePath(path)
                    .andThen(mPathViewModel.insertUpdateCrumb(path.getCrumbs().get(0)))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(()->{
                Log.i(Constants.TAG,"MainActivity.startCrumbing.subscription: Path inserted.");
                if (path.getId() !=null) {
                    Log.i(Constants.TAG,"MainActivity.startCrumbing.subscription: Path ID:" + path.getId());
                    Intent intent = new Intent(this,BreadCrumbActivity.class);
                    intent.putExtra(getString(R.string.path_id),path.getId());
                    intent.putExtra(getString(R.string.new_path),true);
                    startActivity(intent);
                } else {
                    //TODO: Didn't save, do something
                    Toast.makeText(this,R.string.save_path_error,Toast.LENGTH_LONG).show();
                    Log.e(Constants.TAG,"MainActivity.startCrumbing.subscription: Path is null");
                }


            })
        );
        UtilityMethods.logEnd("MainActivity.startCumbing");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.breadcrumbs_main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.activity_settings) {
            Intent startSettingsActivity = new Intent(this,SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof PathAdapter.ViewHolder) {
            Path deletedPath;
            final int deletedIndex = viewHolder.getAdapterPosition();

            mDisposable.add(
                mPathViewModel.retrievePath(mPathViewModel.mAllPaths.get(viewHolder.getAdapterPosition()).getId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(path -> {
                        mPathViewModel.setDeletedPath(path);
                        mPathViewModel.deletePath(mPathViewModel.getDeletedPath().getId())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                    Log.i(Constants.TAG,"Removed path");
                                });
                    })
            );


            String name = mPathViewModel.mAllPaths.get(viewHolder.getAdapterPosition()).getName();
            mPathAdapter.removePath(deletedIndex);
            Snackbar snack = Snackbar.make(mainPageLayout,name + " removed.",Snackbar.LENGTH_LONG);
            snack.setAction("UNDO", new View.OnClickListener(){

                        @Override
                        public void onClick(View view) {
                            mPathAdapter.restorePath(mPathViewModel.getDeletedPath(),deletedIndex);
                        }
                    }

            );
            snack.setActionTextColor(Color.CYAN);
            snack.show();
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        //Create the path and send on the way
        UtilityMethods.logStart("MainActivity.onDialogPositiveClick");
        EditText edit = dialog.getDialog().findViewById(R.id.new_path_name);
        Date now = new Date();
        String name = edit.getText().toString();
        if (name==null || name.length()==0) {
            SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
            name = df.format(now);
        }
        EditText locEdit = dialog.getDialog().findViewById(R.id.starting_location);
        String locationName = locEdit.getText().toString();
        if (locationName==null || locationName.length()==0) {
            locationName = getString(R.string.no_location);
        }

        Crumb crumb = new Crumb();
        crumb.setLocation(locationName);
        crumb.setLatitude(currentLocation!=null?currentLocation.getLatitude():null);
        crumb.setLongitude(currentLocation!=null?currentLocation.getLongitude():null);
        crumb.setTimestamp(now);

        Path path = new Path();
        path.setName(name);
        path.setStartTime(now);
        path.setCurrentLocation(locationName);
        path.setLatitude(currentLocation!=null?currentLocation.getLatitude():null);
        path.setLongitude(currentLocation!=null?currentLocation.getLongitude():null);
        path.setCrumbs(new ArrayList<Crumb>(){
            {
                add(crumb);
            }
        });

        Log.i(Constants.TAG,"MainActivity.onDialogPositiveClick: path object constructed.");

        dialog.getDialog().cancel();
        startCrumbing(path);
        UtilityMethods.logEnd("MainActivity.onDialogPositiveClick");
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        //Just close the dialog
        dialog.getDialog().cancel();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        UtilityMethods.logStart("MainActivity.onSaveInstanceState");
        if (currentLocation!=null){
            outState.putDouble(LAT_KEY,currentLocation.getLatitude());
            outState.putDouble(LONG_KEY,currentLocation.getLongitude());
        }

        super.onSaveInstanceState(outState);
        UtilityMethods.logEnd("MainActivity.onSaveInstanceState");
    }

    private void populateFromState(Bundle state) {
        UtilityMethods.logStart("MainActivity.populateFromState");
        if (state!=null) {
            if (state.containsKey(LAT_KEY) && state.containsKey(LONG_KEY)) {
                lastLocation = new Location("");
                lastLocation.setLatitude(state.getDouble(LAT_KEY));
                lastLocation.setLongitude(state.getDouble(LONG_KEY));
            }
        }
        UtilityMethods.logEnd("MainActivity.populateFromState");
    }
}
