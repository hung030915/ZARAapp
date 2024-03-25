package com.example.du_an1.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.du_an1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;


public class ThongKe_DoanhThu extends Fragment {

    EditText edt_NgayEnd, edt_NgayStart;
    TextView tv_doanhThu, tv_ChoNgayEnd, tv_chonNgayStart;
    AppCompatButton btn_doanhthu;

    public ThongKe_DoanhThu() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_thong_ke__doanh_thu, null);
        edt_NgayEnd = view.findViewById(R.id.edt_ngayEnd);
        edt_NgayStart = view.findViewById(R.id.edt_ngayStart);
        tv_chonNgayStart = view.findViewById(R.id.tv_chonNgayStart);
        tv_ChoNgayEnd = view.findViewById(R.id.tv_chonNgayEnd);
        btn_doanhthu = view.findViewById(R.id.btn_doanhThu);

        tv_doanhThu = view.findViewById(R.id.tv_doanhThu);

        Calendar calendar = Calendar.getInstance();

        tv_chonNgayStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String ngay = "";
                        String thang = "";
                        if (dayOfMonth < 10) {
                            ngay = "0" + dayOfMonth;
                        } else {
                            ngay = String.valueOf(dayOfMonth);
                        }

                        if ((month + 1) < 10) {
                            thang = "0" + (month + 1);
                        } else {
                            thang = String.valueOf(month + 1);
                        }
                        edt_NgayStart.setText(year + "/" + thang + "/" + ngay);
                    }
                },
                        calendar.get(calendar.YEAR),
                        calendar.get(calendar.MONDAY),
                        calendar.get(calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });
        tv_ChoNgayEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String ngay = "";
                        String thang = "";
                        if (dayOfMonth < 10) {
                            ngay = "0" + dayOfMonth;
                        } else {
                            ngay = String.valueOf(dayOfMonth);
                        }
                        if ((month + 1) < 10) {
                            thang = "0" + (month + 1);
                        } else {
                            thang = String.valueOf(month + 1);
                        }
                        edt_NgayEnd.setText(year + "/" + thang + "/" + ngay);
                    }
                },
                        calendar.get(calendar.YEAR),
                        calendar.get(calendar.MONDAY),
                        calendar.get(calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });


        btn_doanhthu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String ngayStart = edt_NgayStart.getText().toString();
                String ngayEnd = edt_NgayEnd.getText().toString();

                db.collection("donHangDaDuyet")
                        .whereGreaterThanOrEqualTo("ngayMua", ngayStart)
                        .whereLessThanOrEqualTo("ngayMua", ngayEnd)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isComplete()) {
                                    Long tong = 0l;
                                    for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                                        Long price = snapshot.getLong("giaDon");
                                        tong += price;
                                    }
                                    tv_doanhThu.setText(tong + " VND");
                                } else {
                                    Toast.makeText(getContext(), "Loi", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


        return view;
    }
}