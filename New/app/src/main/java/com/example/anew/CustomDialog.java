package com.example.anew;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class CustomDialog extends Dialog {
    public CustomDialog(Context context) {
        super(context);
    }
    private DialogInterface.OnClickListener mPositiveButtonClickListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogcustom);

        // Khởi tạo các View trong Dialog

        EditText editText_name = findViewById(R.id.edittext_name);
        Button mButtonCancel = findViewById(R.id.btn_cancel);
        Button mButtonSave = findViewById(R.id.btn_save);
        Spinner type = findViewById(R.id.spinner_group);
        String[] places = new String[]{"Work", "Home", "Shopping", "School", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, places);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(adapter);

        // Xử lý sự kiện click vào nút Cancel


        // Xử lý sự kiện click vào nút Save

        mButtonSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mPositiveButtonClickListener != null) {
                    mPositiveButtonClickListener.onClick(CustomDialog.this, DialogInterface.BUTTON_POSITIVE);
                }
                dismiss();
            }
        });
        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    // Các phương thức để thiết lập giá trị cho các View trong Dialog
    public void setText_address(String text) {
       EditText edAddress = findViewById(R.id.edittext_address);
       edAddress.setText(text);
    }public void setPositiveButton(DialogInterface.OnClickListener listener) {
        mPositiveButtonClickListener = listener;
    }

}
