package hcmute.edu.androidck.Model;

import com.google.firebase.database.Exclude;

public class File {
    private String nameFile;
    private String imageUrl;
    private String mKey;

    public File(){

    }

    public File(String nameFile, String imageUrl) {
        if(nameFile.trim().equals("")){
            nameFile = "No name";
        }
        this.nameFile = nameFile;
        this.imageUrl = imageUrl;
    }

    public String getNameFile() {
        return nameFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Exclude
    public String getKey() {
        return mKey;
    }
    @Exclude
    public void setKey(String mKey) {
        this.mKey = mKey;
    }
}
