package com.rino.ekky.footballforum;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ForumActivity extends AppCompatActivity {
    EditText edtMessage;
    Button btnSend;
    RecyclerView rvMessage;
    Match match;
    DatabaseReference root;
    DatabaseReference root2;
    ArrayList<ForumMessage> listData;
    ArrayList<String> listDataKey;
    ForumAdapter adapter;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    ValueEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        match = getIntent().getParcelableExtra("match");
        listData = new ArrayList<>();
        listDataKey = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        adapter = new ForumAdapter(this, match.getId());
        adapter.setUsername(JadwalDrawerActivity.username);

        edtMessage = findViewById(R.id.edt_message);
        btnSend = findViewById(R.id.btn_send);
        rvMessage = findViewById(R.id.rvForum);
        rvMessage.setHasFixedSize(true);
        rvMessage.setLayoutManager(new LinearLayoutManager(this));

        root = FirebaseDatabase.getInstance().getReference(match.getId());
        listener = root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listData.clear();
                listDataKey.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ForumMessage message = postSnapshot.getValue(ForumMessage.class);
                    listData.add(message);
                    listDataKey.add(postSnapshot.getKey());
                }

                adapter.setListData(listData);
                adapter.setListDataKey(listDataKey);
                rvMessage.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempKey = root.push().getKey();
                ForumMessage forumMessage = new ForumMessage(edtMessage.getText().toString(), JadwalDrawerActivity.username);

                root.child(tempKey).setValue(forumMessage);

                edtMessage.setText("");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        root.removeEventListener(listener);
    }
}
