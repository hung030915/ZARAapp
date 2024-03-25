package com.example.du_an1.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.du_an1.R;
import com.example.du_an1.adapter.Adapter_choduyet;
import com.example.du_an1.model.DonHang;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Fragment_choxacnhan extends Fragment {
RecyclerView rcv_list;
Adapter_choduyet adapterChoduyet;
List<DonHang> list;
FirebaseFirestore db;
FirebaseUser user ;
String TAG = "TAG";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frangment_choxacnhan,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        anhXa(view);
    }

    private void anhXa(View view) {
        rcv_list = view.findViewById(R.id.rcv_list_doncho);
        user = FirebaseAuth.getInstance().getCurrentUser();
        getData();

    }

    private void getData() {
        db = FirebaseFirestore.getInstance();
        db.collection("donHang").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (!task.isComplete()){
                    return;
                }
                Log.e(TAG, "onComplete: "+1 );
                list= new ArrayList<>();
                adapterChoduyet = new Adapter_choduyet(list,getContext(),1);
                rcv_list.setAdapter(adapterChoduyet);
                rcv_list.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
                for (QueryDocumentSnapshot dc : task.getResult()){
                    Log.e(TAG, "onComplete: "+12 );
                    if (user.getUid().equals(dc.toObject(DonHang.class).getMaKhachHang())) {
                        list.add(dc.toObject(DonHang.class));
                        adapterChoduyet.notifyDataSetChanged();
                        Log.e(TAG, "onComplete: "+dc.toObject(DonHang.class).getTrangThai() );
                    }
                }
            }
        });
    }
}
