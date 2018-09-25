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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public class MainActivity extends AppCompatActivity implements PortAdapter.EventPortClick {
    RecyclerView rcvPort;
    private PortAdapter portAdapter;
    private List<PortItem> portItemList = new ArrayList<>();
    private PortItem portItemSelected;
    private static final int REQUEST_PER = 101;
    private CommPortIdentifier portId = null;

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
                intent.putExtra(DevicesActivity.KEY_PORT, portItemSelected);
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_try).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPort();
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
    }

    @Override
    public void OnClickPort(PortItem portItem) {
        portItemSelected = portItem;
    }

    public void getPort() {
        Enumeration<CommPortIdentifier> portIdentifiers = CommPortIdentifier.getPortIdentifiers();
        Toast.makeText(this, "Requesting Ports", Toast.LENGTH_SHORT).show();
        while (portIdentifiers.hasMoreElements()) {
            CommPortIdentifier pid = (CommPortIdentifier) portIdentifiers.nextElement();
            Toast.makeText(this, "Got : " + pid.getName(), Toast.LENGTH_SHORT).show();
            portId = pid;
            portItemList.add(new PortItem(pid.getName(), pid.getCurrentOwner()));
        }
        if (portItemList.size() != 0)
            portAdapter.notifyDataSetChanged();
        if (portId == null) {
            Toast.makeText(this, "Can't find any Serial Port ", Toast.LENGTH_SHORT).show();
        }
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
