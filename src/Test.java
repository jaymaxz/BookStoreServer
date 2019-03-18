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
        server.createContext("/addBook", new MyHandler3());
        server.createContext("/deleteBook", new MyHandler4());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            Gson gson = new Gson();
            String response = gson.toJson(bookList);
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
            Gson gson = new Gson();
            int value = gson.fromJson(new InputStreamReader(inputStream, "UTF-8"), int.class);
            int index = value;
            for (Book book: bookList){
                if(book.getBookID()==value){
                    index = value;
                }
            }
            String response = gson.toJson(bookList.get(index-1));
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class MyHandler3 implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            InputStream inputStream = t.getRequestBody();
            Gson gson = new Gson();
            String bookName = gson.fromJson(new InputStreamReader(inputStream, "UTF-8"), String.class);
            Book newBook = new Book(bookList.get(bookList.size()-1).getBookID()+1, bookName);
            bookList.add(newBook);
            String response = gson.toJson(newBook);
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class MyHandler4 implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            InputStream inputStream = t.getRequestBody();
            Gson gson = new Gson();
            int value = gson.fromJson(new InputStreamReader(inputStream, "UTF-8"), int.class);
            int index = value;
            for (Book book: bookList){
                if(book.getBookID()==value){
                    index = value;
                }
            }Book bookDeleted = bookList.get(index-1);
            bookList.remove(index-1);
            String response = gson.toJson(bookDeleted);
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}

