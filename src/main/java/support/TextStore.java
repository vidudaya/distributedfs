package support;

import java.util.ArrayList;

/**
 * Created by Samiththa on 4/11/16.
 */
public class TextStore {
    private ArrayList<String> files;


    public TextStore(ArrayList<String> files) {
        this.files = files;
    }

    public ArrayList<String> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<String> files) {
        this.files = files;
    }

    public boolean isContains(String file) {
        return files.contains(file.toLowerCase().trim());
    }

    public ArrayList<String> returnAllPartialMatches(String file) {
        ArrayList<String> matches = new ArrayList<String>();
        String pattern = "(.*)" + file.toLowerCase() + "(.*)";

        for (String s : files) {
            if (s.toLowerCase().matches(pattern)) {
                matches.add(s);
            }
        }
        return matches;
    }

    public void printFileList() {
        System.out.println("#####################################################");
        for (String file : files) {
            System.out.println(file);
        }
        System.out.println("#####################################################");
    }
}
