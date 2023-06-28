package com.example.SimpleNotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    FloatingActionButton addNoteBtn;
    RecyclerView recyclerView;
    ImageButton menuBtn;
    NoteAdapter noteAdapter;

    TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Button btn_logout = findViewById(R.id.main_logout);

        //btn_logout.setOnClickListener(v -> {
            //FirebaseAuth.getInstance().signOut();
            //Intent intent = new Intent(this, login.class);
            //startActivity(intent);
            //finish();
        //});

        addNoteBtn = findViewById(R.id.add_note_btn);
        recyclerView = findViewById(R.id.recycler_view);
        menuBtn = findViewById(R.id.menu_btn);

        emptyView = findViewById(R.id.page_message_empty);

        addNoteBtn.setOnClickListener((v -> startActivity(new Intent(MainActivity.this, notesdetails.class))));
        menuBtn.setOnClickListener((v -> showMenu()));
        setupRecyclerView();

    }

    void showMenu(){
        PopupMenu popupMenu = new PopupMenu(MainActivity.this,menuBtn);
        popupMenu.getMenu().add("Logout");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getTitle()=="Logout"){
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this,login.class));
                    finish();
                    return true;
                }
                return false;
            }
        });
    }

    void setupRecyclerView(){
        Query query = Utility.getCollectionReferenceForNotes().orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>().setQuery(query,Note.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(options, this);
        recyclerView.setAdapter(noteAdapter);
        //messageUpdate();
    }

    void messageUpdate(){
        if (recyclerView.getAdapter() == null || recyclerView.getAdapter().getItemCount() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else if(recyclerView.getAdapter() != null || recyclerView.getAdapter().getItemCount() < 0){
            emptyView.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        noteAdapter.startListening();
        //messageUpdate();
        //TextView msg = findViewById(R.id.main_msg);

        if(currentUser != null){
            String str = currentUser.getEmail();
            str = "Welcome " + str + " to your notes!";
            //msg.setText(str);
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        noteAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        noteAdapter.notifyDataSetChanged();
        //messageUpdate();
    }

}