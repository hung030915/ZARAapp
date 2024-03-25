package com.example.du_an1;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.du_an1.adapter.Adapter_thongbao;
import com.example.du_an1.fragment.Frg_quanLyHoaDon;
import com.example.du_an1.fragment.QuanLyGiay;
import com.example.du_an1.fragment.frg_ThongKe;
import com.example.du_an1.model.ThongBao;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManHinhNhanVien extends AppCompatActivity {

    Toolbar toolbar;
    FragmentContainerView viewPager;
    BottomNavigationView bottomNavigationView;
    FirebaseFirestore db;
    QuanLyGiay quanLyGiay = new QuanLyGiay(1);
    Menu menu_thongBao;
    FragmentManager manager;
    Uri uri;
    List<ThongBao> list_thongBao;
    Adapter_thongbao adapterThongbao;
    String TAG = "TAG";
    FirebaseUser user;
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if (o.getResultCode() == RESULT_OK) {
                        Intent intent = o.getData();
                        if (intent == null) {
                            return;
                        }
                        uri = intent.getData();
                        quanLyGiay.hienthiAnh(uri);

                    }

                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_man_hinh_nhan_vien);
        toolbar = findViewById(R.id.toolbar_nhanvien);
        viewPager = findViewById(R.id.fcv_Nhanvien);
        bottomNavigationView = findViewById(R.id.bnv_NhanVien);
        setSupportActionBar(toolbar);
        db=FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        getSupportActionBar().setTitle("Quản Lý Sản Phẩm");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        manager = getSupportFragmentManager();
        manager.beginTransaction().add(R.id.fcv_Nhanvien, quanLyGiay).commit();
        list_thongBao = new ArrayList<>();
        getThongBao();
        adapterThongbao = new Adapter_thongbao(list_thongBao,ManHinhNhanVien.this);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.menu_nhanvien_qlsp) {
                    relaceFrg(quanLyGiay);
                    getSupportActionBar().setTitle("Quản Lý Sản Phẩm");
                } else if (item.getItemId() == R.id.menu_nhanvien_thongke) {
                    relaceFrg(new frg_ThongKe());
                    getSupportActionBar().setTitle("Thống kê");
                } else if (item.getItemId() == R.id.menu_nhanvien_qlhd) {
                    relaceFrg(new Frg_quanLyHoaDon());
                    getSupportActionBar().setTitle("Quản Lý Hóa Đơn");
                } else if (item.getItemId() == R.id.menu_nhanvien_resetpass) {
                    ManHinhAdmin.doipass(ManHinhNhanVien.this);
                    return false;
                } else {
                    Toast.makeText(ManHinhNhanVien.this, "Lỗi", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AlertDialog.Builder builder = new AlertDialog.Builder(ManHinhNhanVien.this);
                builder.setTitle("Thông báo");
                builder.setIcon(R.drawable.user1);
                builder.setMessage("Bạn có muốn đăng xuất");
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(ManHinhNhanVien.this, DangNhap_Activity.class);
                        startActivity(intent);
                        finishAffinity();
                        Toast.makeText(ManHinhNhanVien.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();

            }
        });
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_thongBao) {
            item.setIcon(R.drawable.belldis);
            xemThongBao();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        menu_thongBao = menu;
        return super.onCreateOptionsMenu(menu);
    }

    public void relaceFrg(Fragment fragment) {
        manager.beginTransaction().replace(R.id.fcv_Nhanvien, fragment).commit();
    }

    public void layAnh() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        launcher.launch(intent);
    }

    public void yeucauquyen(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            layAnh();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            String[] quyen = new String[]{android.Manifest.permission.READ_MEDIA_IMAGES};
            requestPermissions(quyen, CODE_QUYEN);
            return;
        }
        if (context.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // xử lý sau
            layAnh();
        } else {
            String[] quyen = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(quyen, CODE_QUYEN);
        }
    }

    private static final int CODE_QUYEN = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE_QUYEN) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                layAnh();
            } else {
                Toast.makeText(this, "Bạn cần cấp quyền để sử dụng tính năng này", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void xemThongBao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_them_hang,null,false);
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.show();

        ListView listView = view.findViewById(R.id.list_hang);
        TextView tittle = view.findViewById(R.id.tv_tittle2);
        EditText editText = view.findViewById(R.id.edt_themhang_);
        ImageButton imageButton = view.findViewById(R.id.ibtn_addhang);

        editText.setVisibility(View.GONE);
        imageButton.setVisibility(View.GONE);
        tittle.setText("Thông báo");
        listView.setAdapter(adapterThongbao);
    }

    public void doiIcon(){
        if (menu_thongBao==null){
            return;
        }
        MenuItem item = menu_thongBao.findItem(R.id.menu_thongBao);
        if (item==null){
            return;
        }
        item.setIcon(R.drawable.bell_dis_);

    }

    private void getThongBao() {
        db.collection("thongBao").whereEqualTo("chucVu",2).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "onEvent: "+1 );
                    return;
                }
                if (value == null) {
                    Log.e(TAG, "onEvent: "+2 );
                    return;
                }
                for (DocumentChange dc : value.getDocumentChanges()) {
                    switch (dc.getType()) {

                        case ADDED:
                            list_thongBao.add(dc.getDocument().toObject(ThongBao.class));
                            Log.e(TAG, "onEvent: "+"tôi yêu vợ" );
                            doiIcon();
                            adapterThongbao.notifyDataSetChanged();
                            Log.e(TAG, "onEvent: "+"tôi yêu" );
                            break;
                        case MODIFIED:
                            ThongBao tb = dc.getDocument().toObject(ThongBao.class);
                            if (dc.getOldIndex() == dc.getNewIndex()) {
                                list_thongBao.set(dc.getOldIndex(), tb);

                            } else {
                                list_thongBao.remove(dc.getOldIndex());
                                list_thongBao.add(tb);
                            }
                            adapterThongbao.notifyDataSetChanged();
                            break;
                        case REMOVED:
                            list_thongBao.remove(dc.getOldIndex());
                            adapterThongbao.notifyDataSetChanged();
                            break;
                    }

                }
            }
        });
    }
}