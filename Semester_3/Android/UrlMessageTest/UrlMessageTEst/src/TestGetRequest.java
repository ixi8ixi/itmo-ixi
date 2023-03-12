import org.json.JSONObject;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestGetRequest {
    public static void main(String[] args) throws IOException {
        JSONObject test = new JSONObject();
        test.put("from", "dinia");
        JSONObject text = new JSONObject();
        text.put("text", "hello world!");
        test.put("data", new JSONObject().put("Text", text));
        System.out.println(test);

        URL url = new URL("http://213.189.221.170:8008/1ch");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
        out.append(test.toString()).flush();
        System.out.println(connection.getResponseCode());

    }
}
