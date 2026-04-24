import java.net.URI;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.*;
import com.google.gson.*;

public class QuizLeaderBoard {

    static final String BASE = "https://devapigw.vidalhealthtpa.com/srm-quiz-task";
    static final String REG_NO = "RA2311003011165";

    public static void main(String[] args) throws Exception {

        HttpClient client = HttpClient.newHttpClient();
        Set<String> seen = new HashSet<>();
        Map<String, Integer> scores = new HashMap<>();

        for (int poll = 0; poll <= 9; poll++) {

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE + "/quiz/messages?regNo=" + REG_NO + "&poll=" + poll))
                    .GET()
                    .build();

            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() != 200) continue;

            String body = res.body();

            // Debug: raw API response
            System.out.println("Poll " + poll + " raw: " + body);

            JsonObject json = JsonParser.parseString(body).getAsJsonObject();
            JsonArray events = json.getAsJsonArray("events");

            for (JsonElement elem : events) {
                JsonObject obj = elem.getAsJsonObject();

                String roundId = obj.get("roundId").getAsString();
                String participant = obj.get("participant").getAsString();
                int score = obj.get("score").getAsInt();

                String key = roundId + "|" + participant;

                if (seen.add(key)) {
                    scores.merge(participant, score, Integer::sum);
                }
            }

            if (poll < 9) Thread.sleep(5000);
        }

        List<Map.Entry<String, Integer>> leaderboard = new ArrayList<>(scores.entrySet());
        leaderboard.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        StringBuilder lb = new StringBuilder("[");
        for (int i = 0; i < leaderboard.size(); i++) {
            Map.Entry<String, Integer> e = leaderboard.get(i);
            lb.append("{\"participant\":\"").append(e.getKey())
              .append("\",\"totalScore\":").append(e.getValue()).append("}");
            if (i < leaderboard.size() - 1) lb.append(",");
        }
        lb.append("]");

        String payload = "{\"regNo\":\"" + REG_NO + "\",\"leaderboard\":" + lb + "}";

        System.out.println("Submitting: " + payload);

        HttpRequest submitReq = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/quiz/submit"))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> submitRes = client.send(submitReq, HttpResponse.BodyHandlers.ofString());
        System.out.println(submitRes.body());
    }
}