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
import ayaseruri.torr.torrfm.objectholder.ChannelInfo;

/**
 * Created by ayaseruri on 15/12/11.
 */
public class NavigationAdapar extends RecyclerView.Adapter<NavigationAdapar.ViewHolder>{
    private List<ChannelInfo> mChannelInfos;
    private Context mContext;
    private IItemClick iItemClick;

    public NavigationAdapar(Context context, List<ChannelInfo> mChannelInfos, IItemClick iItemClick) {
        this.mContext = context;
        this.mChannelInfos = mChannelInfos;
        this.iItemClick = iItemClick;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.navigation_recycler_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        SimpleDraweeView bg = (SimpleDraweeView)holder.itemView.findViewById(R.id.item_bg);
        TextView title = (TextView)holder.itemView.findViewById(R.id.title);

        ChannelInfo channelInfo = mChannelInfos.get(position);
        if(null != channelInfo.getImg()){
            bg.setImageURI(Uri.parse(channelInfo.getImg()));
        }
        if(null != channelInfo.getTitle()){
            title.setText(channelInfo.getTitle());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iItemClick.onItemClick(position, mChannelInfos.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mChannelInfos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface IItemClick{
        void onItemClick(int postion, ChannelInfo channelInfo);
    }
}
