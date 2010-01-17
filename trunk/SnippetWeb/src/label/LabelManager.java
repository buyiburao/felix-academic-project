package label;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import search.object.Document;
import search.object.Sentence;
import search.snippet.MysqlDriver;
import search.snippet.Record;

public class LabelManager {

    private static final String HOST = "192.168.3.19";
    public static final String USERNAME_COOKIE_NAME = "username";

    public static PageInfo findNewTrainingItem(String username)
            throws Exception {
        MysqlDriver driver = getMysqlDriver();
        driver.connect();

        List<String> queries = new ArrayList<String>(driver.getQuerySet(true));
        Collections.shuffle(queries, new Random());

        PageInfo ret = null;
        while (ret == null && !queries.isEmpty()) {
            String query = queries.remove(0);
            List<Record> records = driver.getRecord(query, true);
            Collections.shuffle(records, new Random());
            for (Record record : records) {
                String url = record.getUrl();

                if (!driver.isTrainingLabeled(query, url, username)) {
                    String page = driver.getPage(url);
                    if (!page.isEmpty()) {
                        List<Sentence> sentences = new Document(page).getSentences();
                        List<String> list = new ArrayList<String>(sentences.size());
                        for (Sentence sentence : sentences) {
                            list.add(sentence.getString());
                        }

                        Map<String, String> translation = driver.getTranslation(url);
                        ret = new PageInfo(record, list, translation);
                        break;
                    }
                }
            }
        }

        driver.disconnect();

        return ret;
    }

    private static MysqlDriver getMysqlDriver() {
        return new MysqlDriver(HOST, 3306, "snippet", "working");
    }

    public static void storeLabel(String query, String url, String sentence,
            String user, int rank) throws Exception {
        MysqlDriver driver = getMysqlDriver();
        driver.connect();
        driver.insertTraining(query, url, sentence, user, rank);
        driver.disconnect();

        System.out.println(query);
        System.out.println(url);
        System.out.println(sentence);
        System.out.println(user);
        System.out.println(rank);
    }

    public static String getUserName(HttpServletRequest request) {
        String ret = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (USERNAME_COOKIE_NAME.equals(cookie.getName())) {
                    ret = cookie.getValue();
                    break;
                }
            }
        }

        return ret;
    }

    public static void updateUserName(HttpServletResponse response,
            String username) {
        Cookie cookie = new Cookie(USERNAME_COOKIE_NAME, username);
        cookie.setMaxAge(Integer.MAX_VALUE);
        response.addCookie(cookie);
    }

    public static void removeUserName(HttpServletResponse response) {
        Cookie cookie = new Cookie(USERNAME_COOKIE_NAME, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
