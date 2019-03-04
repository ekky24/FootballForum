package com.rino.ekky.footballforum;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class JadwalDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<ArrayList<Match>> {
    RecyclerView rvMatch;
    MatchAdapter adapter;
    ArrayList<Match> listData;
    String clubId;
    DatabaseReference root;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    String type = "FINISHED";
    public static String username;
    TextView txtNoData;
    ValueEventListener userListener;
    SharedPreferences preferences;
    TextView txtHeaderUsername, txtHeaderEmail;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        txtHeaderUsername = headerView.findViewById(R.id.header_username);
        txtHeaderEmail = headerView.findViewById(R.id.header_email);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        preferences = getSharedPreferences("football_forum", Context.MODE_PRIVATE);

        final SharedPreferences.Editor editor = preferences.edit();

        root = FirebaseDatabase.getInstance().getReference("user");

        userListener = root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listData.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);
                    if (user.getEmail().equals(firebaseUser.getEmail())) {
                        clubId = user.getClub_id();
                        username = user.getUsername();
                        txtHeaderUsername.setText(username);

                        getLoaderManager().initLoader(1, null, JadwalDrawerActivity.this);
                        root.removeEventListener(userListener);

                        editor.putString("club_id", clubId);
                        editor.apply();
                        break;
                    }
                }

                adapter.setListData(listData);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        txtNoData = findViewById(R.id.no_data);
        progressBar = findViewById(R.id.progress_bar);
        rvMatch = findViewById(R.id.recycler_match);
        rvMatch.setHasFixedSize(true);

        listData = new ArrayList<>();
        adapter = new MatchAdapter(getApplicationContext());

        txtHeaderEmail.setText(firebaseUser.getEmail());

        ItemClickSupport.addTo(rvMatch).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Match selectedMatch = listData.get(position);
                Intent intent = new Intent(JadwalDrawerActivity.this, ContainerTabActivity.class);
                intent.putExtra("match", selectedMatch);
                intent.putExtra("username", username);
                intent.putExtra("type", type);
                startActivity(intent);
                //Toast.makeText(JadwalDrawerActivity.this, selectedMatch.getUtcDate(), Toast.LENGTH_SHORT).show();
            }
        });

        getSupportActionBar().setTitle("Finished Match");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Edit Message");
        alertDialog.setMessage("Are you sure you want to exit?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                JadwalDrawerActivity.super.onBackPressed();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.jadwal_finished) {
            type = "FINISHED";
            getLoaderManager().restartLoader(1, null, JadwalDrawerActivity.this);
            getSupportActionBar().setTitle("Finished Match");
        } else if (id == R.id.jadwal_live) {
            type = "LIVE";
            getLoaderManager().restartLoader(1, null, JadwalDrawerActivity.this);
            getSupportActionBar().setTitle("Live Match");
        } else if (id == R.id.jadwal_scheduled) {
            type = "SCHEDULED";
            getLoaderManager().restartLoader(1, null, JadwalDrawerActivity.this);
            getSupportActionBar().setTitle("Scheduled Match");
        } else if (id == R.id.change_club) {
            User user = new User(firebaseUser.getUid(), firebaseUser.getEmail(), username);

            Intent intent = new Intent(this, ChooseClubActivity.class);
            intent.putExtra("change", true);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.signout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public Loader<ArrayList<Match>> onCreateLoader(int id, Bundle args) {
        txtNoData.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        return new MatchAsyncTaskLoader(getApplicationContext(), clubId, type);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Match>> loader, ArrayList<Match> data) {
        progressBar.setVisibility(View.INVISIBLE);
        if (data.size() > 0) {
            rvMatch.setLayoutManager(new LinearLayoutManager(this));
            adapter.setListData(data);
            rvMatch.setAdapter(adapter);
            listData = data;
            txtNoData.setVisibility(View.INVISIBLE);
        }
        else {
            adapter.setListData(data);
            rvMatch.setAdapter(adapter);
            txtNoData.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Match>> loader) {
        adapter.setListData(null);
    }

}
