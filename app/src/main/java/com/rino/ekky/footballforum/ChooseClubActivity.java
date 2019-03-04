package com.rino.ekky.footballforum;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChooseClubActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Club>> {
    Spinner spinnerLeague;
    RecyclerView rvClub;
    String[] strLeagueCode;
    String strLeague = "2014";
    ArrayList<Club> listData;
    ChooseClubAdapter adapter;
    SharedPreferences preferences;
    FirebaseAuth auth;
    User user;
    boolean change;
    ValueEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_club);

        change = getIntent().getBooleanExtra("change", false);

        preferences = getSharedPreferences("football_forum", Context.MODE_PRIVATE);
        auth = FirebaseAuth.getInstance();
        user = getIntent().getParcelableExtra("user");

        spinnerLeague = findViewById(R.id.spinner_league);
        rvClub = findViewById(R.id.recycler_club);
        rvClub.setHasFixedSize(true);
        rvClub.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ChooseClubAdapter(this);
        strLeagueCode = getResources().getStringArray(R.array.league_code);
        listData = new ArrayList<>();

        rvClub.setAdapter(adapter);
        ItemClickSupport.addTo(rvClub).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                FirebaseUser firebaseUser = auth.getCurrentUser();
                Club selectedClub = listData.get(position);
                user.setClub_id(selectedClub.getId());

                final DatabaseReference root = FirebaseDatabase.getInstance().getReference("user");

                if (change) {
                    listener = root.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                String tempKey = postSnapshot.getKey();
                                User userItem = postSnapshot.getValue(User.class);
                                if (userItem.getId().equals(user.getId())) {
                                    root.child(tempKey).setValue(user, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                            Intent intent = new Intent(ChooseClubActivity.this, JadwalDrawerActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                    root.removeEventListener(listener);
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    String tempKey = root.push().getKey();
                    root.child(tempKey).setValue(user, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            Intent intent = new Intent(ChooseClubActivity.this, JadwalDrawerActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        });

        spinnerLeague.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strLeague = strLeagueCode[position];
                getLoaderManager().restartLoader(0, null, ChooseClubActivity.this);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getSupportActionBar().setTitle("Pilih Klub Favorit");
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<ArrayList<Club>> onCreateLoader(int id, Bundle args) {
        return new ClubAsyncTaskLoader(ChooseClubActivity.this, strLeague);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Club>> loader, ArrayList<Club> data) {
        adapter.setData(data);
        adapter.notifyDataSetChanged();
        listData = data;
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Club>> loader) {
        adapter.setData(null);
    }
}
