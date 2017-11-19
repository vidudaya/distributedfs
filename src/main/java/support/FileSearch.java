package support;

import java.util.ArrayList;

/**
 * Created by Samiththa on 4/11/16.
 */
public class FileSearch {

    private Node distributor;

    public FileSearch(Node distributor) {
        this.distributor = distributor;
    }

    public void searchForFiles(String fileName) {
        searchLocalStore(fileName);
        distributor.getNeighbourCommunicationManager().searchFileInNetwork(fileName, distributor);
    }

    public void searchLocalStringStore(String fileName) {
        System.out.println("\nSearching local file store ...");
        ArrayList<String> localStore = distributor.getTextStore().returnAllPartialMatches(fileName);
        System.out.println("Total " + localStore.size() + " records found ");
        if (localStore.size() > 0) {
            String res = "[ ";
            int i = 0;
            for (String file : localStore) {
                res = res + file;
                i++;
                if (i == localStore.size()) {
                    res = res + " ]";
                } else {
                    res = res + ", ";
                }
            }
            System.out.println(res);
        }
    }

    /**
     * Using the new Wall
     * @param fileName
     */
    public void searchLocalStore(String fileName) {
        System.out.println("\nSearching local file store ...");
        ArrayList<FilePost> localStore = distributor.getWall().returnAllPartialMatches(fileName);
        System.out.println("Total " + localStore.size() + " records found ");
        if (localStore.size() > 0) {
            String res = "[ ";
            int i = 0;
            for (FilePost file : localStore) {
                res = res + file.getFileName();
                i++;
                if (i == localStore.size()) {
                    res = res + " ]";
                } else {
                    res = res + ", ";
                }
            }
            System.out.println(res);
        }
    }

}
