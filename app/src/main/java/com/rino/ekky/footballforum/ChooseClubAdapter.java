package com.rino.ekky.footballforum;

import android.content.Context;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestBuilder;;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.InputStream;
import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ChooseClubAdapter extends RecyclerView.Adapter<ChooseClubAdapter.ClubHolder> {
    private ArrayList<Club> data = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;
    private RequestBuilder<PictureDrawable> requestBuilder;

    public ChooseClubAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(ArrayList<Club> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ClubHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.club_choose_item, parent, false);
        return new ClubHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClubHolder holder, int position) {
        holder.txtClubName.setText(data.get(position).getName());
        String tempUrl = data.get(position).getLogoUrl();
        String substrTemp = tempUrl.substring(tempUrl.length()-3);
        if (substrTemp.equals("svg")) {
            Log.e("svg_tes", tempUrl);
            requestBuilder = GlideApp.with(context)
                    .as(PictureDrawable.class)
                    .transition(withCrossFade())
                    .listener(new SvgSoftwareLayerSetter());

            Uri uri = Uri.parse(tempUrl);
            requestBuilder
                    .load(uri)
                    .into(holder.imgCubLogo);
        }
        else {
            Glide.with(context)
                    .load(tempUrl)
                    .into(holder.imgCubLogo);
        }

        /*Picasso.with(context)
                .load(data.get(position).getLogoUrl())
                .into(holder.imgCubLogo);*/
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ClubHolder extends RecyclerView.ViewHolder {
        private ImageView imgCubLogo;
        private TextView txtClubName;

        public ClubHolder(View itemView) {
            super(itemView);
            imgCubLogo = itemView.findViewById(R.id.img_club_logo);
            txtClubName = itemView.findViewById(R.id.txt_club_name);
        }
    }
}
