package com.example.bookstore.adapters;

import static com.example.bookstore.Constants.MAX_BYTES_PDF;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookstore.databinding.ListPdfBinding;
import com.example.bookstore.filterPDF;
import com.example.bookstore.models.ModelPdf;
import com.example.bookstore.myApplication;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;

public class AdapterPDF extends RecyclerView.Adapter<AdapterPDF.HolderPDF>implements Filterable {
    private Context context;
    public ArrayList<ModelPdf>pdfArrayList,filterlist;
    private ListPdfBinding binding;
    private filterPDF filter;
    private static final String TAG="PDF_ADAPTER_TAG";

    public AdapterPDF(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterlist=pdfArrayList;
    }

    @NonNull
    @Override
    public HolderPDF onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding=ListPdfBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderPDF(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPDF holder, int position) {
        ModelPdf model=pdfArrayList.get(position);
        String title=model.getTitle();
        String description=model.getDescription();
        long timestamp=model.getTimestamp();
        String formattedDate= myApplication.formatTimestamp(timestamp);

        holder.textname.setText(title);
        holder.textdesc.setText(description);
        holder.datetv.setText(formattedDate);


        loadCategory(model,holder);
        loadPdfFromUrl(model,holder);
        loadPdfSize(model,holder);

    }

    private void loadPdfSize(ModelPdf model, HolderPDF holder) {
        String pdfurl=model.getUrl();
        StorageReference ref=FirebaseStorage.getInstance().getReferenceFromUrl(pdfurl);
        ref.getMetadata()
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        double bytes=storageMetadata.getSizeBytes();
                        Log.d(TAG, "onSuccess: "+model.getTitle()+""+bytes);
                        double Kb=bytes/1024;
                        double mb=Kb/1024;

                        if(mb>=1){
                            holder.sizetv.setText(String.format("%.2f",mb)+"MB");
                        }
                        else   if(Kb>=1){
                            holder.sizetv.setText(String.format("%.2f",Kb)+"kB");
                        }
                        else{
                            holder.sizetv.setText(String.format("%.2f",bytes)+"bytes");

                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: "+e.getMessage());

                    }
                });
    }

    private void loadPdfFromUrl(ModelPdf model,HolderPDF holder){
        String pdfurl=model.getUrl();
        StorageReference ref=FirebaseStorage.getInstance().getReferenceFromUrl(pdfurl);
        ref.getBytes(MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Log.d(TAG, "onSuccess: "+model.getTitle()+"successfully got the file");
                        holder.pdfview.fromBytes(bytes)
                                .pages(0)
                                .spacing(0)
                                .swipeHorizontal(false)
                                .enableSwipe(false)
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {
                                        holder.progressBar.setVisibility(View.INVISIBLE);
                                        Log.d(TAG, "onError: "+t.getMessage());
                                    }
                                })
                                .onPageError(new OnPageErrorListener() {
                                    @Override
                                    public void onPageError(int page, Throwable t) {
                                        holder.progressBar.setVisibility(View.INVISIBLE);
                                        Log.d(TAG, "onPageError: "+t.getMessage());
                                    }
                                })
                                .onLoad(new OnLoadCompleteListener() {
                                    @Override
                                    public void loadComplete(int nbPages) {
                                        holder.progressBar.setVisibility(View.INVISIBLE);
                                        Log.d(TAG, "loadComplete: pdf loaded");

                                    }
                                })
                                .load();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        holder.progressBar.setVisibility(View.INVISIBLE);
                        Log.d(TAG, "onFailure: failed getting file from url due to"+e.getMessage());

                    }
                });

    }

    private void loadCategory(ModelPdf model, HolderPDF holder) {
        String categoryid=model.getCategoryId();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(categoryid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String category=""+snapshot.child("category").getValue();
                        holder.categorytv.setText(category);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter=new filterPDF(filterlist,this);
        }
        return filter;
    }

    class HolderPDF extends RecyclerView.ViewHolder{
       PDFView pdfview;
       ProgressBar progressBar;
        TextView textname,textdesc,categorytv,sizetv,datetv;

        public HolderPDF(@NonNull View itemView) {
            super(itemView);
            pdfview=binding.pdfView;
            progressBar=binding.prbar;
            textname=binding.titletv;
            textdesc=binding.titledes;
            categorytv=binding.categoryTv;
            sizetv=binding.sizeTv;
            datetv=binding.dateTv;
        }
    }
}
