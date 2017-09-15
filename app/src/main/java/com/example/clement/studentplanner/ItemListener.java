package com.example.clement.studentplanner;

import android.view.View;

/**
 * Created by Clement on 9/10/2017.
 */

public class ItemListener {

    public interface OnClick {
        void onItemClick(View view, long itemId);
    }
    public interface OnLongClick {
        void onItemLongClick(View view, long itemId);
    }
}
