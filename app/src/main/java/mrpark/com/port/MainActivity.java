package mrpark.com.port;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PortAdapter.EventPortClick {
    RecyclerView rcvPort;
    private PortAdapter portAdapter;
    private List<PortItem> portItemList;
    private PortItem portItemSelected;
    private static final int REQUEST_PER = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rcvPort = findViewById(R.id.rcv_port);
        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (portItemSelected == null) {
//                    Toast.makeText(MainActivity.this, "Please select device", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                Intent intent = new Intent(MainActivity.this, DevicesActivity.class);
                startActivity(intent);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(Constance.PERMISSION_EXTERNAL, REQUEST_PER);
        }
        initData();
        getPort();
    }

    private void initData() {
        portAdapter = new PortAdapter(portItemList, this);
        rcvPort.setLayoutManager(new LinearLayoutManager(this));
        rcvPort.setAdapter(portAdapter);

        SerialPortFinder serialPortFinder = new SerialPortFinder();
        String[] path = serialPortFinder.getAllDevicesPath();
        String[] device = serialPortFinder.getAllDevices();
    }

    @Override
    public void OnClickPort(PortItem portItem) {
        portItemSelected = portItem;
    }

    public void getPort() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PER:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                break;
        }
    }
}
