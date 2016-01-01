package li.klass.bezahlscanner;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_CODE_RESOLUTION = 1;
    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 1;

    private GoogleApiClient googleApiClient;
    private DriveFileAccessor driveFileAccessor;
    private QrCodeTextParser qrCodeTextParser = new QrCodeTextParser(new DateTimeProvider());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton scanButton = (FloatingActionButton) findViewById(R.id.scanAction);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.initiateScan();
            }
        });

        ((ListView) findViewById(R.id.payments)).setAdapter(new PaymentAdapter(this));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        String msg;
        if (scanResult != null && driveFileAccessor != null) {
            Payment payment = qrCodeTextParser.parse(scanResult.getContents());
            if (payment != null) {
                driveFileAccessor.addZahlung(payment);
                updatePaymentsView();
                msg = String.format(getString(R.string.payment_added_message), payment.getName());
            } else {
                msg = getString(R.string.could_not_find_payment_in_qr_code);
            }
            Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else if (requestCode == RESOLVE_CONNECTION_REQUEST_CODE && resultCode == RESULT_OK) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (googleApiClient== null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addScope(Drive.SCOPE_APPFOLDER) // required for App Folder sample
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        googleApiClient.connect();
    }

    @Override
    protected void onPause() {
        driveFileAccessor.release();
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    public void onConnected(Bundle bundle) {
        driveFileAccessor = new DriveFileAccessor(googleApiClient);
        driveFileAccessor.start(new DriveFileAccessor.InitDone() {
            @Override
            public void onInitDone() {
                updatePaymentsView();
            }
        });
    }

    private void updatePaymentsView() {
        ((PaymentAdapter) ((ListView) findViewById(R.id.payments)).getAdapter()).setData(driveFileAccessor.getPayments());
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        if (result.hasResolution()) {
            try {
                result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
            } catch (IntentSender.SendIntentException e) {
                showError();
            }
        } else {
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
        }
    }

    private void showError() {
        Snackbar.make(findViewById(android.R.id.content), R.string.google_drive_connection_failed, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
