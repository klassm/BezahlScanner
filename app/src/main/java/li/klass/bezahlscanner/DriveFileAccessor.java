package li.klass.bezahlscanner;

import android.support.annotation.NonNull;

import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.util.List;

public class DriveFileAccessor {

    public static final String FILE_TITLE = "bezahlScannerData";

    private final GoogleApiClient googleApiClient;
    private DriveId driveId;
    private DataHolder dataHolder = new DataHolder("");
    private static final String TAG = DriveFileAccessor.class.getName();

    public DriveFileAccessor(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }

    public void start(InitDone initDone) {
        Log.e(TAG, "start() - starting accessor");
        Query query = new Query.Builder().addFilter(Filters.eq(SearchableField.TITLE, FILE_TITLE)).build();
        Drive.DriveApi.query(googleApiClient, query)
                .setResultCallback(queryCallback(initDone));
    }

    @NonNull
    private ResultCallback<DriveApi.MetadataBufferResult> queryCallback(final InitDone initDone) {
        return new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(@NonNull final DriveApi.MetadataBufferResult result) {
                if (!result.getStatus().isSuccess()) {
                    Log.e(TAG, "queryCallback() - query result is error");
                    return;
                }

                if (result.getMetadataBuffer().getCount() == 0) {
                    Log.i(TAG, "queryCallback() - creating new file");
                    createNewFile(new FileLoadedCallback() {

                        @Override
                        public void onLoaded(DriveId driveId) {
                            loadFileContent(driveId);
                            initDone.onInitDone();
                            result.release();
                        }
                    });
                } else {
                    Log.i(TAG, "queryCallback() - using existing file");
                    loadFileContent(result.getMetadataBuffer().get(0).getDriveId());
                    result.release();
                    initDone.onInitDone();
                }

            }
        };
    }

    private void createNewFile(final FileLoadedCallback callback) {

        Drive.DriveApi.newDriveContents(googleApiClient)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                    @Override
                    public void onResult(@NonNull DriveApi.DriveContentsResult result) {
                        if (!result.getStatus().isSuccess()) {
                            return;
                        }

                        final DriveContents driveContents = result.getDriveContents();

                        // Perform I/O off the UI thread.
                        new Thread() {
                            @Override
                            public void run() {
                                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                        .setTitle(FILE_TITLE)
                                        .setMimeType("text/csv")
                                        .setStarred(false).build();

                                // create a file on root folder
                                Drive.DriveApi.getRootFolder(googleApiClient)
                                        .createFile(googleApiClient, changeSet, driveContents)
                                        .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                                            @Override
                                            public void onResult(@NonNull DriveFolder.DriveFileResult driveFileResult) {
                                                if (!driveFileResult.getStatus().isSuccess()) {
                                                    return;
                                                }
                                                callback.onLoaded(driveFileResult.getDriveFile().getDriveId());
                                            }
                                        });
                            }
                        }.start();
                    }
                });
    }

    private void loadFileContent(DriveId driveId) {
        this.driveId = driveId;

        try {
            String content = new LoadContentsAsyncTask(googleApiClient).execute(driveId.asDriveFile()).get();
            dataHolder = new DataHolder(content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Payment> getPayments() {
        return dataHolder.getPayments();
    }

    public void addPayment(Payment payment) {
        dataHolder.addPayment(payment);

        new WriteContentsAsyncTask(googleApiClient, dataHolder.toCsv()).execute(driveId.asDriveFile());
    }

    private interface FileLoadedCallback {
        void onLoaded(DriveId driveId);
    }

    public interface InitDone {
        void onInitDone();
    }
}
