package pl.nowakprojects.firebaseblog;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Mateusz on 05.01.2017.
 */

class BlogViewHolder extends RecyclerView.ViewHolder {

    View mView;

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

    void setImage(Context context, String image){
        ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);
        Picasso.with(context).load(image).into(post_image);
    }


}
