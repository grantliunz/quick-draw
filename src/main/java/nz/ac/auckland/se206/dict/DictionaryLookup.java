package nz.ac.auckland.se206.dict;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DictionaryLookup {

  private static final String API_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/";

  public static String searchWordInfo(String query) throws IOException, WordNotFoundException {

    OkHttpClient client = new OkHttpClient();
    // makes a request for the word defintion using api
    Request request = new Request.Builder().url(API_URL + query).build();
    Response response = client.newCall(request).execute();
    ResponseBody responseBody = response.body();

    String jsonString = responseBody.string();

    ObjectMapper objectMapper = new ObjectMapper();
    // returns the string definition using the object
    try {
      return objectMapper
          .readValue(jsonString, ObjectNode[].class)[0]
          .get("meanings")
          .get(0)
          .get("definitions")
          .get(0)
          .get("definition")
          .asText();
    } catch (Exception e) {
      // in the case the api does not have the definition of the word
      throw new WordNotFoundException(query, "Word not found", "Word not found");
    }
  }
}
