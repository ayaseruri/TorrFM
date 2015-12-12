package ayaseruri.torr.torrfm.adaptar;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ayaseruri.torr.torrfm.R;

/**
 * Created by ayaseruri on 15/12/12.
 */
public class MusicLrcAdaptar extends RecyclerView.Adapter<MusicLrcAdaptar.ViewHolder>{
    private Context mContext;
    private List<LrcHolder> lines;

    public MusicLrcAdaptar(Context mContext, List<LrcHolder> lines) {
        this.mContext = mContext;
        this.lines = lines;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.lrc_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView jp = (TextView)holder.itemView.findViewById(R.id.lrc_jp);
        TextView cn = (TextView)holder.itemView.findViewById(R.id.lrc_cn);

        jp.setText(lines.get(position).getJp());
        cn.setText(lines.get(position).getCn());
    }

    @Override
    public int getItemCount() {
        return lines.size();
    }

    public List<LrcHolder> getLines() {
        return lines;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class LrcHolder{
        private String time;
        private String cn;
        private String jp;

        public LrcHolder(String time, String jp, String cn) {
            this.time = time;
            this.cn = cn;
            this.jp = jp;
        }

        public String getTime() {
            return time;
        }

        public String getCn() {
            return cn;
        }

        public String getJp() {
            return jp;
        }
    }
}
