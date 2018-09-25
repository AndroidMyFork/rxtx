package mrpark.com.port;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ksnet.comm.CommEvent;
import com.ksnet.comm.CommEventListener;
import com.ksnet.interfaces.Cat;

public class DevicesActivity extends Activity implements View.OnClickListener {
    public static final String KEY_PORT = "KEY_PORT";
    private static final byte[] REQ_TRAN_COMPLETE = {(byte) 0x30};
    Button btnTry, btnSendMess;
    TextView tvMessageReceive;
    private PendingIntent mPermissionIntent;
    UsbDevice usbDevice;
    EditText edtMessage;
    private PortItem portItem;

    /**
     * Event data receive from CATs
     */
    private CommEventListener commEventListener = new CommEventListener() {
        @Override
        public void fireEvent(CommEvent commEvent) {
            byte[] recvData = commEvent.getEventData();//data receive
            String strText = tvMessageReceive.getText().toString();//get current text
            switch (commEvent.getEventType()) {
                case CommEvent.ACK:
                    strText += "Recv Bytes [" + com.ksnet.util.Util.bytesToHexString(recvData) + "] : ACK! \n";
                    break;
                case CommEvent.NAK:
                    strText += "Recv Bytes [" + com.ksnet.util.Util.bytesToHexString(recvData) + "] : NAK\n";
                    break;
                case CommEvent.DLE:
                    strText += "Recv Bytes [" + com.ksnet.util.Util.bytesToHexString(recvData) + "DLE";
                    break;
                case CommEvent.COMM_START:
                    strText += "Recv Bytes [" + com.ksnet.util.Util.bytesToHexString(recvData) + "] : Comstart!\n";
                    break;
                case CommEvent.TRAN_RECV:
                    strText += "Recv Bytes [" + com.ksnet.util.Util.bytesToHexString(recvData) + "] : Tran Receive!\n";
                    break;
                case CommEvent.IC_CARD_REMOVE:
                    strText += "Recv Bytes [" + com.ksnet.util.Util.bytesToHexString(recvData) + "] : IC Card remove\n";
                    break;
                case CommEvent.IC_CARD_BLOCK:
                    strText += "Recv Bytes [" + com.ksnet.util.Util.bytesToHexString(recvData) + "] : IC CARD Block!\n";
                    break;
                case CommEvent.FALL_BACK:
                    strText += "Recv Bytes [" + com.ksnet.util.Util.bytesToHexString(recvData) + "] : FALL BACK \n";
                    break;
                case CommEvent.FIRST_REJECT:
                    strText += "Recv Bytes [" + com.ksnet.util.Util.bytesToHexString(recvData) + "] : FIRST_REJECT!\n";
                    break;
                // �ŷ� ��� ���� ����
                case CommEvent.CAT_TRADE_RESULT:
                    strText += "Recv String [" + new String(recvData, 0, recvData.length) + "] : CAT_TRADE_RESULT!\n";

                {
                    strText += "Send Bytes [" + com.ksnet.util.Util.bytesToHexString(REQ_TRAN_COMPLETE) + "] : Ȯ������ ����!\n";

                    WritePCPos(REQ_TRAN_COMPLETE, true);
                }

                break;
                case CommEvent.CAT_SIGN_START:
                    strText += "Recv Bytes [" + com.ksnet.util.Util.bytesToHexString(recvData) + "] : CAT_SIGN_START!\n";
                    break;
                case CommEvent.CAT_SIGN_END:
                    strText += "Recv Bytes [" + com.ksnet.util.Util.bytesToHexString(recvData) + "] : CAT_SIGN_END!\n";
                    break;
                case CommEvent.COMM_ERROR:
                    strText += "Recv Bytes [" + com.ksnet.util.Util.bytesToHexString(recvData) + "] :COMM_ERROR!\n";
                    break;
                case CommEvent.ERR_COMM_NETCACL_SUCC:
                    strText += "Recv Bytes [" + com.ksnet.util.Util.bytesToHexString(recvData) + "] : ERR_COMM_NETCACL_SUCC!\n";
                    break;
                case CommEvent.ERR_COMM_NETCACL_FAIL:
                    strText += "Recv Bytes [" + com.ksnet.util.Util.bytesToHexString(recvData) + "] : ERR_COMM_NETCACL_FAIL!\n";
                    break;
                case CommEvent.ERR_IC_REMOVE_NET_SUCC:
                    strText += "Recv Bytes [" + com.ksnet.util.Util.bytesToHexString(recvData) + "] : IC ERR_IC_REMOVE_NET_SUCC!\n";
                    break;
                case CommEvent.ERR_IC_REMOVE_NET_FAIL:
                    strText += "Recv Bytes [" + com.ksnet.util.Util.bytesToHexString(recvData) + "] : IC ERR_IC_REMOVE_NET_FAIL!\n";
                    break;
                case CommEvent.ERR_2ND_NET_SUCC:
                    strText += "Recv Bytes [" + com.ksnet.util.Util.bytesToHexString(recvData) + "] : ERR_2ND_NET_SUCC!\n";
                    break;
                case CommEvent.ERR_2ND_NET_FAIL:
                    strText += "Recv Bytes [" + com.ksnet.util.Util.bytesToHexString(recvData) + "] : ERR_2ND_NET_FAIL!\n";
                    break;

                // Error �߻�!
                case CommEvent.ERROR_UNKOWN_COMMAND:
                case CommEvent.ERROR_UNKNOWN:
                case CommEvent.ERROR_CONNECTION:
                case CommEvent.ERROR_DISCONNECTION:
                case CommEvent.ERROR_WRITE:
                case CommEvent.ERROR_READ:
                case CommEvent.ERROR_NONE_RESPONSE:
                    //strText += "Recv [" + com.ksnet.util.Util.bytesToHexString(recvData) + "] : Error\n";
                    strText += "Recv [" + new String(recvData, 0, recvData.length) + "] : Error\n";
                    break;
            }
            tvMessageReceive.setText(strText);
//            tvMessageReceive.scro
        }
    };

