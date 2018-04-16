package com.epipasha.cashflow;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.epipasha.cashflow.data.Backuper;
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

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;
    private TaskCompletionSource<DriveId> mOpenItemTaskSource;

    private Button btnDriveShare, btnDriveRestore, btnConnect;

    public BackupFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_backup, container, false);

        Button share = (Button) v.findViewById(R.id.btnFileShare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String data = Backuper.backupDb(getActivity());

                    File root = android.os.Environment.getExternalStorageDirectory();
                    File file = new File(root.getAbsolutePath(), "myData.txt");
                    FileOutputStream outputStream = new FileOutputStream(file);
                    outputStream.write(data.getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Button restore = (Button) v.findViewById(R.id.btnFileRestore);
        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    File root = android.os.Environment.getExternalStorageDirectory();
                    File file = new File(root.getAbsolutePath(), "myData.txt");
                    FileInputStream is = new FileInputStream(file);
                    int size = is.available();

                    byte[] buffer = new byte[size];

                    is.close();

                    Backuper.restoreDb(getActivity(), new String(buffer, "UTF-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnDriveShare = (Button)v.findViewById(R.id.btnDriveShare);
        btnDriveShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createFileWithIntent();
            }
        });

        btnDriveRestore = (Button)v.findViewById(R.id.btnDriveRestore);
        btnDriveRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadFileFromDrive();
            }
        });

        btnConnect = (Button)v.findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        btnConnect.setVisibility(View.VISIBLE);
        btnDriveRestore.setVisibility(View.GONE);
        btnDriveShare.setVisibility(View.GONE);

        loadDriveClient();

        return v;
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

    protected Task<DriveId> pickTextFile() {
        OpenFileActivityOptions openOptions =
                new OpenFileActivityOptions.Builder()
                        .setSelectionFilter(Filters.eq(SearchableField.MIME_TYPE, "text/plain"))
                        .setActivityTitle("Select")
                        .build();
        return pickItem(openOptions);
    }

    private Task<DriveId> pickItem(OpenFileActivityOptions openOptions) {
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

    private void retrieveContents(DriveFile file) {
        // [START open_file]
        Task<DriveContents> openFileTask =
                mDriveResourceClient.openFile(file, DriveFile.MODE_READ_ONLY);
        // [END open_file]
        // [START read_contents]
        openFileTask
                .continueWithTask(new Continuation<DriveContents, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
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

                            Backuper.restoreDb(getActivity(), contentsAsString);

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

    private void createFileWithIntent() {
        // [START create_file_with_intent]
        Task<DriveContents> createContentsTask = mDriveResourceClient.createContents();
        createContentsTask
                .continueWithTask(new Continuation<DriveContents, Task<IntentSender>>() {
                    @Override
                    public Task<IntentSender> then(@NonNull Task<DriveContents> task)
                            throws Exception {
                        DriveContents contents = task.getResult();
                        OutputStream outputStream = contents.getOutputStream();

                        // Write the bitmap data from it.
                        String str = null;
                        try {
                            str = Backuper.backupDb(getActivity());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //finish();
                        }

                        try {
                            outputStream.write(str.getBytes());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode != RESULT_OK) {
                    // Sign-in may fail or be cancelled by the user. For this sample, sign-in is
                    // required and is fatal. For apps where sign-in is optional, handle
                    // appropriately
                    Log.e(TAG, "Sign-in failed.");
                    //finish();
                    return;
                }

                Task<GoogleSignInAccount> getAccountTask =
                        GoogleSignIn.getSignedInAccountFromIntent(data);
                if (getAccountTask.isSuccessful()) {
                    initializeDriveClient(getAccountTask.getResult());
                } else {
                    Log.e(TAG, "Sign-in failed.");
                    //finish();
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

        btnConnect.setVisibility(View.GONE);
        btnDriveRestore.setVisibility(View.VISIBLE);
        btnDriveShare.setVisibility(View.VISIBLE);
    }

}
