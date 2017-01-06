package pl.nowakprojects.firebaseblog;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private RecyclerView mBlogList;

    @Override
    protected void onStart() {
        super.onStart();

        setupRecyclerView();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    private void setupRecyclerView() {
        FirebaseRecyclerAdapter<Blog,BlogViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                       Blog.class, R.layout.blog_row,BlogViewHolder.class,mDatabase
                ) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(MainActivity.this, model.getImage());
            }
        };

        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFirebase();
        initUserInterface();
    }

    private void initFirebase() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("FirebaseBlog");
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (!userIsLogged()) {
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //uzytkownik nie moze wrócić do poprzedniej
                    startActivity(loginIntent);
                }
            }

            private boolean userIsLogged(){
                return mAuth.getCurrentUser() != null;
            }
        };
    }

    private void initUserInterface() {
        mBlogList = (RecyclerView) findViewById(R.id.blog_list);
        mBlogList.setHasFixedSize(true);//dodawania, usuwanie elementów nie zmienia jej rozmiaru
        mBlogList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_add:
                startActivity(new Intent(getApplicationContext(),PostActivity.class));break;
            case R.id.action_logout:
                logout();break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mAuth.signOut();
    }
}
