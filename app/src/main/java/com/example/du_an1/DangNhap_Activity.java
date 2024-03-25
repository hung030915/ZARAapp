package com.example.du_an1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class DangNhap_Activity extends AppCompatActivity {

    private Button dangky, dangNhap;
    private EditText email, matKhau;
    private TextView quenMK;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db;
    private DocumentReference reference;
    private Intent intent;
    private ListenerRegistration registration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);
        anhxa();
        dangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chuyen(DangKy_Activity.class);
            }
        });

        dangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dangnhap();
            }
        });
        quenMK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quenMK();
            }
        });
    }

    private void quenMK() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_quenpass, null, false);
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.show();
        EditText email = view.findViewById(R.id.edt_email_quen);
        Button gui = view.findViewById(R.id.btn_quen);

        gui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quenPass(email, dialog);

            }
        });
    }

    private void quenPass(EditText email, Dialog dialog) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = email.getText().toString();
        if (emailAddress.isEmpty()) {
            Toast.makeText(this, "Không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Sẽ mất một lúc vui lòng chờ");
        progressDialog.show();
        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.cancel();
                            Toast.makeText(DangNhap_Activity.this, "Đã gửi link khôi phục hãy kiểm tra email", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
    }

    private void anhxa() {
        dangky = findViewById(R.id.btn_dangky);
        dangNhap = findViewById(R.id.btn_dangnhap);
        email = findViewById(R.id.edt_email_dangnhap);
        matKhau = findViewById(R.id.edt_matkhau_dangnhap);
        quenMK = findViewById(R.id.tv_quenpass);
        progressDialog = new ProgressDialog(this);
        db = FirebaseFirestore.getInstance();
    }


    public void dangnhap() {
        String mEmail = email.getText().toString().trim();
        String mPass = matKhau.getText().toString().trim();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (email.getText().toString().isEmpty() || matKhau.getText().toString().isEmpty()) {
            Toast.makeText(this, "Không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Sẽ mất một lúc vui lòng chờ");
        progressDialog.show();



        mAuth.signInWithEmailAndPassword(mEmail, mPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            checkBan(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(DangNhap_Activity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                        }

                    }
                });
    }

    private void checkBan(FirebaseUser user) {
        reference = db.collection("user").document(user.getUid());


        registration= db.collection("user").whereEqualTo("trangThai", 1).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value == null) {
                    return;
                }
                if (error != null) {
                    Toast.makeText(DangNhap_Activity.this, "Lỗi mẹ nó rồi sửa đi", Toast.LENGTH_SHORT).show();
                    return;
                }
                int i = 0;
                for (DocumentSnapshot dc : value.getDocuments()) {
                    if (user.getUid().equals(dc.get("maUser"))) {
                        vaomanhinh();
                        i=1;
                        return;
                    }
                }
                if (i==0){
                    Toast.makeText(DangNhap_Activity.this, "Tài khoản bạn đã bị đình chỉ vui lòng liên", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                }
            }
        });
    }

    private void chuyen(Class a) {
        Intent intent = new Intent(this, a);
        startActivity(intent);
    }


    private void vaomanhinh() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            intent = new Intent(this, DangNhap_Activity.class);
            startActivity(intent);
        } else {
            check(new Long[]{0l}, user);

        }

    }

    private void check(Long[] chucvu, FirebaseUser user) {
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isComplete()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Tài liệu người dùng tồn tại
                        Map<String, Object> userData = document.getData();
                        chucvu[0] = (Long) userData.get("chucVu");
                        if (chucvu[0] == 1) {
                            intent = new Intent(DangNhap_Activity.this, ManHinhAdmin.class);
                        } else if (chucvu[0] == 2) {
                            intent = new Intent(DangNhap_Activity.this, ManHinhNhanVien.class);
                        } else if (chucvu[0] == 3) {
                            intent = new Intent(DangNhap_Activity.this, ManHinhKhachHang.class);
                        } else {
                            Toast.makeText(DangNhap_Activity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.cancel();
                        finishAffinity();
                        if (!isFinishing()){
                            return;
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        registration.remove();
                        Toast.makeText(DangNhap_Activity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("TAG", "vaomanhinh: " + user.getUid());
                        Toast.makeText(DangNhap_Activity.this, "Người dùng không tồn tại", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DangNhap_Activity.this, "Lỗi truy vấn", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }
}