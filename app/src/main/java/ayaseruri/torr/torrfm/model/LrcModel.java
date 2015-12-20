package ayaseruri.torr.torrfm.model;

import java.util.ArrayList;
import java.util.List;

import ayaseruri.torr.torrfm.objectholder.LrcInfo;

/**
 * Created by ayaseruri on 15/12/12.
 */
public class LrcModel {
    private int indexCurrent;
    private List<ILrc> iLrcs;
    private ArrayList<LrcInfo> lrcInfos;

    public LrcModel() {
        indexCurrent = 0;
        lrcInfos = new ArrayList<>();
        iLrcs = new ArrayList<>();
    }

    public void addILrc(ILrc iLrc) {
        this.iLrcs.add(iLrc);
    }

    public int getIndexCurrent() {
        return indexCurrent;
    }

    public void setIndexCurrent(int indexCurrent) {
        this.indexCurrent = indexCurrent;
        update();
    }

    public void setLrcInfos(ArrayList<LrcInfo> lrcInfos) {
        this.lrcInfos = lrcInfos;
        update();
    }

    public ArrayList<LrcInfo> getLrcInfos() {
        return lrcInfos;
    }

    private void update() {
        for (ILrc iLrc : iLrcs) {
            iLrc.onLrcUpdate(this);
        }
    }

    public interface ILrc {
        void onLrcUpdate(LrcModel lrcModel);
    }
}
