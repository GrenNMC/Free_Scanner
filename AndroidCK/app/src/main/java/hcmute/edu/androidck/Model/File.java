package hcmute.edu.androidck.Model;

public class File {
    private String nameFile;
    private String imageUrl;

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
}
