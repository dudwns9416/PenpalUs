package com.sc.fopa.penpalus.view;

import android.app.Activity;
import android.app.Dialog;

/**
 * Created by fopa on 2017-12-12.
 */

public class CustomDialog extends Dialog {
    Activity activity;

    public CustomDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

}
