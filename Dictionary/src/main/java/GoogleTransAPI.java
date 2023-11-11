import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class GoogleTransAPI {
    private static String translate (String langFrom, String langTo, String text) throws IOException {
        String urlStr = "https://script.google.com/macros/s/AKfycbxgv80pLJjQ4iesOK1T6nvHORoYjP05zLPz5snFYleVxfCnMkoOWdhsiWiicw1ViOsXMg/exec"
                        + "?q=" + URLEncoder.encode(text, "UTF-8") //Encode the text
                        + "&target=" + langTo
                        + "&source=" + langFrom;
        URL url = new URL(urlStr);
        StringBuilder response = new StringBuilder();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();  //Init a connect from HTTP to URL
        con.setRequestProperty("User-Agent", "Mozilla/5.0"); //Set request's attribute and browser to communicate to sever
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())); //Setting InputStream to read
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    public static void main(String[] args) throws IOException {
        String text = "Hello guys, Im fucking Duong";
        System.out.println(translate("en", "vi", text));


        //String text = translate("I am programmer", "en", "vi");
    }
}
