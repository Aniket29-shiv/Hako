package com.hako.dreamproject.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;

import com.hako.dreamproject.R;

public class showInviteDialog {

    public Dialog showDialog(Dialog dialog, Context context) {
        dialog.setContentView(R.layout.invite_dialog);
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
