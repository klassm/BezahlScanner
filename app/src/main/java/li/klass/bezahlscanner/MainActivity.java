package li.klass.bezahlscanner;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_CODE_RESOLUTION = 1;
    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 1;
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient googleApiClient;
    private DriveFileAccessor driveFileAccessor;
    private QrCodeTextParser qrCodeTextParser = new QrCodeTextParser(new DateTimeProvider());

    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton scanButton = findViewById(R.id.scanAction);
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
                driveFileAccessor.addPayment(payment);
                updatePaymentsView();
                msg = String.format(getString(R.string.payment_added_message), payment.getName());
            } else {
                msg = getString(R.string.could_not_find_payment_in_qr_code);
            }
            Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            if (result.isSuccess()) {
                Log.e(TAG, "sign in success");
                if (googleApiClient != null) {
                    connect();
                }
            } else {
                Log.e(TAG, result.getStatus().toString());
            }
        }
    }

    private void connect() {
        Log.e(TAG, "sign in success");
        driveFileAccessor = new DriveFileAccessor(googleApiClient);
        driveFileAccessor.start(new DriveFileAccessor.InitDone() {
            @Override
            public void onInitDone() {
                updatePaymentsView();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient == null) {
            GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestScopes(Drive.SCOPE_FILE, Drive.SCOPE_APPFOLDER)
                    .build();

            googleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, 0, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                    .addApi(Drive.API)
                    .build();

            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.stopAutoManage(this);
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    private void updatePaymentsView() {
        ((PaymentAdapter) ((ListView) findViewById(R.id.payments)).getAdapter()).setData(driveFileAccessor.getPayments());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.e(TAG, result.toString());
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
