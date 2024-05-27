package com.oop.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import javafx.scene.control.Alert;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.oop.exception.NetworkException;
import com.oop.exception.ServerNoResponseException;
import com.oop.model.Item;

public class APICaller {
    // Lấy gợi ý
    public static List<String> querySuggest(String input)
            throws URISyntaxException, IOException, ParseException, ServerNoResponseException {

        List<String> res = new ArrayList<>(); // Khai báo đối tượng kiểu tổng quát
        StringBuffer content = connectAndGetRawData("GET", "http://localhost:8000/suggestion?data=", input);
        JSONParser parser = new JSONParser();
        JSONObject jo = (JSONObject) parser.parse(content.toString());
        JSONArray ja = (JSONArray) jo.get("result");
        for (Object o : ja) {
            res.add((String) o);
        }
        return res;
    }

    // Lấy tìm kiếm
    public static List<Item> getSearchResult(String input) throws ParseException, IOException, URISyntaxException, ServerNoResponseException {
        try {
            checkConnectNetWork();
            checkServerResponse();
        } catch (NetworkException | ServerNoResponseException e) {
            throw new RuntimeException(e);
        }
        List<Item> results = new ArrayList<>();
        StringBuffer jsonContent = connectAndGetRawData("GET",
                "http://localhost:8000/search?byTitle=0&semanticSearch=0&q=", input);
        String s = jsonContent.toString().replace("NaN", "\"None\"");
        JSONObject jo = (JSONObject) new JSONParser().parse(s);
        JSONArray ja = (JSONArray) jo.get("results");
        for (Object obj : ja) {
            Item item = new Item();
            JSONObject article = (JSONObject) ((JSONObject) obj).get("article");
            item.setArticleLink(article.get("Article link").toString());
            item.setWebsiteSource(article.get("Website source").toString());
            item.setArticleType(article.get("Article type").toString());
            item.setArticleTitle(article.get("Article title").toString());
            item.setContent(article.get("Content").toString());
            item.setCreationDate(article.get("Creation date").toString());
            item.setAuthor(article.get("Author").toString());
            item.setTags(article.get("Tags").toString());
            item.setCategory(article.get("Category").toString());
            item.setSummary(article.get("Summary").toString());
            results.add(item);
        }
        return results;
    }

    // Get entities from paragraph
    public static HashMap<String, Set<String>> getEntities(String input)
            throws IOException, URISyntaxException, ParseException, ServerNoResponseException {
        HashMap<String, Set<String>> result = new HashMap<>();
        StringBuffer jsonContent = connectAndGetRawData("POST", "http://localhost:8000/predict", input);
        String s = jsonContent.toString();
        JSONObject jo = (JSONObject) new JSONParser().parse(s);
        JSONObject entitiesObject = (JSONObject) jo.get("entities");

        for (Object key : entitiesObject.keySet()) {
            String entityType = (String) key;
            JSONArray entityArray = (JSONArray) entitiesObject.get(entityType);

            Set<String> items = new HashSet<>();
            for (Object entity : entityArray) {
                JSONArray entityDetails = (JSONArray) entity;
                for (Object detail : entityDetails) {
                    if (detail instanceof String) {
                        items.add((String) detail);
                    }
                }
            }
            result.put(entityType, items);
        }
        return result;
    }

    // Trend detect
    // Phần tử đầu vector là reason, còn lại là citations
    public static HashMap<String, Vector<String>> trendDectect(String input)
            throws IOException, URISyntaxException, ParseException, ServerNoResponseException {
        try {
            APICaller.checkConnectNetWork();
        } catch (NetworkException e) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Network connect error!");
            alert.setHeaderText(null);
            alert.setContentText("Please check your connect and retry!");
            alert.showAndWait();
        }
        try {
            APICaller.checkServerResponse();
        } catch (ServerNoResponseException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Server no response!");
            alert.setHeaderText(null);
            alert.setContentText("Plese check server and retry!");
            alert.showAndWait();
        }
        HashMap<String, Vector<String>> result = new HashMap<>();
        StringBuffer content = connectAndGetRawData("POST", "http://localhost:8000/detect", input);
        String s = content.toString();
        JSONObject jo = (JSONObject) new JSONParser().parse(s);
        JSONArray trends = (JSONArray) jo.get("trends");
        for (Object t : trends) {
            Vector<String> v = new Vector<>();
            JSONObject trendObject = (JSONObject) t;
            JSONArray citations = (JSONArray) (trendObject).get("citations");
            String reason = (trendObject).get("reason").toString();
            String trend = (trendObject).get("trend").toString();
            v.add(reason);
            for (Object o : citations) {
                v.add(o.toString());
            }
            result.put(trend, v);
        }
        return result;
    }

    // Get statistic
