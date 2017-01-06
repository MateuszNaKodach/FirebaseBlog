package pl.nowakprojects.firebaseblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mUsersDatabase;

    private ProgressDialog mProgressDialog;

    private EditText mNameField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mPasswordReField;

    private Button mRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initFirebase();
        initUserInterface();
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    private void initUserInterface() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Singin up...");

        mNameField = (EditText) findViewById(R.id.nameField);
        mEmailField = (EditText) findViewById(R.id.emailField);
        mPasswordField = (EditText) findViewById(R.id.passwordField);
        mPasswordReField = (EditText) findViewById(R.id.passwordReField);

        mRegisterButton = (Button) findViewById(R.id.registerButton);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegister();
            }
        });
    }

    private void startRegister() {
        final String name = mNameField.getText().toString().trim();
        final String email = mEmailField.getText().toString().trim();
        final String password = mPasswordField.getText().toString().trim();
        final String repeatedPassword = mPasswordReField.getText().toString().trim();

        if(registerFormIsCompleted(name, email, password, repeatedPassword)){
            mProgressDialog.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                        addUserInfoToDatabase();

                    mProgressDialog.dismiss();

                    Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
                    startActivity(mainIntent);
                }

                private void addUserInfoToDatabase() {
                    String userId = mAuth.getCurrentUser().getUid();

                    DatabaseReference currentUserDatabase = mUsersDatabase.child(userId);
                    currentUserDatabase.child("name").setValue(name);
                    currentUserDatabase.child("image").setValue("default");
                }
            });
        }else
            showFormWrongMessage();
    }

    private void showFormWrongMessage() {
        Toast.makeText(this,"Something in form is wrong!",Toast.LENGTH_LONG).show();
    }

    private boolean registerFormIsCompleted(String name, String email, String password, String repeatedPassword) {
        return !TextUtils.isEmpty(name)&& !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && password.equals(repeatedPassword);
    }


}
