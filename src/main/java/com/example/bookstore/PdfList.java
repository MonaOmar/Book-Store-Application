package com.example.bookstore;

import static io.reactivex.rxjava3.internal.util.NotificationLite.getValue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.example.bookstore.adapters.AdapterPDF;
import com.example.bookstore.databinding.ActivityPdflistBinding;
import com.example.bookstore.models.ModelPdf;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PdfList extends AppCompatActivity {
    private ArrayList<ModelPdf> pdfArrayList;
    private AdapterPDF adapterPDF;
    private ActivityPdflistBinding binding;
    private String categoryId,categoryTitle;
    private static final String TAG="PDF_LIST_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPdflistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent=getIntent();
       categoryId= intent.getStringExtra("categoryId");
       categoryTitle= intent.getStringExtra("categoryTitle");
       binding.subtitleTv.setText(categoryTitle);


       LoadPdfList();

       binding.searchEt.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
               try {
                   adapterPDF.getFilter().filter(s);

               }catch (Exception e){
                   Log.d(TAG, "onTextChanged: "+e.getMessage());

               }

           }

           @Override
           public void afterTextChanged(Editable s) {

           }
       });
       binding.backBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               onBackPressed();
           }
       });
    }

    private void LoadPdfList() {
        pdfArrayList=new ArrayList<>();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Books");
        ref.orderByChild("categoryId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pdfArrayList.clear();
                        for(DataSnapshot ds:snapshot.getChildren()){
                        String uid=ds.child("uid").getValue().toString();
                        String id=ds.child("id").getValue().toString();
                        String title=ds.child("title").getValue().toString();
                        String description=ds.child("description").getValue().toString();
                        String categoryId=ds.child("categoryId").getValue().toString();
                        String url=ds.child("url").getValue().toString();
                        long timestamp=ds.child("timestamp").getValue().hashCode();
                        ModelPdf model=new ModelPdf(uid,id,title,description,categoryId,url,timestamp);
                        pdfArrayList.add(model);
                            Log.d(TAG, "onDataChange: "+model.getId()+""+model.getTitle());
                        }
                        adapterPDF =new AdapterPDF(PdfList.this,pdfArrayList);
                        binding.bookRv.setAdapter(adapterPDF);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}