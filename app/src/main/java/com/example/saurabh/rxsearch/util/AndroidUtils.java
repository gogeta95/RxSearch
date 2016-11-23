package com.example.saurabh.rxsearch.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by saurabh on 22/09/16.
 */

public class AndroidUtils {
    public static void openURLinBrowser(Context context, String url){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }
}
