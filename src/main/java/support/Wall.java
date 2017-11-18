package support;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by vidudaya on 18/11/17.
 * <p/>
 * This is a store of the movies
 */
public class Wall {
    private Map<String,FilePost> files;

    public Wall(Map<String,FilePost> files) {
        this.files = files;
    }

    public Map<String, FilePost> getFiles() {
        return files;
    }

    public void setFiles(Map<String, FilePost> files) {
        this.files = files;
    }

    public ArrayList<FilePost> returnAllPartialMatches(String file) {
        ArrayList<FilePost> matches = new ArrayList<FilePost>();
        String pattern = "(.*)" + file.toLowerCase() + "(.*)";

        for (FilePost s : files.values()) {
            if (s.getFileName().toLowerCase().matches(pattern)) {
                matches.add(s);
            }
        }
        return matches;
    }

    public void printFileList() {
        System.out.println("#####################################################");
        for (FilePost file : files.values()) {
            System.out.println("[" + file.getId() + "] " + file.getFileName());
        }
        System.out.println("#####################################################");
    }
}
