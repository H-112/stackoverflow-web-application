package cn.edu.sustech.crawler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class StackOverflowService {
    private static final Logger logger = LoggerFactory.getLogger(StackOverflowService.class);
    private final ApiClient apiClient;
    private final int pageSize;

    public StackOverflowService(int pageSize) {
        this.apiClient = new ApiClient();
        this.pageSize = pageSize;
    }

    public JSONObject getQuestionStats() {
        String params = "filter=total&tagged=java";
        return apiClient.executeRequest("questions", params);
    }

    public JSONObject getNoAnswerStats() {
        String params = "filter=total&tagged=java";
        return apiClient.executeRequest("questions/no-answers", params);
    }

    public List<JSONObject> getQuestions(int page) {
        String params = String.format("page=%d&pagesize=%d&order=desc&sort=activity&tagged=java&filter=withbody",
                page, pageSize);
        JSONObject response = apiClient.executeRequest("questions", params);
        return extractItems(response);
    }

    public List<JSONObject> getAnswers(List<Integer> questionIds) {
        if (questionIds.isEmpty()) {
            return new ArrayList<>();
        }

        String ids = String.join(";", questionIds.stream().map(String::valueOf).toArray(String[]::new));
        String params = "filter=withbody&order=desc&sort=activity";
        JSONObject response = apiClient.executeRequest("questions/" + ids + "/answers", params);
        return extractItems(response);
    }

    public List<JSONObject> getComments(String type, List<Integer> ids) {
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }

        String idsStr = String.join(";", ids.stream().map(String::valueOf).toArray(String[]::new));
        String endpoint = type.equals("question") ? "questions/" : "answers/";
        String params = "filter=withbody&order=desc&sort=creation";

        JSONObject response = apiClient.executeRequest(endpoint + idsStr + "/comments", params);
        return extractItems(response);
    }

    private List<JSONObject> extractItems(JSONObject response) {
        List<JSONObject> items = new ArrayList<>();
        JSONArray itemsArray = response.getJSONArray("items");

        if (itemsArray != null) {
            for (int i = 0; i < itemsArray.size(); i++) {
                items.add(itemsArray.getJSONObject(i));
            }
        }

        return items;
    }
}