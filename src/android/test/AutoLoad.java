
package android.test;

import com.jovision.Consts;
import com.jovision.commons.MyLog;

public class AutoLoad {

    public static boolean foo() {
        boolean result = false;

        try {
            System.loadLibrary("gnustl_shared");
            System.loadLibrary("stlport_shared");
            System.loadLibrary("accountsdk");
            System.loadLibrary("tools");
            System.loadLibrary("alu");
            System.loadLibrary("play");
            result = true;
            MyLog.e(Consts.TAG_APP, "AutoLoad libs have done!");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
