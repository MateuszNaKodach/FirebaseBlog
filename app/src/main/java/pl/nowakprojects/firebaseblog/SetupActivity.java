package pl.nowakprojects.firebaseblog;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SetupActivity extends AppCompatActivity {

    private ImageButton mSetupImageButton;
    private EditText mNameField;
    private Button mSubmitButton;

    private static final int GALLERY_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        initUserInterface();
    }

    private void initUserInterface() {
        mSetupImageButton = (ImageButton) findViewById(R.id.setupImageButton);
        mNameField = (EditText) findViewById(R.id.setupNameField);
        mSubmitButton = (Button) findViewById(R.id.submitButton);

        mSetupImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseProfileImage();
            }
        });
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
        Uri resultUri = result.getUri();
        mSetupImageButton.setImageURI(resultUri);
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
