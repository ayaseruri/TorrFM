package ayaseruri.torr.torrfm.controller;

import java.util.ArrayList;

import ayaseruri.torr.torrfm.model.LrcModel;
import ayaseruri.torr.torrfm.objectholder.LrcInfo;

/**
 * Created by ayaseruri on 15/12/20.
 */
public class LrcController {
    private LrcModel lrcModel;

    public LrcController(LrcModel lrcModel) {
        this.lrcModel = lrcModel;
    }

    public void setMusicTimeCurrent(long currentTime){
        for(int i=0; i< lrcModel.getLrcInfos().size(); i++){
            if(i + 1 < lrcModel.getLrcInfos().size()){
                if(lrcModel.getLrcInfos().get(i).getTime() < currentTime
                        && lrcModel.getLrcInfos().get(i + 1).getTime() > currentTime){
                    lrcModel.setIndexCurrent(i);
                }
            }else {
                if(lrcModel.getLrcInfos().get(i).getTime() < currentTime){
                    lrcModel.setIndexCurrent(i);
                }
            }
        }
    }

    public void reset(){
        lrcModel.setLrcInfos(new ArrayList<LrcInfo>());
    }
}
