package dhakchianandan.waves.model;

/**
 * Created by Dhakchianandan on 02-07-2015.
 */
public class Radio {
    private String name;
    private String url;
    private int image;

    public Radio() {
    }

    public Radio(String name, String url, int image) {
        this.url = url;
        this.name = name;
        this.image = image;
    }

    public Radio(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Radio{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", image=" + image +
                '}';
    }
}
