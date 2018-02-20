package com.n8sqrd.breadcrumbs.activities;

import android.Manifest;
import android.app.DialogFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.n8sqrd.breadcrumbs.R;
import com.n8sqrd.breadcrumbs.fragments.AddCrumbFragment;
import com.n8sqrd.breadcrumbs.persistence.BreadcrumbDatabase;
import com.n8sqrd.breadcrumbs.persistence.Crumb;
import com.n8sqrd.breadcrumbs.persistence.Path;
import com.n8sqrd.breadcrumbs.ui.CrumbAdapter;
import com.n8sqrd.breadcrumbs.ui.CrumbViewModel;
import com.n8sqrd.breadcrumbs.ui.PathViewModel;
import com.n8sqrd.breadcrumbs.ui.ViewModelFactory;
import com.n8sqrd.breadcrumbs.utils.Constants;
import com.n8sqrd.breadcrumbs.utils.UtilityMethods;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class BreadCrumbActivity extends AppCompatActivity implements AddCrumbFragment.AddCrumbListener {
    private PathViewModel mPathViewModel;
    private CrumbViewModel mCrumbViewModel;
    private CrumbAdapter mCrumbAdapter;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private String mPhotoPath;
    private RecyclerView mCrumbList;
    static final int REQUEST_TAKE_PHOTO = 1;
    private ViewModelFactory mModelFactory;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location mCurrentLocation;
    @Override
    protected void onStart() {
        UtilityMethods.logStart("BreadCrumbActivity.onStart");
        super.onStart();

        UtilityMethods.logEnd("BreadCrumbActivity.onStart");

        //getSupportActionBar().setTitle(title);
    }
    private void setupFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_crumb_fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                addCrumbClicked();
            }

        });
    }


    private void addCrumbClicked(){
        //Toast.makeText(this,"Clicked",Toast.LENGTH_LONG).show();
        AddCrumbFragment fragment = new AddCrumbFragment();

        /*
        1. Get location of last crumb
        2. If close to the old location, pre-fill with old location name
        3. Randomly choose a prompt... from the randomly sorted list. Yo Dawg...
        4. Set the prompt
         */
        Crumb lastCrumb = retrieveLastCrumb();
        if (lastCrumb!=null && mCurrentLocation!=null && lastCrumb.getLatitude()!=null && lastCrumb.getLongitude()!=null){
            Location lastLocation = new Location("");
            lastLocation.setLongitude(lastCrumb.getLongitude());
            lastLocation.setLatitude(lastCrumb.getLatitude());

            float distance = lastLocation.distanceTo(mCurrentLocation);
            Log.i(Constants.TAG,"Distance from last location: " + distance + "m");
            if (distance<Constants.MIN_MOVE_DISTANCE){
                fragment.mLocationText=lastCrumb.getLocation();
            }
        }

        if (mPathViewModel.mAllPrompts!=null && !mPathViewModel.mAllPrompts.isEmpty()){
            int idx = (int)Math.floor(Math.random() * mPathViewModel.mAllPrompts.size());
            fragment.mPrompt=mPathViewModel.mAllPrompts.get(idx).getText();
        } else {
            fragment.mPrompt=getString(R.string.default_prompt);
        }


        fragment.show(getFragmentManager(),"add_crumb");
    }

    private Crumb retrieveLastCrumb() {
        return mPathViewModel.getCurrentPath()!=null
                && mPathViewModel.getCurrentPath().getCrumbs()!=null
                && !mPathViewModel.getCurrentPath().getCrumbs().isEmpty()
                ?
                mPathViewModel.getCurrentPath().getCrumbs().get(mPathViewModel.getCurrentPath().getCrumbs().size()-1)
                :
                null;
    }

    private void getLocation() {
        UtilityMethods.logStart("MainActivity:getLocation");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    UtilityMethods.logStart("MainActivity.setupPermissions.onSuccessListener");
                    mCurrentLocation = location;
                    UtilityMethods.logEnd("MainActivity.setupPermissions.onSuccessListener");
                }
            });
        }
        UtilityMethods.logEnd("MainActivity:getLocation");
    }
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.path_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposable.clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        UtilityMethods.logStart("BreadCrumbActivity.onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bread_crumb);
        initialize();
        setupToolbar();
        setupCrumbRecyclerView();
        setupFAB();
        int width = findViewById(R.id.path_layout).getWidth();

//        if (savedInstanceState!=null && savedInstanceState.containsKey(getString(R.string.path_extra))){
//            mPath = (Path)savedInstanceState.getParcelable(getString(R.string.path_extra));
//            Log.i(Constants.TAG,"Retrieved path from savedInstanceState. ID: " + mPath.getId());
//        } else if(getIntent().getExtras()!=null && getIntent().getExtras().containsKey(getString(R.string.path_extra))) {
//            mPath = (Path)getIntent().getExtras().getParcelable(getString(R.string.path_extra));
//
//                    //.get(getString(R.string.path_extra));
//            Log.i(Constants.TAG,"Retrieved path from extras. ID: " + mPath.getId());
//            Log.i(Constants.TAG, "Crumb ID: " + mPath.getCrumbs().get(0).getId() + ". Crumb path id: " + mPath.getCrumbs().get(0).getPathId());
//        }else

        Long pathId;
        if(getIntent().getExtras()!=null && getIntent().getExtras().containsKey(getString(R.string.path_id))) {
            pathId=getIntent().getExtras().getLong(getString(R.string.path_id));
            Log.i(Constants.TAG,"Retrieved path id from extras. ID: " + pathId);
        } else if (savedInstanceState!=null && savedInstanceState.containsKey(getString(R.string.path_id))){
            pathId = savedInstanceState.getLong(getString(R.string.path_id));
            Log.i(Constants.TAG,"Retrieved path id from savedInstanceState. ID: " + pathId);
        } else {
            finish();
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            return;
        }

        mPathViewModel.setCurrentPath(new Path(){
            {this.setId(pathId);}
        });
        setupPathListener();

        initializePrompts();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            getLocation();
        }
    }

    private void setupPathListener() {
        Log.i(Constants.TAG,"Retrieving from path id. ID: " + mPathViewModel.getCurrentPath().getId());
        mDisposable.add(
            mPathViewModel.retrievePath(mPathViewModel.getCurrentPath().getId()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(path -> {
                Log.i(Constants.TAG,"BreadCrumbActivity.onCreate.retrievePath: Path retrieved: " +(path!=null?path.getId():"NO PATH"));
                mPathViewModel.setCurrentPath(path);
                getSupportActionBar().setTitle(mPathViewModel.getCurrentPath().getName()!=null?mPathViewModel.getCurrentPath().getName():"NEW");
                mCrumbAdapter.updateData(mPathViewModel.getCurrentPath().getCrumbs());
                if (getIntent().getExtras()!=null && getIntent().getExtras().containsKey(getString(R.string.new_path))
                        && getIntent().getExtras().getBoolean(getString(R.string.new_path))){
                    handleNewPath();
                }

            })
        );
    }

    private void setupCrumbRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mCrumbList = findViewById(R.id.crumb_list);
        mCrumbList.setLayoutManager(layoutManager);
        mCrumbAdapter = new CrumbAdapter(this,mCrumbViewModel,mPathViewModel!=null?mPathViewModel.getCurrentPath():null);
        mCrumbList.setAdapter(mCrumbAdapter);
    }

    private void initialize() {
        mModelFactory = new ViewModelFactory(BreadcrumbDatabase.getInstance(this));
        mPathViewModel = ViewModelProviders.of(this,mModelFactory).get(PathViewModel.class);
        mCrumbViewModel = ViewModelProviders.of(this,mModelFactory).get(CrumbViewModel.class);
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
    }

    private void handleNewPath() {
        if (getIntent().getExtras().containsKey(getString(R.string.new_path))
                && getIntent().getExtras().getBoolean(getString(R.string.new_path))){
            Log.i(Constants.TAG, "Setting working crumb ID: " + mPathViewModel.getCurrentPath().getCrumbs().get(0).getId());
            mCrumbViewModel.setWorkingCrumb(mPathViewModel.getCurrentPath().getCrumbs().get(0));
            Log.i(Constants.TAG,"New path. Take a picture");
            dispatchTakePictureIntent(mCrumbViewModel.getWorkingCrumb());
        }
    }

    private void initializePrompts() {
        mDisposable.add(
        mPathViewModel.getAllPrompts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(prompts -> {
                    Log.i(Constants.TAG,"Retrieved prompts");
                })
        );
    }





    private File createImageFile(Crumb crumb) throws IOException {
        // Create an image file name
        UtilityMethods.logStart("BreadCrumbActivity.createImageFile");
        Log.i(Constants.TAG,"Creating image for crumb ID: " + crumb.getId());
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "CRUMB_"+crumb.getPathId() + "_"+ crumb.getId()+ "_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mPhotoPath = image.getAbsolutePath();
        Log.i(Constants.TAG,"Image path: " + image.getAbsolutePath());
        UtilityMethods.logEnd("BreadCrumbActivity.createImageFile");
        return image;

    }


    private void dispatchTakePictureIntent(Crumb crumb) {
        UtilityMethods.logStart("BreadCrumbActivity.dispatchTakePictureIntent");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile(crumb);
            } catch (IOException ex) {
                // Error occurred while creating the File
                //TODO
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.n8sqrd.breadcrumbs.fileprovider",
                        photoFile);
                Log.i(Constants.TAG,"Photo URI: " + photoURI.toString());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }
        }
        UtilityMethods.logEnd("BreadCrumbActivity.dispatchTakePictureIntent");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        UtilityMethods.logStart("BreadCrumbActivity.onActivityResult");
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Log.i(Constants.TAG,"Photo taken successfully.");
            getIntent().putExtra(getString(R.string.new_path),false);
            if (mCrumbViewModel.getWorkingCrumb()!=null && mPhotoPath!=null){
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(mPhotoPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                   File file = new File(mPhotoPath);

                //Uri uri = Uri.fromFile(file);
                Uri uri = FileProvider.getUriForFile(this,"com.n8sqrd.breadcrumbs.fileprovider",file);
                mCrumbViewModel.getWorkingCrumb().setURI(uri.toString());
                Log.i(Constants.TAG,"Starting update path subscription");
                Log.i(Constants.TAG, "Updating image for crumb. ID: " + mCrumbViewModel.getWorkingCrumb().getId() +  ". Path ID: " + mCrumbViewModel.getWorkingCrumb().getPathId());
                mDisposable.add(
                mCrumbViewModel.insertUpdateCrumb(mCrumbViewModel.getWorkingCrumb()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            Log.i(Constants.TAG, "Crumb updated");
                            mCrumbAdapter.updateData(mPathViewModel.getCurrentPath().getCrumbs());
                        } )
                );
            }
        }
        UtilityMethods.logEnd("BreadCrumbActivity.onActivityResult");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        //Create crumb and launch
        EditText locText = (EditText)dialog.getDialog().findViewById(R.id.add_crumb_location);
        Crumb crumb = new Crumb();
        if (locText!=null && locText.getText()!=null) {
            crumb.setLocation(locText.getText().toString());
        } else {
            crumb.setLocation(getString(R.string.no_location));
        }
        TextView textView = (TextView)dialog.getDialog().findViewById(R.id.crumb_prompt);
        if (textView!=null && textView.getText()!=null) {
            crumb.setNotes(textView.getText().toString());
        }
        if (mCurrentLocation!=null) {
            crumb.setLatitude(mCurrentLocation.getLatitude());
            crumb.setLongitude(mCurrentLocation.getLongitude());
        }

        mCrumbViewModel.insertUpdateCrumb(crumb)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    mCrumbViewModel.setWorkingCrumb(crumb);
                    mPathViewModel.getCurrentPath().getCrumbs().add(crumb);
                    mCrumbAdapter.updateData(mPathViewModel.getCurrentPath().getCrumbs());
                    dispatchTakePictureIntent(mCrumbViewModel.getWorkingCrumb());
                    dialog.getDialog().cancel();
                });


    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }
}
