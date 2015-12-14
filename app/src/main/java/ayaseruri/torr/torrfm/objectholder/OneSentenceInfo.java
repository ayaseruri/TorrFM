package ayaseruri.torr.torrfm.objectholder;

/**
 * Created by ayaseruri on 15/12/14.
 */
public class OneSentenceInfo {

    /**
     * hitokoto : 我想要握紧的并不是匕首或是什么，只不过是他的掌心而已。
     * cat : a
     * author : 万事屋神乐酱
     * source : 空之境界
     * like : 10
     * date : 2011.11.24 19:26:59
     * catname : Anime - 动画
     * id : 1322134019000
     */

    private String hitokoto;
    private String cat;
    private String author;
    private String source;
    private int like;
    private String date;
    private String catname;
    private long id;

    public void setHitokoto(String hitokoto) {
        this.hitokoto = hitokoto;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCatname(String catname) {
        this.catname = catname;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHitokoto() {
        return hitokoto;
    }

    public String getCat() {
        return cat;
    }

    public String getAuthor() {
        return author;
    }

    public String getSource() {
        return source;
    }

    public int getLike() {
        return like;
    }

    public String getDate() {
        return date;
    }

    public String getCatname() {
        return catname;
    }

    public long getId() {
        return id;
    }
}
