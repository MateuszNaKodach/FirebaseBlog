package pl.nowakprojects.firebaseblog;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by Mateusz on 05.01.2017.
 */

class BlogViewHolder extends RecyclerView.ViewHolder {

    private View mView;

    public BlogViewHolder(View itemView) {
        super(itemView);

        mView = itemView;
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
}
