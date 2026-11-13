package com.example.mycgame.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CharacterCreationData {
    public static final int DATA_VERSION = 1;

    public int selectedSection = 0;
    public String playerName = "";
    public String playerAge = "18";
    public String gender = "MALE";
    public String innateTrait = "";
    public String countryCode = "";
    public String countryName = "";
    public String residenceCountryCode = "";
    public String residenceCountryName = "";
    public String residenceLocationId = "";
    public String residenceLocationName = "";
    public String residenceType = "";
    public String educationLevel = "";
    public String educationCurrentLevel = "";
    public String educationStatus = "";
    public String educationField = "";
    public String educationPerformance = "";
    public String economicStatus = "";
    public String familyStructure = "";
    public String familyParentStatus = "";
    public String familySupport = "";
    public String familyEconomicStatus = "";
    public final HashSet<String> selectedGeneralSkillIds = new HashSet<>();
    public final HashSet<String> selectedSpecializedSkillIds = new HashSet<>();

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("dataVersion", DATA_VERSION);
        json.put("selectedSection", selectedSection);
        json.put("playerName", safe(playerName));
        json.put("playerAge", safe(playerAge));
        json.put("gender", safe(gender));
        json.put("innateTrait", safe(innateTrait));
        json.put("countryCode", safe(countryCode));
        json.put("countryName", safe(countryName));
        json.put("residenceCountryCode", safe(residenceCountryCode));
        json.put("residenceCountryName", safe(residenceCountryName));
        json.put("residenceLocationId", safe(residenceLocationId));
        json.put("residenceLocationName", safe(residenceLocationName));
        json.put("residenceType", safe(residenceType));
        json.put("educationLevel", safe(educationLevel));
        json.put("educationCurrentLevel", safe(educationCurrentLevel));
        json.put("educationStatus", safe(educationStatus));
        json.put("educationField", safe(educationField));
        json.put("educationPerformance", safe(educationPerformance));
        json.put("economicStatus", safe(economicStatus));
        json.put("familyStructure", safe(familyStructure));
        json.put("familyParentStatus", safe(familyParentStatus));
        json.put("familySupport", safe(familySupport));
        json.put("familyEconomicStatus", safe(familyEconomicStatus));
        json.put("selectedGeneralSkillIds", toArray(selectedGeneralSkillIds));
        json.put("selectedSpecializedSkillIds", toArray(selectedSpecializedSkillIds));
        return json;
    }

    public static CharacterCreationData fromJson(String source) throws JSONException {
        JSONObject json = new JSONObject(source);
        CharacterCreationData data = new CharacterCreationData();
        data.selectedSection = Math.max(0, Math.min(6, json.optInt("selectedSection", 0)));
        data.playerName = json.optString("playerName", "");
        data.playerAge = json.optString("playerAge", "18");
        data.gender = json.optString("gender", "MALE");
        data.innateTrait = json.optString("innateTrait", "");
        data.countryCode = json.optString("countryCode", "");
        data.countryName = json.optString("countryName", "");
        data.residenceCountryCode = json.optString("residenceCountryCode", "");
        data.residenceCountryName = json.optString("residenceCountryName", "");
        data.residenceLocationId = json.optString("residenceLocationId", "");
        data.residenceLocationName = json.optString("residenceLocationName", "");
        data.residenceType = json.optString("residenceType", "");
        data.educationLevel = json.optString("educationLevel", "");
        data.educationCurrentLevel = json.optString("educationCurrentLevel", "");
        data.educationStatus = json.optString("educationStatus", "");
        data.educationField = json.optString("educationField", "");
        data.educationPerformance = json.optString("educationPerformance", "");
        data.economicStatus = json.optString("economicStatus", "");
        data.familyStructure = json.optString("familyStructure", "");
        data.familyParentStatus = json.optString("familyParentStatus", "");
        data.familySupport = json.optString("familySupport", "");
        data.familyEconomicStatus = json.optString("familyEconomicStatus", "");
        readArray(json.optJSONArray("selectedGeneralSkillIds"), data.selectedGeneralSkillIds);
        readArray(json.optJSONArray("selectedSpecializedSkillIds"), data.selectedSpecializedSkillIds);
        return data;
    }

    private static JSONArray toArray(Set<String> values) {
        ArrayList<String> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        JSONArray array = new JSONArray();
        for (String value : sorted) array.put(value);
        return array;
    }

    private static void readArray(JSONArray array, Set<String> target) {
        if (array == null) return;
        for (int i = 0; i < array.length(); i++) {
            String value = array.optString(i, "");
            if (!value.isEmpty()) target.add(value);
        }
    }
}
