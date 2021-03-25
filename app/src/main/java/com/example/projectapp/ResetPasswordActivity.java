package com.example.projectapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private Button ResetPasswordSendEmailButton;
    private EditText ResetEmailInput;
    private FirebaseAuth mAuth;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth = FirebaseAuth.getInstance();

        ResetPasswordSendEmailButton = (Button) findViewById(R.id.reset_pswrd_button);
        ResetEmailInput = (EditText) findViewById(R.id.reset_pswrd_email);

        ResetPasswordSendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = ResetEmailInput.getText().toString();

                if(TextUtils.isEmpty(userEmail))
                {
                    Toast.makeText(ResetPasswordActivity.this,"please, type your email...",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(ResetPasswordActivity.this,"Please,check your mail...",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(ResetPasswordActivity.this,loginActivity.class));
                            }
                            else {
                                String message = task.getException().getMessage();
                                Toast.makeText(ResetPasswordActivity.this,"Error Occured..."+ message,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