//    public static HashMap<String, Vector<String>> getStats()
//            throws IOException, URISyntaxException, ParseException, ServerNoResponseException {
//        HashMap<String, Vector<String>> result = new HashMap<>();
//        String content = connectAndGetRawData("GET", "http://localhost:8000/stats", "").toString();
//        JSONObject jo = (JSONObject) new JSONParser().parse(content);
//        String articleCount, s;
//        int count;
//        Vector<String> vct = new Vector<>();
//        JSONObject t;
//        String keys[] = { "articleCount", "website_source", "authors", "category", "tags" };
//
//        articleCount = jo.get("articleCount").toString();
//        vct.add(articleCount);
//        result.put("Article count", vct);
//
//        for (String key : keys) {
//            vct = new Vector<>();
//            t = (JSONObject) jo.get(key);
//            for (Object o : t.keySet()) {
//                s = o.toString();
//                count = (int) t.get(s);
//                vct.add(s + ": " + String.valueOf(count));
//            }
//            result.put(key, vct);
//        }
//
//        return result;
//    }

    // Goi api va lay ket qua vao buffer
    public static StringBuffer connectAndGetRawData(String methodType, String urlString, String input)
            throws IOException, URISyntaxException {
        try {
            APICaller.checkConnectNetWork();
        } catch (NetworkException e) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Network connect error!");
            alert.setHeaderText(null);
            alert.setContentText("Please check your connect and retry!");
            alert.showAndWait();
        }
        try {
            APICaller.checkServerResponse();
        } catch (ServerNoResponseException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Server no response!");
            alert.setHeaderText(null);
            alert.setContentText("Plese check server and retry!");
            alert.showAndWait();
        }
        StringBuffer content = new StringBuffer();
        String parsedInput = "";
        if (methodType.equals("GET")) {
            parsedInput = URLEncoder.encode(input, StandardCharsets.UTF_8);
        } else if (methodType.equals("POST")) {
            parsedInput = "";
        }
        URI uri = new URI(urlString + parsedInput);
        URL url = uri.toURL();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(methodType);
        con.setRequestProperty("Content-type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        if (methodType.equals("POST")) {
            // Enable sending data
            con.setDoOutput(true);
            // Convert the text data to JSON format
            String jsonInputString = "{\"text\":\"" + input + "\"}";
            // Send the request data
            try (OutputStream os = con.getOutputStream()) {
                byte[] in = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(in, 0, in.length);
            }
        } else if (methodType.equals("GET")) {
            con.setConnectTimeout(10000);
            con.setReadTimeout(10000);
        }
        if (con.getResponseCode() == 200) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
        }
        return content;
    }

    // Kiem tra ket noi internet
    public static void checkConnectNetWork() throws NetworkException {
        String host = "google.com";
        try {
            InetAddress inetAddress = InetAddress.getByName(host);
            if (!inetAddress.isReachable(20000)) {
                throw new NetworkException("Không thể kết nối tới mạng");
            }
        } catch (UnknownHostException e) {
            System.err.println("Không thể tìm thấy host: " + host);
            throw new NetworkException("Không thể kết nối tới mạng");
        } catch (IOException e) {
            System.err.println("Lỗi khi kiểm tra kết nối tới mạng");
            throw new NetworkException("Lỗi khi kiểm tra kết nối tới mạng");
        }
    }

    // Kiểm tra server có phản hồi không
    public static void checkServerResponse() throws ServerNoResponseException {
        String serverUrl = "http://localhost:8000/search?byTitle=0&semanticSearch=0&q=";
        try {
            URL url = new URL(serverUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(10000); // 5 seconds
            con.setReadTimeout(10000);    // 5 seconds

            int status = con.getResponseCode();
            if (status != 200) {
                throw new ServerNoResponseException("Server không phản hồi");
            }
        } catch (IOException e) {
            throw new ServerNoResponseException("Server không phản hồi");
        }
    }
}
