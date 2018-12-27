package com.example.molder.app1202;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;



import java.util.HashSet;
import java.util.Set;

public class Common {
    public static final int REQ_EXTERNAL_STORAGE = 0;
    private static final String TAG = "Common";

    public static Bitmap downSize(Bitmap srcBitmap, int newSize) {
        if (newSize <= 20) {
            // 如果欲縮小的尺寸過小，就直接定為20
            newSize = 20;
        }
        int srcWidth = srcBitmap.getWidth();
        int srcHeight = srcBitmap.getHeight();
        int longer = Math.max(srcWidth, srcHeight);

        if (longer > newSize) {
            int dstWidth = 100;
            int dstHeight = 100;
            srcBitmap = Bitmap.createScaledBitmap(srcBitmap, dstWidth, dstHeight, false);
//            Resources res = this.getContext().getResources();
//            Bitmap src = BitmapFactory.decodeResource(res, srcBitmap);
//            RoundedBitmapDrawable dr =
//                    RoundedBitmapDrawableFactory.create(res, src);
//            dr.setCornerRadius(Math.max(src.getWidth(), src.getHeight()) / 2.0f);

        }
        return srcBitmap;
    }

    // New Permission see Appendix A
    public static void askPermissions(Activity activity, String[] permissions, int requestCode) {
        Set<String> permissionsRequest = new HashSet<>();
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(activity, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionsRequest.add(permission);
            }
        }

        if (!permissionsRequest.isEmpty()) {
            ActivityCompat.requestPermissions(activity,
                    permissionsRequest.toArray(new String[permissionsRequest.size()]),
                    requestCode);
        }
    }
}
