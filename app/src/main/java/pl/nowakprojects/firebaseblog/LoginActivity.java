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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mUsersDatabase;

    private ProgressDialog mProgressDialog;

    private EditText mLoginEmailField;
    private EditText mPasswordEmailField;

    private Button mLoginButton;
    private Button mNewAccountButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initFirebase();
        initUserInterface();
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);
    }

    private void initUserInterface() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Checking login...");

        mLoginEmailField = (EditText) findViewById(R.id.loginEmailField);
        mPasswordEmailField = (EditText) findViewById(R.id.loginPasswordField);
        
        mLoginButton = (Button) findViewById(R.id.loginButton);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLogin();
            }
        });
    }

    private void checkLogin() {
        String email = mLoginEmailField.getText().toString().trim();
        String password = mPasswordEmailField.getText().toString().trim();

        if(isLoginFormCompleted(email,password)){
            mProgressDialog.show();
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        mProgressDialog.dismiss();
                        checkIfUserExistsInDatabase();
                    }else{
                        mProgressDialog.dismiss();
                        Toast.makeText(LoginActivity.this,"Error Login!",Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    }

    private void checkIfUserExistsInDatabase() {
        final String userId = mAuth.getCurrentUser().getUid();

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(userId))
                    startMainActivity();
                else
                   startAccountSetupActivity();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void startAccountSetupActivity() {
        Intent setupIntent = new Intent(LoginActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //uzytkownik nie moze wrócić do poprzedniej
        startActivity(setupIntent);
    }

    private void startMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //uzytkownik nie moze wrócić do poprzedniej
        startActivity(mainIntent);
    }

    private boolean isLoginFormCompleted(String email, String password) {
        return !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password);
    }


}
