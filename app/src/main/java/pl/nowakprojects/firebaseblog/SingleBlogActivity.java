package pl.nowakprojects.firebaseblog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class SingleBlogActivity extends AppCompatActivity {

    private String mPostKey=null;

    private DatabaseReference mDatabase;
    private DatabaseReference mLikesDatabase;
    private FirebaseAuth mAuth;

    private ImageView mBlogSingleImage;
    private TextView mBlogSingleTitle;
    private TextView mBlogSingleDesc;
    private TextView mSingleRemoveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_blog);

        mPostKey = getIntent().getExtras().getString("BLOG_ID");

        initUserInterface();
        initFirebase();
    }
    private void initUserInterface() {
        mBlogSingleImage = (ImageView) findViewById(R.id.singleBlogImage);
        mBlogSingleTitle = (TextView) findViewById(R.id.singleBlogTitle);
        mBlogSingleDesc = (TextView) findViewById(R.id.singleBlogDesc);
        mSingleRemoveButton = (Button) findViewById(R.id.removeButton);
        mSingleRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child(mPostKey).removeValue();
                mLikesDatabase.child(mPostKey).removeValue();
                Intent mainIntent = new Intent(SingleBlogActivity.this,MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(mainIntent);
            }
        });
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("FirebaseBlog");
        mLikesDatabase = FirebaseDatabase.getInstance().getReference().child("Likes");

        mDatabase.child(mPostKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String postTitle = (String) dataSnapshot.child("title").getValue();
                String postDesc = (String) dataSnapshot.child("desc").getValue();
                String postImage = (String) dataSnapshot.child("image").getValue();
                String postUid = (String) dataSnapshot.child("uid").getValue();

                mBlogSingleTitle.setText(postTitle);
                mBlogSingleDesc.setText(postDesc);
                Picasso.with(SingleBlogActivity.this).load(postImage).into(mBlogSingleImage);

                if(currentUserIsPostAuthor(postUid))
                    mSingleRemoveButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean currentUserIsPostAuthor(String postUid) {
        return mAuth.getCurrentUser().getUid().equals(postUid);
    }


}
