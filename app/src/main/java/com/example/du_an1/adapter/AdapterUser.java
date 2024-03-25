package com.example.du_an1.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.du_an1.R;
import com.example.du_an1.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.viewHolder> {
    private final Context context;
    private final List<User> list;
    FirebaseFirestore db;

    public AdapterUser(Context context, List list) {
        this.context = context;
        this.list = list;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.item_quan_ly_khach_hang, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, @SuppressLint("RecyclerView") int position) {
        User user = list.get(position);
        holder.tvTen.setText("Tên: " + list.get(position).getHoTen());
        holder.tvEmail.setText("Email: " + list.get(position).getEmail());

        if (user.getTrangThai() == 0) {
            holder.tvTrangThai.setText("Không hoạt động");
            holder.tvTrangThai.setTextColor(ContextCompat.getColor(context, R.color.xam));
        } else if (user.getTrangThai() == 1) {
            holder.tvTrangThai.setText("Đang hoạt động");
            holder.tvTrangThai.setTextColor(ContextCompat.getColor(context, R.color.xanhla));
        }
        holder.ibtn_xoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setIcon(R.drawable.warning);
                builder.setTitle("Cảnh báo !");
                builder.setMessage("Bạn có chắc chắn muốn xóa dữ liệu của nhân viên " + user.getHoTen() + " không ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.collection("user").document(list.get(position).getMaUser()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isComplete()){
                                    Toast.makeText(context, "Thành công", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }else {
                                    Toast.makeText(context, "Lỗi cụ rồi bảo dev fix đi", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setIcon(R.drawable.warning);
                builder.setTitle("Cánh bảo !");
                builder.setMessage("Bạn có muốn dừng hoạt động nhân viên " + user.getHoTen() + " không ?");
                builder.setPositiveButton("Tắt trạng thái", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       changeTT(0,list.get(position));
                    }
                });
                builder.setNegativeButton("Mở trạng thái", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        changeTT(1,list.get(position));
                    }
                });
                builder.create().show();

            }
        });
    }
    private void changeTT(int i,User user) {
        user.setTrangThai(i);
        db.collection("user").document(user.getMaUser()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()){
                    Toast.makeText(context, "Thành công", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "Lỗi cụ rồi bảo dev fix đi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView tvTen, tvEmail, tvTrangThai;
        ImageButton ibtn_xoa;


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            tvTen = itemView.findViewById(R.id.tv_Ten);
            tvEmail = itemView.findViewById(R.id.tv_Email);
            tvTrangThai = itemView.findViewById(R.id.tv_Trangthai);
            ibtn_xoa = itemView.findViewById(R.id.ibtn_xoa);
        }
    }


}
