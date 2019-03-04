package com.rino.ekky.footballforum;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import java.util.Calendar;

public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ForumViewHolder> {
    Context context;
    private LayoutInflater inflater;
    ArrayList<ForumMessage> listData;
    ArrayList<String> listDataKey;
    String matchId;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    String username;

    public ForumAdapter(Context context, String matchId) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listData = new ArrayList<>();
        listDataKey = new ArrayList<>();
        this.matchId = matchId;

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
    }

    public void setListData(ArrayList<ForumMessage> listData) {
        this.listData = listData;
    }

    public void setListDataKey(ArrayList<String> listDataKey) {
        this.listDataKey = listDataKey;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @NonNull
    @Override
    public ForumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forum_item, parent, false);
        return new ForumAdapter.ForumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForumViewHolder holder, int position) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(listData.get(position).getMessageTime());
        String tempDate = calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.MONTH) +
                "-" + calendar.get(Calendar.YEAR);

        holder.txtUsername.setText(listData.get(position).getMessageUser());
        holder.txtMessage.setText(listData.get(position).getMessageText());
        holder.txtDate.setText(tempDate);
        holder.btnEdit.setOnClickListener(new CustomOnItemClickListener(position, new CustomOnItemClickListener.OnItemClickCallback() {
            @Override
            public void onItemClicked(View view, final int position) {
                final EditText input = new EditText(context);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                input.setText(listData.get(position).getMessageText());

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Edit Message");
                alertDialog.setView(input);
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listData.get(position).setMessageText(input.getText().toString());

                        DatabaseReference root = FirebaseDatabase.getInstance().getReference(matchId);
                        root.child(listDataKey.get(position)).setValue(listData.get(position));
                    }
                });
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
                //Toast.makeText(context, listData.get(position).getMessageText(), Toast.LENGTH_SHORT).show();
            }
        }));

        holder.btnDelete.setOnClickListener(new CustomOnItemClickListener(position, new CustomOnItemClickListener.OnItemClickCallback() {
            @Override
            public void onItemClicked(View view, final int position) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Edit Message");
                alertDialog.setMessage("Are you sure you want to delete this?");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference root = FirebaseDatabase.getInstance().getReference(matchId);
                        root.child(listDataKey.get(position)).removeValue();
                        listData.remove(position);
                        listDataKey.remove(position);
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
        }));

        if(!username.equals(listData.get(position).getMessageUser())) {
            holder.btnEdit.setEnabled(false);
            holder.btnDelete.setEnabled(false);
            holder.btnEdit.setVisibility(View.INVISIBLE);
            holder.btnDelete.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ForumViewHolder extends RecyclerView.ViewHolder {
        private TextView txtUsername, txtDate, txtMessage;
        private ImageButton btnEdit, btnDelete;

        public ForumViewHolder(View itemView) {
            super(itemView);
            txtUsername = itemView.findViewById(R.id.txt_username);
            txtDate = itemView.findViewById(R.id.txt_message_date);
            txtMessage = itemView.findViewById(R.id.txt_message);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_hapus);
        }
    }
}
