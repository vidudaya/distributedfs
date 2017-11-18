package support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by vidudaya on 4/11/16.
 */
public class CommonSupport {

    private final String BLANK = " ";
    private Node node;

    public CommonSupport(Node node) {
        this.node = node;
    }

    public String getFormattedNumber(int number, int pad) {
        String formatted = String.format("%0" + pad + "d", number);
        return formatted;
    }

    public String getUniqueId() {
        String chars[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
                , "a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
        String id = "";
        Random rand = new Random();
        for (int i = 0; i < 8; ++i) {
            id = id.concat(chars[rand.nextInt(20)]);
        }
        return id;
    }

    public ArrayList<String> getRandomFileList() throws IOException {
        ArrayList<String> list = new ArrayList<String>();
        InputStream is = null;
        BufferedReader br = null;
        try {
            is = this.getClass().getResource("/filenames").openStream();
            br = new BufferedReader(new InputStreamReader(is));
            int i = 0;
            Random rand = new Random();
            while (br.ready() && i < 5) {
                String temp = br.readLine();
                if (rand.nextInt(10) % 3 == 0) {
                    continue;
                }
                if ((rand.nextInt(10) + list.size()) % 2 == 0) {
                    list.add(temp);
                    //System.out.println("file : " + temp);
                    ++i;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                br.close();
            }
            if (is != null) {
                is.close();
            }
        }
        return list;
    }

    public String generateMessageToSend(String... args) {
        String messageToSend = "";
        String message = "";

        for (String value : args) {
            message = message.concat(BLANK).concat(value);
        }
        message = message.trim();
        int length = message.length() + 5;
        messageToSend = getFormattedNumber(length, 4).concat(BLANK).concat(message);

        return messageToSend;
    }

    public String getCombinedStringOfList(ArrayList<String> list) {
        String messageToSend = "";
        for (String value : list) {
            value = value.trim().replaceAll(" ", "_"); // for make a single continues string
            messageToSend = messageToSend.concat(BLANK).concat(value);
        }

        return messageToSend.trim();
    }

    public Map<String, FilePost> getRandomFilePostMap() throws IOException {
        ArrayList<String> list = getRandomFileList();
        Map<String, FilePost> fpMap = new HashMap<String, FilePost>();
        for (String s : list) {
            FilePost fp = new FilePost(s, node);
            fpMap.put(fp.getId(), fp);
        }
        return fpMap;
    }
}
