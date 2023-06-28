package com.example.SimpleNotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    TextView tv_login_alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        TextView tv_goto_signup = findViewById(R.id.goto_signup);
        tv_login_alert = findViewById(R.id.login_alert);
        EditText et_email = findViewById(R.id.login_email);
        EditText et_pass = findViewById(R.id.login_pass);
        Button btn_login = findViewById(R.id.btn_login);

        SpannableString content = new SpannableString("Don't have an account? Signup");
        content.setSpan(new UnderlineSpan(), 23, 29, 0);
        tv_goto_signup.setText(content);

        tv_goto_signup.setClickable(true);
        tv_goto_signup.setOnClickListener(v -> {
            Intent intent = new Intent(login.this, signup.class);
            startActivity(intent);
        });

        btn_login.setOnClickListener(v-> {
            String email = et_email.getText().toString();
            String pass = et_pass.getText().toString();
            if (!isValidEmail(email)) {
                tv_login_alert.setText("Invalid Email! ");
                tv_login_alert.setTextColor(Color.RED);
            } else {
                login(email, pass);
            }
        });
    }

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void login(String email, String pass) {
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            tv_login_alert.setText("Login Successful");
                            tv_login_alert.setTextColor(Color.GREEN);

                            //pause for a bit
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(1500); // pause for 1000 milliseconds (1 second)
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();

                            // Start the next activity
                            Intent intent = new Intent(login.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            // If login fails, display a message to the user
                            Exception exception = task.getException();
                            if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                // Invalid password
                                tv_login_alert.setText("Invalid Password");
                            } else if (exception instanceof FirebaseAuthInvalidUserException) {
                                // Invalid email address
                                tv_login_alert.setText("No such user found!");
                            } else {
                                // Other failure
                                assert exception != null;
                                tv_login_alert.setText(exception.toString());
                            }
                            tv_login_alert.setTextColor(Color.RED);
                        }
                    }
                });
    }
}