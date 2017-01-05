package pl.nowakprojects.firebaseblog;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class PostActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 1;

    private ImageButton mSelectImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        initUserInterface();
    }

    private void initUserInterface(){
        mSelectImage = (ImageButton) findViewById(R.id.selectImage);
        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPostImage();
            }
        });
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
        mSelectImage.setImageURI(imageUri);
    }
}
