import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Test {

    private final static String LIST_REQ_QUEUE_NAME = "List Request Queue";
    private final static String LIST_RES_QUEUE_NAME = "List Response Queue";
    private final static String DETAILS_REQ_QUEUE_NAME = "Details Request Queue";
    private final static String DETAILS_RES_QUEUE_NAME = "Details Response Queue";
    private final static String ADD_REQ_QUEUE_NAME = "Add Request Queue";
    private final static String ADD_RES_QUEUE_NAME = "Add Response Queue";
    private final static String DELETE_REQ_QUEUE_NAME = "Delete Request Queue";
    private final static String DELETE_RES_QUEUE_NAME = "Delete Response Queue";
    public static ArrayList<Book> bookList;


    public static void main(String[] args) throws Exception {
        bookList = new ArrayList<Book>();
        Book book1 = new Book(1,"The Hobbit");
        bookList.add(book1);
        Book book2 = new Book(2,"Lord of the Rings");
        bookList.add(book2);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(LIST_REQ_QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
            Gson gson = new Gson();
            String response = gson.toJson(bookList);
            channel.queueDeclare(LIST_RES_QUEUE_NAME, false, false, false, null);
            channel.basicPublish("", LIST_RES_QUEUE_NAME, null, response.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + response + "'");
        };
        channel.basicConsume(LIST_REQ_QUEUE_NAME, true, deliverCallback, consumerTag -> { });

        channel.queueDeclare(DETAILS_REQ_QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
            Gson gson = new Gson();
            int value = gson.fromJson(message, int.class);
            int index = value;
            for (Book book: bookList){
                if(book.getBookID()==value){
                    index = value;
                }
            }
            String response = gson.toJson(bookList.get(index-1));
            channel.queueDeclare(DETAILS_RES_QUEUE_NAME, false, false, false, null);
            channel.basicPublish("", DETAILS_RES_QUEUE_NAME, null, response.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + response + "'");
        };
        channel.basicConsume(DETAILS_REQ_QUEUE_NAME, true, deliverCallback2, consumerTag -> { });

        channel.queueDeclare(ADD_REQ_QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        DeliverCallback deliverCallback3 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
            Gson gson = new Gson();
            String bookName = gson.fromJson(message, String.class);
            Book newBook = new Book(bookList.get(bookList.size()-1).getBookID()+1, bookName);
            bookList.add(newBook);
            String response = gson.toJson(newBook);
            channel.queueDeclare(ADD_RES_QUEUE_NAME, false, false, false, null);
            channel.basicPublish("", ADD_RES_QUEUE_NAME, null, response.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + response + "'");
        };
        channel.basicConsume(ADD_REQ_QUEUE_NAME, true, deliverCallback3, consumerTag -> { });


        channel.queueDeclare(DELETE_REQ_QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        DeliverCallback deliverCallback4 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
            Gson gson = new Gson();
            int value = gson.fromJson(message, int.class);
            int index = value;
            for (Book book: bookList){
                if(book.getBookID()==value){
                    index = value;
                }
            }
            Book bookDeleted = bookList.get(index-1);
            bookList.remove(index-1);
            String response = gson.toJson(bookDeleted);
            channel.queueDeclare(DELETE_RES_QUEUE_NAME, false, false, false, null);
            channel.basicPublish("", DELETE_RES_QUEUE_NAME, null, response.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + response + "'");
        };
        channel.basicConsume(DELETE_REQ_QUEUE_NAME, true, deliverCallback4, consumerTag -> { });

        /*HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/getAllBooks", new MyHandler());
        server.createContext("/getBookDetails", new MyHandler2());
        server.createContext("/addBook", new MyHandler3());
        server.createContext("/deleteBook", new MyHandler4());
        server.setExecutor(null); // creates a default executor
        server.start();*/
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

