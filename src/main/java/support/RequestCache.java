package support;

import java.util.Arrays;

/**
 * Created by Samiththa on 4/11/16.
 */
public class RequestCache {
    final int LIMIT = 10;
    int pointer;
    // need an identifier to identify a message for search request
    private String[] cache;

    public RequestCache() {
        this.cache = new String[LIMIT];
        pointer = 0;
    }

    public boolean isPossibleDuplicate(String id) {
        for (String s : cache) {
            if (id.trim().equals(s)) {
                return true;
            }
        }
        return false;
    }

    public void addToCache(String id) {
        cache[pointer % LIMIT] = id;
        pointer = (pointer + 1) % LIMIT;

        System.out.println("Request cache : " + Arrays.toString(cache));
    }

}
