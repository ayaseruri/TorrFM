package ayaseruri.torr.torrfm.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ayaseruri.torr.torrfm.objectholder.LrcInfo;

/**
 * Created by ayaseruri on 15/12/12.
 */
public class Util {
    private static final Pattern lrclineFormate = Pattern.compile("^\\[.+\\].{0,}$", Pattern.MULTILINE);
    private static final Pattern lrcTimeFormate = Pattern.compile("\\[\\d+[.:]\\d+[.:]\\d+\\]");

    public static String FormatMusicTime(int ms) {
        Date date = new Date(ms);
        return new SimpleDateFormat("mm:ss").format(date);
    }

    public static ArrayList<LrcInfo> decodeLrc(String rawString) throws Exception {
        ArrayList<LrcInfo> lrcInfos = new ArrayList<>();
        Matcher lineMatcher = lrclineFormate.matcher(rawString);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        while (lineMatcher.find()){
            Matcher timeMatcher = lrcTimeFormate.matcher(lineMatcher.group());
            while (timeMatcher.find()){
                String rawTime = timeMatcher.group().replaceAll("\\.", ":").replaceAll("\\[|\\]", "");
                String[] times = rawTime.split(":");
                long ms = 0;
                if(3 == times.length){
                    ms += Integer.valueOf(times[0]) * 60 * 1000;
                    ms += Integer.valueOf(times[1]) * 1000;
                    ms += Integer.valueOf(times[2]);
                    LrcInfo lrcInfo = new LrcInfo(ms, lineMatcher.group().replaceAll("\\[\\d+[.:]\\d+[.:]\\d+\\]", ""), "");
                    lrcInfos.add(lrcInfo);
                }else {
                    throw new Exception("歌词时间格式不正确");
                }
            }
        }
        return lrcInfos;
    }
//
//    public static ArrayList<LrcInfo> decodeLrc(File rawFile){
//
//    }
}
