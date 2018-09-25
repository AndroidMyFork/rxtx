package mrpark.com.port;

import android.Manifest;

public class Constance {
    public static final String[] PERMISSION = {"com.android.example.USB_PERMISSION"};
    public static final String[] PERMISSION_EXTERNAL = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final String USE_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";

    public static final String[] ANDROID_PORT = {"/dev/tty", "/dev/ttySA", "/dev/ttyUSB"};
}
