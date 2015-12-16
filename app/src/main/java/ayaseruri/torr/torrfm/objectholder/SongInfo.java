package ayaseruri.torr.torrfm.objectholder;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by ayaseruri on 15/12/11.
 */
@DatabaseTable
public class SongInfo {

    /**
     * sid : 246
     * uid : 0
     * title : もってけ!セーラーふく[グルコサミっくす]
     * url : http://www.xiami.com/song/1769402184
     * img : http://img.xiami.net/images/album/img0/62500/3711251268103030_4.jpg?v=bgblur
     * album_name : もってけ!セーラーふく Re-Mix001 -7 burni
     * artist_name : 日本ACG
     * text :
     * src : http://ww.danmu.fm:233/aac/1769402184.m4a
     * length : 257
     */
    @DatabaseField(generatedId = true)
    private int id;
    private int sid;
    private int uid;
    @DatabaseField
    private String title;
    private String url;
    @DatabaseField
    private String img;
    private String album_name;
    @DatabaseField
    private String artist_name;
    private String text;
    @DatabaseField
    private String src;
    private int length;
    @DatabaseField
    private long time;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setAlbum_name(String album_name) {
        this.album_name = album_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getSid() {
        return sid;
    }

    public int getUid() {
        return uid;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getImg() {
        return img;
    }

    public String getAlbum_name() {
        return album_name;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public String getText() {
        return text;
    }

    public String getSrc() {
        return src;
    }

    public int getLength() {
        return length;
    }
}
