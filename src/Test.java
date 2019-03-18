import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Test {

    public static ArrayList<Book> bookList;

    public static void main(String[] args) throws Exception {
        bookList = new ArrayList<Book>();
        Book book1 = new Book(1,"The Hobbit");
        bookList.add(book1);
        Book book2 = new Book(2,"Lord of the Rings");
        bookList.add(book2);
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/getAllBooks", new MyHandler());
        server.createContext("/getBookDetails", new MyHandler2());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            //InputStream inputStream = t.getRequestBody();
            //JSONParser jsonParser = new JSONParser();
            //JSONObject jsonObject = new JSONObject();
            //JSONArray jsonArray = new JSONArray();
            //try {
            //    jsonArray = (JSONArray)jsonParser.parse(
            //            new InputStreamReader(inputStream, "UTF-8"));
            //} catch (ParseException e) {
            //    e.printStackTrace();
            //}
            //String response = jsonArray.toJSONString();
            //jsonArray = (JSONArray)jsonParser.parse(bookList.toString());
            //jsonArray.add(jsonParser.parsebookList.get(0));
            //jsonArray.add(bookList.get(1));
            //String response = "[{\"bookID\":1,\"name\":\"The Hobbit\"},{\"bookID\":1,\"name\":\"The Hobbit\"}]";
            //String response = jsonArray.toJSONString();
            Gson gson = new Gson();
            String jsnObj = gson.toJson(bookList);
            String response = jsnObj.toString();
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    static class MyHandler2 implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            InputStream inputStream = t.getRequestBody();
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = new JsonObject();
            Gson gson = new Gson();
            int index = gson.fromJson(new InputStreamReader(inputStream, "UTF-8"), int.class);
            //JSONArray jsonArray = new JSONArray();
            //try {
            //    jsonArray = (JSONArray)jsonParser.parse(
            //            new InputStreamReader(inputStream, "UTF-8"));
            //} catch (ParseException e) {
            //    e.printStackTrace();
            //}
            //String response = jsonArray.toJSONString();
            //jsonArray = (JSONArray)jsonParser.parse(bookList.toString());
            //jsonArray.add(jsonParser.parsebookList.get(0));
            //jsonArray.add(bookList.get(1));
            //String response = "[{\"bookID\":1,\"name\":\"The Hobbit\"},{\"bookID\":1,\"name\":\"The Hobbit\"}]";
            //String response = jsonArray.toJSONString();
            String jsnObj = gson.toJson(bookList.get(index));
            String response = jsnObj.toString();
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}

