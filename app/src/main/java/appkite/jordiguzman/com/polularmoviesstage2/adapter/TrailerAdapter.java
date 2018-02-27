package appkite.jordiguzman.com.polularmoviesstage2.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import appkite.jordiguzman.com.polularmoviesstage2.R;
import appkite.jordiguzman.com.polularmoviesstage2.model.Trailer;


public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerHolder>{

    private static final String TAG_LOG = TrailerAdapter.class.getSimpleName();
    private Trailer[] mTrailer;
    private TextView tv_trailer_adapter;
    private ImageView iv_trailer_image;
    private Context mContext;



    public TrailerAdapter(Trailer[] trailers, Context context){
        this.mTrailer=trailers;
        this.mContext= context;

    }

    @Override
    public TrailerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_trailer, parent, false);

        return new TrailerHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerHolder holder, @SuppressLint("RecyclerView") final int position) {
        String name= mTrailer[position].getmName();
        final String key = mTrailer[position].getmKey();
        tv_trailer_adapter.append(name);
        final String url_base_youtube_video= "https://www.youtube.com/watch?v=";
        String url_base_youtube_image = "http://img.youtube.com/vi/";
        Picasso.with(mContext)
                .load(url_base_youtube_image.concat(key).concat("/0.jpg"))
                .into(iv_trailer_image);
         iv_trailer_image.setOnClickListener(new View.OnClickListener() {
              /*try {
                 return new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
             } catch (ActivityNotFoundException ex) {
                 return new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + id));
             }*/
             @Override
             public void onClick(View v) {
                 String urlVideo= String.valueOf(url_base_youtube_video.concat(key));
                 Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(urlVideo));
                 mContext.startActivity(intent);
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
