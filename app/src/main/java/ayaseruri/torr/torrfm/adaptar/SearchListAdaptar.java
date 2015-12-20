package ayaseruri.torr.torrfm.adaptar;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import ayaseruri.torr.torrfm.R;
import ayaseruri.torr.torrfm.objectholder.SongInfo;

/**
 * Created by ayaseruri on 15/12/19.
 */
public class SearchListAdaptar extends RecyclerView.Adapter<SearchListAdaptar.ViewHolder> {

    private List<SongInfo> songInfos;
    private Context mContext;
    private ItemClick itemClick;

    public SearchListAdaptar(Context mContext, List<SongInfo> songInfos, ItemClick itemClick) {
        this.songInfos = songInfos;
        this.mContext = mContext;
        this.itemClick = itemClick;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.search_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        SimpleDraweeView mucisCover = (SimpleDraweeView)holder.itemView.findViewById(R.id.music_cover);
        TextView title = (TextView)holder.itemView.findViewById(R.id.title);
        TextView artistName = (TextView)holder.itemView.findViewById(R.id.artist_name);

        final SongInfo currentSongInfo = songInfos.get(position);
        if(null != currentSongInfo.getImg()){
            mucisCover.setImageURI(Uri.parse(currentSongInfo.getImg()));
        }
        title.setText(currentSongInfo.getTitle());
        artistName.setText(currentSongInfo.getArtist_name());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClick.onSearchItemClick(position, currentSongInfo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songInfos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface ItemClick{
        void onSearchItemClick(int postion, SongInfo songInfo);
    }
}
