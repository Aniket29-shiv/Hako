package com.hako.dreamproject.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;

import com.hako.dreamproject.R;

public class TimeOutDialog {

    @SuppressLint("UseCompatLoadingForDrawables")
    public Dialog showDialog(Dialog dialog, Context context) {
        dialog.setContentView(R.layout.time_out_dialog);
        dialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.dialog_background));
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.show();

        return dialog;
    }

}
