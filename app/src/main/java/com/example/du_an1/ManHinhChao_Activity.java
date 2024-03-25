package com.example.du_an1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class ManHinhChao_Activity extends AppCompatActivity {

    private SharedPreferences preferences;
    private FirebaseFirestore db;
    private DocumentReference reference;
    private ListenerRegistration registration;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_man_hinh_chao);
        Log.e("TAG", "onCreate: " + "Đang mở activity");
        preferences = getSharedPreferences("begin", MODE_PRIVATE);
        int i = preferences.getInt("only", 0);
        db = FirebaseFirestore.getInstance();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (i == 0) {
                    Intent intent = new Intent(ManHinhChao_Activity.this, ManHinhKhoiDau_Activity.class);
                    startActivity(intent);

                    finish();
                } else {
                    vaomanhinh();
                }

            }
        }, 3000);
    }

    private void thaygiatri() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("only", 1);
        editor.apply();
    }


    private void vaomanhinh() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            intent = new Intent(this, DangNhap_Activity.class);
            startActivity(intent);
        } else {
            checkBan(user);

        }

    }

    private void checkBan(FirebaseUser user) {
        reference = db.collection("user").document(user.getUid());
        registration = db.collection("user").whereEqualTo("trangThai", 1).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value == null) {
                    return;
                }
                if (error != null) {
                    Toast.makeText(ManHinhChao_Activity.this, "Lỗi mẹ nó rồi sửa đi", Toast.LENGTH_SHORT).show();
                    return;
                }
                int i = 0;
                for (DocumentSnapshot dc : value.getDocuments()) {
                    if (user.getUid().equals(dc.get("maUser"))) {
                        DangNhap(user);
                        i = 1;
                        return;
                    }
                }
                if (i == 0) {
                    Toast.makeText(ManHinhChao_Activity.this, "Tài khoản bạn đã bị đình chỉ vui lòng liên", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    finish();
                }
            }
        });
    }

    private void DangNhap(FirebaseUser user) {
        final Long[] chucvu = new Long[]{0L};
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isComplete()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Tài liệu người dùng tồn tại
                        Map<String, Object> userData = document.getData();
                        chucvu[0] = (Long) userData.get("chucVu");
                        if (chucvu[0] == null) {
                            return;
                        }
                        if (chucvu[0] == 1) {
                            intent = new Intent(ManHinhChao_Activity.this, ManHinhAdmin.class);
                        } else if (chucvu[0] == 2) {
                            intent = new Intent(ManHinhChao_Activity.this, ManHinhNhanVien.class);
                        } else if (chucvu[0] == 3) {
                            intent = new Intent(ManHinhChao_Activity.this, ManHinhKhachHang.class);
                        } else {
                            Toast.makeText(ManHinhChao_Activity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                        }
                        finishAffinity();
                        if (!isFinishing()) {
                            return;
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        Log.e("TAG", "onComplete: " + "đã mở activity mới");
                        registration.remove();
                    } else {
                        Toast.makeText(ManHinhChao_Activity.this, "Người dùng không tồn tại", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                    }
                } else {
                    Toast.makeText(ManHinhChao_Activity.this, "Lỗi truy vấn", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                }
            }

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("TAG", "onDestroy: " + "đã kết thúc activity");
        System.gc();
    }
}