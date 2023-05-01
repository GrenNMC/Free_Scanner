package hcmute.edu.androidck.Model;

import java.io.Serializable;

public class Folder implements Serializable {
    private int idFolder;
    private String nameFolder;

    public Folder(){}
    public Folder(int idFolder, String nameFolder) {
        this.idFolder = idFolder;
        this.nameFolder = nameFolder;
    }

    public int getIdFolder() {
        return idFolder;
    }

    public void setIdFolder(int idFolder) {
        this.idFolder = idFolder;
    }

    public String getNameFolder() {
        return nameFolder;
    }

    public void setNameFolder(String nameFolder) {
        this.nameFolder = nameFolder;
    }
}