    private Cat catCtrl = new Cat(commEventListener);

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);
        portItem = (PortItem) getIntent().getSerializableExtra(KEY_PORT);
        if (portItem == null) {
            Toast.makeText(this, "Undefine port", Toast.LENGTH_SHORT).show();
            finish();
        } else
            openConnection();
//        usbDevice = getIntent().getParcelableExtra(UsbManager.EXTRA_DEVICE);
//        UsbManager mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
//        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(Constance.PERMISSION[0]), 0);
//        IntentFilter filter = new IntentFilter(Constance.USE_ATTACHED);
//        registerReceiver(deviceBCReceiver, filter);
//        if (mUsbManager != null)
//            if (usbDevice != null)
//                mUsbManager.requestPermission(usbDevice, mPermissionIntent);
//            else
//                Toast.makeText(this, "Please connect device", Toast.LENGTH_SHORT).show();
        initView();
    }

    private void initView() {
        btnTry = findViewById(R.id.btn_try);
        btnSendMess = findViewById(R.id.btn_send_mess);
        tvMessageReceive = findViewById(R.id.tv_current_message);
        edtMessage = findViewById(R.id.tv_message_send);
        tvMessageReceive.setMovementMethod(new ScrollingMovementMethod());

        btnSendMess.setOnClickListener(this);
        btnTry.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        IntentFilter intentFilter = new IntentFilter(Constance.PERMISSION[0]);
//        registerReceiver(deviceBCReceiver, intentFilter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        if (usbDevice == null)
            Toast.makeText(this, "Please try again!!", Toast.LENGTH_SHORT).show();
        else {
            Toast.makeText(this, usbDevice.getDeviceId() + "---" + usbDevice.getDeviceProtocol(), Toast.LENGTH_SHORT).show();
        }
    }

    private final BroadcastReceiver deviceBCReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constance.PERMISSION[0].equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            //todo open connection
                            DevicesActivity.this.openConnection();
                        }
                    } else {
                        Toast.makeText(context, "permission denied for device", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null) {
                    //todo close connection
                    DevicesActivity.this.closeConnection();
                }
            }
        }
    };

    /**
     * Close Connection
     */
    private void closeConnection() {
        catCtrl.closePort();
    }

    /**
     * Open connection
     */
    private void openConnection() {
        short result = catCtrl.openPort(portItem == null ? "/dev/ttyS" : portItem.getPort(), 38400, 20);
        switch (result) {
            case 0:
                Toast.makeText(this, "Connect success", Toast.LENGTH_SHORT).show();
                break;
            case -200://fail
                Toast.makeText(this, "Connect fail, Try again!!!", Toast.LENGTH_SHORT).show();
                break;
            case 201://port in use
                Toast.makeText(this, "This port is being in used", Toast.LENGTH_SHORT).show();
                break;
            case 202://many listener
                Toast.makeText(this, "Many listener is added", Toast.LENGTH_SHORT).show();
                break;
            case 203://unsupport com
                Toast.makeText(this, "This port may be not Serial Port...", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "Unknow Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void scrollTextView() {

    }

    /**
     * send data to machine
     *
     * @param writeData data want to send to machine
     * @param lrcFlag   true if show to screen, false to print
     */
    private void WritePCPos(byte[] writeData, boolean lrcFlag) {

        String strText = tvMessageReceive.getText().toString();

        short rtn = catCtrl.requestEncPcPosWrite(writeData, lrcFlag);

        if (rtn == 0) {
            strText += "Send Bytes [" + com.ksnet.util.Util.bytesToHexString(writeData) + "]!\n";
        } else if (rtn == -200) {
            String strMsg = String.format("Send error[%d]\n", rtn);
            strText += strMsg;
        } else {
            String strMsg = String.format("Unknown", rtn);
            strText += strMsg;
        }

        tvMessageReceive.setText(strText);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_mess:
                String mess = edtMessage.getText().toString();
                if (TextUtils.isEmpty(mess))
                    Toast.makeText(this, "Message send can't be empty!!!", Toast.LENGTH_SHORT).show();
                sendMessageToMachine(mess);
                break;
            case R.id.btn_try:

                openConnection();
                break;
        }
    }

    private void sendMessageToMachine(String message) {
        byte[] writeData = message.getBytes();
        String strText = tvMessageReceive.getText().toString();
        strText += "Send Bytes [" + new String(writeData, 0, writeData.length) + "] : �ݾ� ����!\n";
        tvMessageReceive.setText(strText);
        WritePCPos(writeData, true);
    }

    private void printData(String data) {
        byte[] bytes = data.getBytes();
        WritePCPos(bytes, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (regis)
//        unregisterReceiver(deviceBCReceiver);
    }

    @Override
    public void onBackPressed() {
        closeConnection();
        super.onBackPressed();
    }
}
