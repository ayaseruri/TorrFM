package ayaseruri.torr.torrfm.objectholder;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by ayaseruri on 15/12/11.
 */
@DatabaseTable
public class ChannelInfo {

    /**
     * hid : 11
     * title : 偶像大师
     * img : http://itorr.sinaapp.com/fm/img/r/10.jpg
     * text :  IM@S
     * data :
     * num : 0
     * play : 0
     * look : 0
     * created : 0
     * modified : 0
     */
    @DatabaseField(id = true)
    private String hid;
    @DatabaseField
    private String title;
    @DatabaseField
    private String img;
    private String text;
    private String data;
    private String num;
    private String play;
    private String look;
    private String created;
    private String modified;

    public void setHid(String hid) {
        this.hid = hid;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void setPlay(String play) {
        this.play = play;
    }

    public void setLook(String look) {
        this.look = look;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getHid() {
        return hid;
    }

    public String getTitle() {
        return title;
    }

    public String getImg() {
        return img;
    }

    public String getText() {
        return text;
    }

    public String getData() {
        return data;
    }

    public String getNum() {
        return num;
    }

    public String getPlay() {
        return play;
    }

    public String getLook() {
        return look;
    }

    public String getCreated() {
        return created;
    }

    public String getModified() {
        return modified;
    }
}
