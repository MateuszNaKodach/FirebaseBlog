package pl.nowakprojects.firebaseblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SetupActivity extends AppCompatActivity {

    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;
    private StorageReference mProfileImagesStorage;

    private ProgressDialog mProgressDialog;

    private ImageButton mSetupImageButton;
    private EditText mNameField;
    private Button mSubmitButton;

    private Uri mImageUri = null;
    private static final int GALLERY_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        initFirebase();
        initUserInterface();
    }

    private void initFirebase(){
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        mProfileImagesStorage = FirebaseStorage.getInstance().getReference().child("Profile_images");
    }

    private void initUserInterface() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Finishing setup...");
        mNameField = (EditText) findViewById(R.id.setupNameField);

        mSetupImageButton = (ImageButton) findViewById(R.id.setupImageButton);
        mSetupImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseProfileImage();
            }
        });

        mSubmitButton = (Button) findViewById(R.id.setupSubmitButton);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSetupAccountInDatabaseAndStorage();
            }
        });

    }

    private void startSetupAccountInDatabaseAndStorage() {
        final String name = mNameField.getText().toString().trim();
        final String userId = mAuth.getCurrentUser().getUid();
        if(isSetupAccountFormCompleted(name)){
            mProgressDialog.show();
            StorageReference filepath = mProfileImagesStorage.child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadedUrl = taskSnapshot.getDownloadUrl().toString();

                    mUsersDatabase.child(userId).child("name").setValue(name);
                    mUsersDatabase.child(userId).child("image").setValue(downloadedUrl);

                    mProgressDialog.dismiss();

                    Intent mainIntent = new Intent(SetupActivity.this,MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                }
            });
        }
    }

    private boolean isSetupAccountFormCompleted(String name) {
        return !TextUtils.isEmpty(name) && mImageUri!=null;
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
                cropProfileImage(data.getData());
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                setProfileImageFromCropedResult(data);
                break;
        }
    }

    private void setProfileImageFromCropedResult(Intent data) {
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        mImageUri = result.getUri();
        mSetupImageButton.setImageURI(mImageUri);
    }

    private void cropProfileImage(Uri data) {
        CropImage.activity(data)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(this);
    }

    private void chooseProfileImage() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GALLERY_REQUEST);
    }
}
