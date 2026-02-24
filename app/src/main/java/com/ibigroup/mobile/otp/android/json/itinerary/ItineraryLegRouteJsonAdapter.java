package com.ibigroup.mobile.otp.android.json.itinerary;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class ItineraryLegRouteJsonAdapter implements JsonSerializer<ItineraryLegRoute>, JsonDeserializer<ItineraryLegRoute> {

    @Override
    public JsonElement serialize(ItineraryLegRoute itineraryLegRoute, Type typeOfSrc, JsonSerializationContext context) {

        if (itineraryLegRoute != null && itineraryLegRoute.getShortName() != null) {
            boolean hasMoreData = itineraryLegRoute.getLongName() != null ||
                    itineraryLegRoute.getId() != null ||
                    itineraryLegRoute.getColor() != null ||
                    itineraryLegRoute.getTextColor() != null ||
                    itineraryLegRoute.getGtfsid() != null;
            if (!hasMoreData) {
                return new JsonPrimitive(itineraryLegRoute.getShortName());
            }

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("shortName", itineraryLegRoute.getShortName());
            jsonObject.addProperty("longName", itineraryLegRoute.getLongName());
            jsonObject.addProperty("id", itineraryLegRoute.getId());
            jsonObject.addProperty("color", itineraryLegRoute.getColor());
            jsonObject.addProperty("textColor", itineraryLegRoute.getTextColor());
            jsonObject.addProperty("type", itineraryLegRoute.getType());
            jsonObject.addProperty("gtfsid", itineraryLegRoute.getGtfsid());

            String alertsString = itineraryLegRoute.getAlerts();
            if (alertsString != null && !alertsString.isEmpty()) {
                JsonArray alertsArray = new JsonArray();

                try {
                    JsonElement parsedElement = JsonParser.parseString(alertsString);

                    if (parsedElement.isJsonArray()) {
                        alertsArray = parsedElement.getAsJsonArray();
                    } else {
                        String[] alertsList = alertsString.split(",");
                        for (String alert : alertsList) {
                            alertsArray.add(alert.trim());
                        }
                    }
                } catch (Exception e) {
                    String[] alertsList = alertsString.split(",");
                    for (String alert : alertsList) {
                        alertsArray.add(alert.trim());
                    }
                }
                jsonObject.add("alerts", alertsArray);
            }


            return jsonObject;
        }

        return new JsonObject();
    }

    @Override
    public ItineraryLegRoute deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        ItineraryLegRoute itineraryLegRoute = new ItineraryLegRoute();

        if (!json.isJsonNull()) {
            if (!json.isJsonObject()) {
                itineraryLegRoute.setShortName(json.getAsString());
            } else {
                JsonObject jsonObject = (JsonObject) json;
                if (jsonObject.get("shortName") != null && !jsonObject.get("shortName").isJsonNull()) {
                    itineraryLegRoute.setShortName(jsonObject.get("shortName").getAsString());
                }
                if (jsonObject.get("color") != null && !jsonObject.get("color").isJsonNull()) {
                    itineraryLegRoute.setColor(jsonObject.get("color").getAsString());
                }
                if (jsonObject.get("id") != null && !jsonObject.get("id").isJsonNull()) {
                    itineraryLegRoute.setId(jsonObject.get("id").getAsString());
                }
                if (jsonObject.get("longName") != null && !jsonObject.get("longName").isJsonNull()) {
                    itineraryLegRoute.setLongName(jsonObject.get("longName").getAsString());
                }
                if (jsonObject.get("textColor") != null && !jsonObject.get("textColor").isJsonNull()) {
                    itineraryLegRoute.setTextColor(jsonObject.get("textColor").getAsString());
                }
                if (jsonObject.get("type") != null && !jsonObject.get("type").isJsonNull()) {
                    itineraryLegRoute.setType(jsonObject.get("type").getAsInt());
                }
                if (jsonObject.get("alerts") != null && !jsonObject.get("alerts").isJsonNull()) {
                    itineraryLegRoute.setAlerts(jsonObject.get("alerts").toString());
                }
            }
        }
        return itineraryLegRoute;
    }
}
