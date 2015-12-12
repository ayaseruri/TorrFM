package ayaseruri.torr.torrfm.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ayaseruri on 15/12/12.
 */
public class LrcModel {
    private int timeCurrent;
    private List<ILrc> iLrcs;

    public LrcModel() {
        iLrcs = new ArrayList<>();
    }

    public void addILrc(ILrc iLrc){
        this.iLrcs.add(iLrc);
    }

    public int getTimeCurrent() {
        return timeCurrent;
    }

    public void setTimeCurrent(int timeCurrent) {
        this.timeCurrent = timeCurrent;
        update();
    }

    private void update(){
        for(ILrc iLrc : iLrcs){
            iLrc.onMusicTimeCurrentChange(this);
        }
    }

    public interface ILrc{
        void onMusicTimeCurrentChange(LrcModel lrcModel);
    }
}
