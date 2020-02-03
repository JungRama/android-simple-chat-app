package writeit.aclass.Adapter;

/**
 * Created by Gung Rama on 11/26/2017.
 */

public class DataUserAdapter {

    public String image;
    public String username;
    public String status;
    public String thumb_image;

    public DataUserAdapter(){

    }

    public DataUserAdapter(String image, String username, String status , String thumb_image) {
        this.image = image;
        this.username = username;
        this.status = status;
        this.thumb_image = thumb_image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
