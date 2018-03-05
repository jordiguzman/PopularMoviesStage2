package appkite.jordiguzman.com.polularmoviesstage2.adapter;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import appkite.jordiguzman.com.polularmoviesstage2.R;
import appkite.jordiguzman.com.polularmoviesstage2.model.Trailer;


public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerHolder>{

    private Trailer[] mTrailer;
    private TextView tv_trailer_adapter;
    private ImageView iv_trailer_image;
    private Context mContext;



    public TrailerAdapter(Trailer[] trailers, Context context){
        this.mTrailer=trailers;
        this.mContext= context;

    }

    @NonNull
    @Override
    public TrailerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_trailer, parent, false);

        return new TrailerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerHolder holder, @SuppressLint("RecyclerView") final int position) {
        String name= mTrailer[position].getmName();
        final String key = mTrailer[position].getmKey();
        tv_trailer_adapter.append(name);
        final String url_base_youtube_video= "https://www.youtube.com/watch?v=";
        String url_base_youtube_image = "http://img.youtube.com/vi/";
        final String url_base_vnd = "vnd.youtube:";
        final String urlVideo= String.valueOf(url_base_youtube_video.concat(key));
        Picasso.with(mContext)
                .load(url_base_youtube_image.concat(key).concat("/0.jpg"))
                .into(iv_trailer_image);
         iv_trailer_image.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Animation shake = AnimationUtils.loadAnimation(mContext, R.anim.shake);
                 v.startAnimation(shake);
                 CountDownTimer countDownTimer= new CountDownTimer(70, 10) {
                     @Override
                     public void onTick(long millisUntilFinished) {
                     }
                     @Override
                     public void onFinish() {
                         Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url_base_vnd.concat(key)));
                         Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlVideo));
                         try {
                             mContext.startActivity(appIntent);
                         } catch (ActivityNotFoundException ex) {
                             mContext.startActivity(webIntent);
                         }
                     }
                 };
                 countDownTimer.start();

             }
         });
    }

    @Override
    public int getItemCount() {
        return mTrailer.length;
    }


    class TrailerHolder extends RecyclerView.ViewHolder {

        TrailerHolder(View itemView) {
            super(itemView);
            tv_trailer_adapter = itemView.findViewById(R.id.tv_trailer_adapter);
            iv_trailer_image = itemView.findViewById(R.id.iv_trailer_image);

        }



    }
}
