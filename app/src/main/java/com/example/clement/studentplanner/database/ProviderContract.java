package com.example.clement.studentplanner.database;

import android.net.Uri;

/**
 * Created by Clement on 8/30/2017.
 */

public interface ProviderContract {
    Uri getContentUri();
    Uri getContentUri(long id);
    String getContentItemType();
    String getAuthority();
    String getBasePath();
}
