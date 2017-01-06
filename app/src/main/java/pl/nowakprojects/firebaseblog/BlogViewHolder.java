package pl.nowakprojects.firebaseblog;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by Mateusz on 05.01.2017.
 */

class BlogViewHolder extends RecyclerView.ViewHolder {

    private View mView;

    private ImageButton mLikeButton;

    public BlogViewHolder(View itemView) {
        super(itemView);

        mView = itemView;

        mLikeButton = (ImageButton) mView.findViewById(R.id.likeButton);
    }

    void setTitle(String title){
        TextView post_title = (TextView) mView.findViewById(R.id.post_title);

        post_title.setText(title);
    }

    void setDesc(String desc){
        TextView post_desc = (TextView) mView.findViewById(R.id.post_desc);
        post_desc.setText(desc);
    }

    void setImage(final Context context, final String image){
        final ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);
        Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(post_image, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(context).load(image).into(post_image);
            }
        });
    }

    void setUsername(String username){
        TextView post_username = (TextView) mView.findViewById(R.id.post_username);
        post_username.setText(username);
    }

    public View getView() {
        return mView;
    }

    public ImageButton getLikeButton() {
        return mLikeButton;
    }

    void setLikeButton(final String postKey){
        final DatabaseReference mLikesDatabase = FirebaseDatabase.getInstance().getReference().child("Likes");
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mLikesDatabase.addValueEventListener(new ValueEventListener() {
            private boolean checkIfUserAlreadyLikeThisPost(DataSnapshot dataSnapshot, String postKey) {
                return dataSnapshot.child(postKey).hasChild(mAuth.getCurrentUser().getUid());
            }
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(checkIfUserAlreadyLikeThisPost(dataSnapshot,postKey))
                    mLikeButton.setImageResource(R.drawable.like_red);
                else
                    mLikeButton.setImageResource(R.drawable.like_grey);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
