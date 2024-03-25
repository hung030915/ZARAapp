package com.example.du_an1.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.du_an1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class frg_DoiMatKhau extends Fragment {



    public frg_DoiMatKhau() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog__doi_mat_khau, null);
        EditText edt_passCu, edt_passMoi, edt_xacNhan;
        AppCompatButton btn_doiMK;
        edt_passCu = view.findViewById(R.id.edt_nhapmkcu);
        edt_passMoi = view.findViewById(R.id.edt_nhapmkmoi);
        edt_xacNhan = view.findViewById(R.id.edt_xacnhanmk);
        btn_doiMK = view.findViewById(R.id.btn_doiMK);

        btn_doiMK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pasCu = edt_passCu.getText().toString();
                String pasMoi = edt_passMoi.getText().toString();
                String xacNhan = edt_xacNhan.getText().toString();

                if (pasCu.isEmpty() || pasMoi.isEmpty() || xacNhan.isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else if (xacNhan.equals(pasMoi)) {
                    doiMK(pasCu, pasMoi);
                } else {
                    Toast.makeText(getContext(), "Xác nhận mật khẩu mới sai", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    public void doiMK(String pasCu, String pasMoi) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential authenticator = EmailAuthProvider.getCredential(user.getEmail(), pasCu);
        user.reauthenticate(authenticator).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    user.updatePassword(pasMoi).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Mật khẩu cũ sai vui lòng nhập lại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}