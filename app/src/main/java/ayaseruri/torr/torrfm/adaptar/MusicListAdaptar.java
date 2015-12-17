package ayaseruri.torr.torrfm.adaptar;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ayaseruri.torr.torrfm.R;
import ayaseruri.torr.torrfm.objectholder.SongInfo;

/**
 * Created by ayaseruri on 15/12/11.
 */
public class MusicListAdaptar extends RecyclerView.Adapter<MusicListAdaptar.ViewHolder> {
    private List<SongInfo> mSongInfos;
    private Context mContext;
    private IItemAction iItemAction;

    public MusicListAdaptar(Context mContext, List<SongInfo> mSongInfos, IItemAction iItemAction) {
        this.mSongInfos = mSongInfos;
        this.mContext = mContext;
        this.iItemAction = iItemAction;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.music_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        TextView musicName = (TextView) holder.itemView.findViewById(R.id.music_name);
        musicName.setText(mSongInfos.get(position).getTitle());
        ImageView musicDelete = (ImageView) holder.itemView.findViewById(R.id.music_delete_icon);
        musicDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iItemAction.onItemDelete(position, mSongInfos.get(position));
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iItemAction.onItemClick(position, mSongInfos.get(position));
            }
        });
        ImageView nowPlaying = (ImageView)holder.itemView.findViewById(R.id.music_playing_icon);

        if(mSongInfos.get(position).isPlaying()){
            nowPlaying.setVisibility(View.VISIBLE);
            nowPlaying.setColorFilter(R.color.colorAccent);
            musicName.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
            musicDelete.setColorFilter(R.color.colorAccent);
        }else {
            nowPlaying.setVisibility(View.GONE);
            musicName.setTextColor(mContext.getResources().getColor(R.color.favouriteSongsDialogTextColor));
            musicDelete.setColorFilter(Color.parseColor("#55000000"));
        }
    }

    @Override
    public int getItemCount() {
        return mSongInfos.size();
    }

    public interface IItemAction {
        void onItemClick(int postion, SongInfo songInfo);

        void onItemDelete(int postion, SongInfo songInfo);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
