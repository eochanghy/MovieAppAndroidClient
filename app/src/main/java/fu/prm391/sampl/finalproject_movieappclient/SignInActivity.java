package fu.prm391.sampl.finalproject_movieappclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    private TextView txtSignUp;
    private EditText txtEmail, txtPassword;
    private Button btnSignIn;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initView();
        initListener();
    }

    private void initListener() {
        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!txtEmail.getText().toString().trim().isEmpty() && !txtPassword.getText().toString().trim().isEmpty()) {
                    signIn();
                } else {
                    Toast.makeText(SignInActivity.this, "Please input email and password..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signIn() {
        progressDialog.setMessage("Sign in...");
        progressDialog.show();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String strEmail = txtEmail.getText().toString().trim();
        String strPassword = txtPassword.getText().toString().trim();
        mAuth.signInWithEmailAndPassword(strEmail, strPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            progressDialog.dismiss();
                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(SignInActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initView() {
        progressDialog = new ProgressDialog(this);
        txtEmail = findViewById(R.id.txt_login_email);
        txtPassword = findViewById(R.id.txt_login_password);
        btnSignIn = findViewById(R.id.btn_sign_in);
        txtSignUp = findViewById(R.id.signup);
    }
}