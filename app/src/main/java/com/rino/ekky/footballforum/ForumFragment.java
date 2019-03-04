package com.rino.ekky.footballforum;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForumFragment extends Fragment {
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
    String username;
    ValueEventListener listener;

    public ForumFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum, container, false);

        match = getActivity().getIntent().getParcelableExtra("match");
        username = getActivity().getIntent().getStringExtra("username");
        listData = new ArrayList<>();
        listDataKey = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        adapter = new ForumAdapter(getActivity(), match.getId());
        adapter.setUsername(username);

        edtMessage = view.findViewById(R.id.edt_message);
        btnSend = view.findViewById(R.id.btn_send);
        rvMessage = view.findViewById(R.id.rvForum);
        rvMessage.setHasFixedSize(true);
        rvMessage.setLayoutManager(new LinearLayoutManager(getActivity()));

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
                ForumMessage forumMessage = new ForumMessage(edtMessage.getText().toString(), username);

                root.child(tempKey).setValue(forumMessage);

                edtMessage.setText("");
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        root.removeEventListener(listener);
    }
}
