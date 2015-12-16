package ayaseruri.torr.torrfm.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ayaseruri on 15/12/12.
 */
public class Util {
    public static String FormatMusicTime(int ms) {
        Date date = new Date(ms);
        return new SimpleDateFormat("mm:ss").format(date);
    }

//    public static ArrayList<LrcInfo> decodeLrc(String rawString){
//
//    }
//
//    public static ArrayList<LrcInfo> decodeLrc(File rawFile){
//
//    }
}
