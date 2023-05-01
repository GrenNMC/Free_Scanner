package hcmute.edu.androidck.Model;

public class File {
    private int idFile;
    private String nameFile;
    private int isFoler;

    public File(){

    }

    public File(int idFile, String nameFile, int isFoler) {
        this.idFile = idFile;
        this.nameFile = nameFile;
        this.isFoler = isFoler;
    }

    public int getIdFile() {
        return idFile;
    }

    public void setIdFile(int idFile) {
        this.idFile = idFile;
    }

    public String getNameFile() {
        return nameFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }

    public int getIsFoler() {
        return isFoler;
    }

    public void setIsFoler(int isFoler) {
        this.isFoler = isFoler;
    }
}
