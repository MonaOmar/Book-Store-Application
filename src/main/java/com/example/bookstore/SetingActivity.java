package com.example.bookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class SetingActivity extends AppCompatActivity {
    private EditText passedittext;
    private Button changepass;
    private  FirebaseAuth mAuth;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seting);
        passedittext=(EditText) findViewById(R.id.changePassword);
        changepass=(Button) findViewById(R.id.btnchange);
        mAuth = FirebaseAuth.getInstance();


        changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resetpass();
            }
        });

    }
    private void resetpass() {
         password = passedittext.getText().toString().trim();
        if (password.isEmpty()) {
            passedittext.setError("password is required");

        } else {
            forgetPass();
        }
    }

    private void forgetPass() {
        mAuth.sendPasswordResetEmail(password).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SetingActivity.this,"check your email to reset password",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(SetingActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(SetingActivity.this,"try again! something wrong happened!",Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}