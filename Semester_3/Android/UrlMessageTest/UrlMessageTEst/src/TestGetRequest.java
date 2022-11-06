import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestGetRequest {
    public static void main(String[] args) throws IOException {
        URL host = new URL("http://213.189.221.170:8008/1ch");
        HttpURLConnection connection = (HttpURLConnection) host.openConnection();
//        connection.setDoOutput(true);
//        connection.setDoInput(true);

        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line = in.readLine();

        in.close();

//        System.out.println(line);
        JSONArray test = new JSONArray(line);
        long d = test.getJSONObject(0).getLong("time");
        Date cringe = new Date(d);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        System.out.println(test.length());
        System.out.println(sdf.format(cringe));

        test.getJSONObject(0).getJSONObject("data").has("Text");
    }
}
