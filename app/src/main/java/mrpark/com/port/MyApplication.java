package mrpark.com.port;

import android.app.Application;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

public class MyApplication extends MultiDexApplication {
    static {
        System.loadLibrary("rxtxSerial");
    }
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
    }
}
