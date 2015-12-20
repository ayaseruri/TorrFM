package ayaseruri.torr.torrfm.controller;

import ayaseruri.torr.torrfm.model.LrcModel;

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
}
