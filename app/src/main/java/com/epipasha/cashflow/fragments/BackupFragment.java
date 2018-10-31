package com.epipasha.cashflow.fragments;

import static android.app.Activity.RESULT_OK;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.data.Backuper;
import com.epipasha.cashflow.viewmodel.BackupViewModel;
import com.epipasha.cashflow.viewmodel.ViewModelFactory;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.CreateFileActivityOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityOptions;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

public class BackupFragment extends Fragment {

    private static final String TAG = "drive-quickstart";
    protected static final int REQUEST_CODE_SIGN_IN = 0;
    protected static final int REQUEST_CODE_OPEN_ITEM = 1;
    private static final int REQUEST_CODE_CREATE_FILE = 2;

    private static final int REQUEST_CODE_FILE_BACKUP = 3;
    private static final int REQUEST_CODE_FILE_RESTORE = 4;

    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;
    private TaskCompletionSource<DriveId> mOpenItemTaskSource;

    private Button btnDriveShare, btnDriveRestore, btnConnect;

    private BackupViewModel model;

    public BackupFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_backup, container, false);

        model = ViewModelProviders.of(this, ViewModelFactory.getInstance(getActivity().getApplication())).get(BackupViewModel.class);

        Button bthFileShare = v.findViewById(R.id.btnFileShare);
        Button bthFileRestore = v.findViewById(R.id.btnFileRestore);
        btnDriveShare = v.findViewById(R.id.btnDriveShare);
        btnDriveRestore = v.findViewById(R.id.btnDriveRestore);
        btnConnect = v.findViewById(R.id.btnConnect);

        bthFileShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFileBackup(REQUEST_CODE_FILE_BACKUP);
            }
        });
        bthFileRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFileBackup(REQUEST_CODE_FILE_RESTORE);
            }
        });

        btnDriveShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DriveBackuper backuper = new DriveBackuper();
                backuper.execute();
            }
        });
        btnDriveRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadFileFromDrive();
            }
        });
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        setDriveVisibility(false);

        loadDriveClient();

        return v;
    }

    private void startFileBackup(int request){
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    request);
        } else {
            // permission has been granted, continue as usual
            startBackuping(request);
        }
    }

    private void startBackuping(int request){
        switch (request){
            case REQUEST_CODE_FILE_BACKUP: {
                model.fileBackup();
                break;
            }
            case REQUEST_CODE_FILE_RESTORE: {
                model.fileRestore();
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (grantResults.length == 0
                || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        startBackuping(requestCode);
    }


    private void loadDriveClient() {
        Set<Scope> requiredScopes = new HashSet<>(2);
        requiredScopes.add(Drive.SCOPE_FILE);
        requiredScopes.add(Drive.SCOPE_APPFOLDER);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (signInAccount != null && signInAccount.getGrantedScopes().containsAll(requiredScopes)) {
            initializeDriveClient(signInAccount);
        }
    }

    protected void signIn() {
           GoogleSignInOptions signInOptions =
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestScopes(Drive.SCOPE_FILE)
                            .requestScopes(Drive.SCOPE_APPFOLDER)
                            .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getActivity(), signInOptions);
            startActivityForResult(googleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);

    }

    private void downloadFileFromDrive(){

        pickTextFile()
                .addOnSuccessListener(getActivity(),
                        new OnSuccessListener<DriveId>() {
                            @Override
                            public void onSuccess(DriveId driveId) {
                                retrieveContents(driveId.asDriveFile());
                            }
                        })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "No file selected", e);
                    }
                });

    }

    private void createFileWithIntent(final String backup) {
        // [START create_file_with_intent]
        Task<DriveContents> createContentsTask = mDriveResourceClient.createContents();
        createContentsTask
                .continueWithTask(new Continuation<DriveContents, Task<IntentSender>>() {
                    @Override
                    public Task<IntentSender> then(@NonNull Task<DriveContents> task) {
                        DriveContents contents = task.getResult();
                        OutputStream outputStream = contents.getOutputStream();

                        try {
                            outputStream.write(backup.getBytes());
                        } catch (IOException e1) {
                            Log.i(TAG, "Unable to write file contents.");
                        }

                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                .setTitle("Cashflow backup")
                                .setMimeType("text/plain")
                                .setStarred(true)
                                .build();

                        CreateFileActivityOptions createOptions =
                                new CreateFileActivityOptions.Builder()
                                        .setInitialDriveContents(contents)
                                        .setInitialMetadata(changeSet)
                                        .build();
                        return mDriveClient.newCreateFileActivityIntentSender(createOptions);
                    }
                })
                .addOnSuccessListener(getActivity(),
                        new OnSuccessListener<IntentSender>() {
                            @Override
                            public void onSuccess(IntentSender intentSender) {
                                try {
                                    startIntentSenderForResult(
                                            intentSender, REQUEST_CODE_CREATE_FILE, null, 0, 0, 0, null);
                                } catch (IntentSender.SendIntentException e) {
                                    Log.e(TAG, "Unable to create file", e);
                                    //showMessage(getString(R.string.file_create_error));
                                    //finish();
                                }
                            }
                        })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Unable to create file", e);
                        //showMessage(getString(R.string.file_create_error));
                        //finish();
                    }
                });
        // [END create_file_with_intent]
    }

    protected Task<DriveId> pickTextFile() {
        OpenFileActivityOptions openOptions =
                new OpenFileActivityOptions.Builder()
                        .setSelectionFilter(Filters.eq(SearchableField.MIME_TYPE, "text/plain"))
                        .setActivityTitle("Select")
                        .build();

        mOpenItemTaskSource = new TaskCompletionSource<>();
        mDriveClient
                .newOpenFileActivityIntentSender(openOptions)
                .continueWith(new Continuation<IntentSender, Void>() {
                    @Override
                    public Void then(@NonNull Task<IntentSender> task) throws Exception {
                        startIntentSenderForResult(
                                task.getResult(), REQUEST_CODE_OPEN_ITEM, null, 0, 0, 0, null);
                        return null;
                    }
                });
        return mOpenItemTaskSource.getTask();
    }

    private void retrieveContents(DriveFile file) {
        // [START open_file]
        Task<DriveContents> openFileTask =
                mDriveResourceClient.openFile(file, DriveFile.MODE_READ_ONLY);
        // [END open_file]
        // [START read_contents]
        openFileTask
                .continueWithTask(new Continuation<DriveContents, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<DriveContents> task) {
                        DriveContents contents = task.getResult();
                        // Process contents...
                        // [START_EXCLUDE]
                        // [START read_as_string]
                        try {
                            BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(contents.getInputStream()));
                            StringBuilder builder = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                builder.append(line).append("\n");
                            }

                            String contentsAsString = builder.toString();

                            DriveRestorer restorer = new DriveRestorer();
                            restorer.execute(contentsAsString);

                        }catch (Exception e){

                        }
                        // [END read_as_string]
                        // [END_EXCLUDE]
                        // [START discard_contents]
                        Task<Void> discardTask = mDriveResourceClient.discardContents(contents);
                        // [END discard_contents]
                        return discardTask;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                        // [START_EXCLUDE]
                        Log.e(TAG, "Unable to read contents", e);
                        //showMessage(getString(R.string.read_failed));
                        //finish();
                        // [END_EXCLUDE]
                    }
                });
        // [END read_contents]
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode != RESULT_OK) {
                    Log.e(TAG, "Sign-in failed.");
                    return;
                }

                Task<GoogleSignInAccount> getAccountTask =
                        GoogleSignIn.getSignedInAccountFromIntent(data);
                if (getAccountTask.isSuccessful()) {
                    initializeDriveClient(getAccountTask.getResult());
                } else {
                    Log.e(TAG, "Sign-in failed.");
                }
                break;

            case REQUEST_CODE_OPEN_ITEM:
                if (resultCode == RESULT_OK) {
                    DriveId driveId = data.getParcelableExtra(
                            OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID);
                    mOpenItemTaskSource.setResult(driveId);
                } else {
                    mOpenItemTaskSource.setException(new RuntimeException("Unable to open file"));
                }
                break;
            case REQUEST_CODE_CREATE_FILE:{
                if (resultCode != RESULT_OK) {
                    Log.e(TAG, "Unable to create file");
                    //showMessage(getString(R.string.file_create_error));
                } else {
                    DriveId driveId =
                            data.getParcelableExtra(OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID);
                    //showMessage(getString(R.string.file_created, "File created with ID: " + driveId));
                }
                //finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initializeDriveClient(GoogleSignInAccount signInAccount) {
        mDriveClient = Drive.getDriveClient(getContext(), signInAccount);
        mDriveResourceClient = Drive.getDriveResourceClient(getContext(), signInAccount);

        setDriveVisibility(true);
    }

    private void setDriveVisibility(Boolean connected){
        btnConnect.setVisibility(connected ? View.GONE : View.VISIBLE);
        btnDriveRestore.setVisibility(connected ? View.VISIBLE : View.GONE);
        btnDriveShare.setVisibility(connected ? View.VISIBLE : View.GONE);
    }


    private class DriveBackuper extends AsyncTask<Void, Void, String>{

        @Override
        protected void onPostExecute(String backup) {
            createFileWithIntent(backup);
        }

        @Override
        protected String doInBackground(Void... voids) {
            return Backuper.backupRoomDb(getActivity());
        }
    }

    private class DriveRestorer extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... data) {
            Backuper.restoreRoomDb(getActivity(), data[0]);
            return null;
        }
    }

}
