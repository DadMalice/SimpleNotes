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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class signup extends AppCompatActivity {

    private FirebaseAuth mAuth;
    TextView tv_alert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        tv_alert = findViewById(R.id.signup_alert);
        TextView tv_goto_login = findViewById(R.id.goto_login);
        EditText et_email = findViewById(R.id.signup_email);
        EditText et_pass1 = findViewById(R.id.signup_pass);
        EditText et_pass2 = findViewById(R.id.signup_pass2);
        Button btn_signup = findViewById(R.id.btn_signup);


        SpannableString content = new SpannableString("Already have an account? Login");
        content.setSpan(new UnderlineSpan(), 25, 30, 0);
        tv_goto_login.setText(content);

        tv_goto_login.setClickable(true);
        tv_goto_login.setOnClickListener(v -> {
            Intent intent = new Intent(signup.this, login.class);
            startActivity(intent);
        });

        btn_signup.setOnClickListener(v -> {
            String email = et_email.getText().toString();
            String pass1 = et_pass1.getText().toString();
            String pass2 = et_pass2.getText().toString();
            if (!isValidEmail(email)) {
                String str = "Invalid Email! " + email;
                tv_alert.setText(str);
                tv_alert.setTextColor(Color.RED);
            } else {
                if (pass1.length() < 8) {
                    tv_alert.setText("Password can't be less than 8 character");
                    tv_alert.setTextColor(Color.RED);
                } else if (!pass1.equals(pass2)) {
                    tv_alert.setText("Passwords don't match!");
                    tv_alert.setTextColor(Color.RED);
                } else {
                    signUp(email, pass1);
                }
            }
        });
    }

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void signUp(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            tv_alert.setText("Sign-up Successful. You can login now!");
                            tv_alert.setTextColor(Color.GREEN);

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

                            Intent intent = new Intent(signup.this, login.class);
                            startActivity(intent);

                        } else {
                            Exception exception = task.getException();
                            if (exception instanceof FirebaseAuthUserCollisionException) {
                                // Email address already in use
                                tv_alert.setText("Email address already in use!");
                                tv_alert.setTextColor(Color.RED);
                            } else {
                                assert exception != null;
                                tv_alert.setText(exception.toString());
                                tv_alert.setTextColor(Color.RED);
                            }
                        }
                    }
                });
    }


}