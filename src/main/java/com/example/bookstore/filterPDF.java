package com.example.bookstore;

import android.widget.Filter;

import com.example.bookstore.adapters.AdapterCategory;
import com.example.bookstore.adapters.AdapterPDF;
import com.example.bookstore.models.ModelPdf;

import java.util.ArrayList;
import java.util.Locale;

public class filterPDF extends Filter {
    ArrayList<ModelPdf>filterlist;
    AdapterPDF adapterPDF;

    public filterPDF(ArrayList<ModelPdf> filterlist, AdapterPDF adapterPDF) {
        this.filterlist = filterlist;
        this.adapterPDF =adapterPDF;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
      FilterResults results=new FilterResults();
      if(constraint!=null&&constraint.length()>0){
          constraint=constraint.toString().toUpperCase();
          ArrayList<ModelPdf>filteredmodel=new ArrayList<>();
          for (int i=0;i<filterlist.size();i++){
              if(filterlist.get(i).getTitle().toUpperCase().contains(constraint)){
                  filteredmodel.add(filterlist.get(i));
              }
          }
          results.count=filteredmodel.size();
          results.values=filteredmodel;
      }
      else{
          results.count=filterlist.size();
          results.values=filterlist;
      }
      return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapterPDF.pdfArrayList=(ArrayList<ModelPdf>) results.values;
        adapterPDF.notifyDataSetChanged();

    }
}
