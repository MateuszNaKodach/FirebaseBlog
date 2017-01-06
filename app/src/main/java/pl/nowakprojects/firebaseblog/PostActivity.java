package pl.nowakprojects.firebaseblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 1;
    private Uri mImageUri = null;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mUserDatabase;

    private ProgressDialog mProgressDialog;
    private ImageButton mSelectImage;
    private EditText mPostTitle;
    private EditText mPostDesc;
    private Button mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        initFirebase();
        initUserInterface();
    }

    private void initFirebase() {
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("FirebaseBlog");
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
    }

    private void initUserInterface(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Posting to Blog...");

        mPostTitle = (EditText) findViewById(R.id.titleTextField);
        mPostDesc = (EditText) findViewById(R.id.descriptionTextField);
        mSelectImage = (ImageButton) findViewById(R.id.selectImage);
        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPostImage();
            }
        });
        mSubmitButton = (Button) findViewById(R.id.submitButton);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPosting();
            }
        });
    }

    private void startPosting() {

        final String title_val = mPostTitle.getText().toString().trim();
        final String desc_val = mPostDesc.getText().toString().trim();

        if(postIsCompleted()){
            addPostToFirebase(title_val, desc_val);
        }

    }

    private void addPostToFirebase(final String title_val, final String desc_val) {
        mProgressDialog.show();

        StorageReference filepath = mStorage.child("Blog_Images").child(mImageUri.getLastPathSegment());
        filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadedUri = taskSnapshot.getDownloadUrl();
                addPostToDatabase(title_val, desc_val, downloadedUri);

                mProgressDialog.dismiss();

                startActivity(new Intent(PostActivity.this, MainActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PostActivity.this,"Something went wrong!",Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();
            }
        });

    }

    private void addPostToDatabase(final String title_val, final String desc_val, final Uri downloadedUri) {
        final DatabaseReference newPost = mDatabase.push();
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                newPost.child("title").setValue(title_val);
                newPost.child("desc").setValue(desc_val);
                newPost.child("image").setValue(downloadedUri.toString());
                newPost.child("uid").setValue(mCurrentUser.getUid());
                newPost.child("username").setValue(dataSnapshot.child("name").getValue())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                            startActivity(new Intent(PostActivity.this,MainActivity.class));
                        else
                            Toast.makeText(getApplicationContext(),"Something went wrong!",Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private boolean postIsCompleted(){
        return !TextUtils.isEmpty(mPostTitle.getText().toString()) &&
                !TextUtils.isEmpty(mPostDesc.getText().toString()) &&
                mImageUri != null;
    }

    private void selectPostImage(){
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/+");
        startActivityForResult(galleryIntent,GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK)
            onProperlyActivityResult(requestCode,data);

    }

    private void onProperlyActivityResult(int requestCode, Intent data){
        switch(requestCode){
            case GALLERY_REQUEST:
                setPostImageFromUri(data.getData());
                break;
        }
    }

    private void setPostImageFromUri(Uri imageUri){
        mImageUri = imageUri;
        mSelectImage.setImageURI(imageUri);
    }
}
