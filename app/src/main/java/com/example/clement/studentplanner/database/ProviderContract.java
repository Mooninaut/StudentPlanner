package com.example.clement.studentplanner.database;

import android.net.Uri;

/**
 * Created by Clement on 8/30/2017.
 */

public interface ProviderContract {
    Uri contentUri();
    Uri contentUri(long id);
    String contentItemType();
    String authority();
    String basePath();
}
