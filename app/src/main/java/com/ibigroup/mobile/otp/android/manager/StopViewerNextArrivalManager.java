package com.ibigroup.mobile.otp.android.manager;

import static com.ibigroup.mobile.otp.android.utility.ServerDelegate.volleyStringRequest;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.exception.ApolloException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ibigroup.mobile.otp.android.MyApplication;
import com.ibigroup.mobile.otp.android.StopTimesQuery;
import com.ibigroup.mobile.otp.android.api.Resource;
import com.ibigroup.mobile.otp.android.json.StopViewerPattern;
import com.ibigroup.mobile.otp.android.json.StopViewerSchedule;
import com.ibigroup.mobile.otp.android.json.StopViewerTime;
import com.ibigroup.mobile.otp.android.utility.Configurations;
import com.ibigroup.mobile.otp.android.utility.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StopViewerNextArrivalManager {

    private static StopViewerNextArrivalManager instance;
    private final ArrayList<NextArrivalListener> nextArrivalListeners;

    private final Handler handler;
    private final Runnable runnable;
    private StringRequest nextArrivalRequest = null;

    public StopViewerNextArrivalManager() {
        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                getNextArrivals();

                handler.postDelayed(this, Configurations.getInt("stop_viewer_next_arrival_refresh_rate", 10) * 1000L);
            }
        };

        nextArrivalListeners = new ArrayList<NextArrivalListener>();
    }

    public static StopViewerNextArrivalManager getInstance() {
        if (instance == null) {
            synchronized (StopViewerNextArrivalManager.class) {
                // May be another thread has created an instance, check again within the synched code
                if (instance == null) {
                    instance = new StopViewerNextArrivalManager();
                }
            }
        }
        return instance;
    }

    private String getHeadSignFromStopViewerTime(StopViewerSchedule stopViewerSchedule) {
        if (stopViewerSchedule != null) {
            if (stopViewerSchedule.getTimes() != null && stopViewerSchedule.getTimes().size() > 0
                    && stopViewerSchedule.getTimes().get(0) != null && stopViewerSchedule.getTimes().get(0).getHeadsign() != null
                    && !stopViewerSchedule.getTimes().get(0).getHeadsign().isEmpty()) {
                return stopViewerSchedule.getTimes().get(0).getHeadsign();
            } else {
                if (stopViewerSchedule.getPattern() != null && stopViewerSchedule.getPattern().getDesc() != null) {
                    String description = stopViewerSchedule.getPattern().getDesc();
                    Pattern pattern = Pattern.compile(" to ([^(from)]+) \\(.+\\)");
                    Matcher matcher = pattern.matcher(description);

                    if (matcher.find()) {
                        if (matcher.groupCount() >= 1) {
                            return matcher.group(1);
                        }
                    }
                }
            }
        }

        return "";
    }

    private void filterAndSortNextArrivals() {
        if (MyApplication.getInstance().stopViewerNextArrivals != null) {
            ArrayList<StopViewerSchedule> stopViewerNextArrivals = MyApplication.getInstance().stopViewerNextArrivals;

            ArrayList<StopViewerSchedule> newArrayList = new ArrayList<>();
            boolean found;
            Comparator<StopViewerTime> stopViewerTimeComparator = new Comparator<StopViewerTime>() {
                @Override
                public int compare(StopViewerTime o1, StopViewerTime o2) {
                    if (o1 != null && o2 != null) {
                        long time1 = getTimeFromStopViewerTime(o1);
                        long time2 = getTimeFromStopViewerTime(o2);
                        return Long.compare(time1, time2);
                    }
                    return 0;
                }
            };
            for (StopViewerSchedule stopViewerSchedule : stopViewerNextArrivals) {
                if (newArrayList.size() == 0) {
                    Collections.sort(stopViewerSchedule.getTimes(), stopViewerTimeComparator);
                    newArrayList.add(stopViewerSchedule);
                } else {
                    StopViewerPattern stopViewerPattern = stopViewerSchedule.getPattern();
                    found = false;
                    if (stopViewerPattern != null && stopViewerSchedule.getTimes() != null &&
                            stopViewerSchedule.getTimes().size() > 0) {
                        String id = stopViewerPattern.getId();
                        if (id != null) {
                            String[] ids = id.split(":");
                            if (ids.length >= 2) {
                                id = String.format("%s:%s", ids[0], ids[1]);
                            }
                            id = String.format("%s-%s", id, getHeadSignFromStopViewerTime(stopViewerSchedule));

                            for (StopViewerSchedule addedStopViewerSchedule : newArrayList) {
                                StopViewerPattern addedStopViewerPattern = addedStopViewerSchedule.getPattern();
                                if (addedStopViewerPattern != null && addedStopViewerPattern.getId() != null) {
                                    String addedId = addedStopViewerPattern.getId();
                                    String[] addedIds = addedId.split(":");
                                    if (addedIds.length >= 2) {
                                        addedId = String.format("%s:%s", addedIds[0], addedIds[1]);
                                    }
                                    addedId = String.format("%s-%s", addedId, getHeadSignFromStopViewerTime(addedStopViewerSchedule));

                                    if (addedId.equals(id)) {
                                        found = true;

                                        if (addedStopViewerSchedule.getTimes() == null
                                                || addedStopViewerSchedule.getTimes().size() == 0) {
                                            newArrayList.remove(addedStopViewerSchedule);
                                            Collections.sort(stopViewerSchedule.getTimes(), stopViewerTimeComparator);
                                            newArrayList.add(stopViewerSchedule);
                                        } else {
                                            //merge and keep 3 less times
                                            addedStopViewerSchedule.getTimes().addAll(stopViewerSchedule.getTimes());
                                            Collections.sort(addedStopViewerSchedule.getTimes(), stopViewerTimeComparator);

                                            if (addedStopViewerSchedule.getTimes().size() > 3) {
                                                addedStopViewerSchedule.getTimes().subList(3, addedStopViewerSchedule.getTimes().size()).clear();
                                            }
                                        }

                                        break;
                                    }
                                }
                            }
                        }
                    }

                    if (!found) {
                        if (stopViewerSchedule.getTimes() != null) {
                            Collections.sort(stopViewerSchedule.getTimes(), stopViewerTimeComparator);
                        }
                        newArrayList.add(stopViewerSchedule);
                    }
                }
            }

            Collections.sort(newArrayList, new Comparator<StopViewerSchedule>() {
                @Override
                public int compare(StopViewerSchedule o1, StopViewerSchedule o2) {
                    if (o1.getTimes() != null && o1.getTimes().size() > 0
                            && o2.getTimes() != null && o2.getTimes().size() > 0) {
                        StopViewerTime stopViewerTime1 = o1.getTimes().get(0);
                        StopViewerTime stopViewerTime2 = o2.getTimes().get(0);

                        long time1 = getTimeFromStopViewerTime(stopViewerTime1);
                        long time2 = getTimeFromStopViewerTime(stopViewerTime2);
                        return Long.compare(time1, time2);
                    }
                    return 0;
                }
            });
            MyApplication.getInstance().stopViewerNextArrivals = newArrayList;
        }
    }

    private long getTimeFromStopViewerTime(StopViewerTime stopViewerTime) {
        if (stopViewerTime != null) {
            long time = stopViewerTime.getServiceDay();
            if (stopViewerTime.isRealtime()) {
                return (time + stopViewerTime.getRealtimeArrival());
            } else {
                return (time + stopViewerTime.getScheduledArrival());
            }
        } else {
            return 0;
        }
    }

    public void getNextArrivals() {
        Response.Listener getNextArrivalListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && !response.isEmpty() && response.startsWith("[")) {
                    Gson gson = new Gson();

                    MyApplication.getInstance().stopViewerNextArrivals = gson.fromJson(response, new TypeToken<ArrayList<StopViewerSchedule>>() {
                    }.getType());
                    filterAndSortNextArrivals();
                    fireNextArrivalChanged();
                } else {
                    Log.d("nextArrival", "data is not correct:" + response);
                }

            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("get next arrivals feed error:", error.toString());
                fireNextArrivalStopRequest();
            }
        };

        try {
            fireNextArrivalStartRequest();
            getNextArrivalResponse(getNextArrivalListener, errorListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getNextArrivalResponse(Response.Listener listener,
                                       Response.ErrorListener errListener) throws Exception {
        if (MyApplication.getInstance().stopViewerStopID != null) {
            if (!Configurations.getString("graphql_base_url", Configurations.GRAPHQL_SERVER_URL).isEmpty()) {
                ApolloClient apolloClient = ApolloClient.builder()
                        .serverUrl(Configurations.getString("graphql_base_url", Configurations.GRAPHQL_SERVER_URL))
                        .build();
                apolloClient.query(new StopTimesQuery(MyApplication.getInstance().stopViewerStopID)).enqueue(new ApolloCall.Callback<StopTimesQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull com.apollographql.apollo.api.Response<StopTimesQuery.Data> response) {
                        if (response != null && response.getData() != null && response.getData().stop() != null
                                && response.getData().stop().stopTimes() != null) {
                            Gson gson = new Gson();
                            MyApplication.getInstance().stopViewerNextArrivals = gson.fromJson(gson.toJson(response.getData().stop().stopTimes()), new TypeToken<ArrayList<StopViewerSchedule>>() {
                            }.getType());

                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            mainHandler.post(() -> {
                                filterAndSortNextArrivals();
                                fireNextArrivalChanged();
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        Logger.e("get next arrivals feed error:", e.toString());
                        fireNextArrivalStopRequest();
                    }
                });
            } else {
                String url = String.format(Locale.getDefault(), Resource.API_STOP_VIEWER_NEXT_ARRIVAL,
                        MyApplication.getInstance().stopViewerStopID, System.currentTimeMillis() / 1000);

                nextArrivalRequest = volleyStringRequest(Request.Method.GET, url, "", listener, errListener);
            }
        }
    }

    public void start() {
        if (handler != null) {
            handler.removeCallbacks(runnable);
            if (nextArrivalRequest != null) {
                nextArrivalRequest.cancel();
                nextArrivalRequest = null;
            }
            handler.postDelayed(runnable, 1);
        }
    }

    public void startForOneTime() {
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
        if (nextArrivalRequest != null) {
            nextArrivalRequest.cancel();
            nextArrivalRequest = null;
        }
        getNextArrivals();
    }

    public void stop() {
        if (handler != null) {
            //cancel handler
            handler.removeCallbacks(runnable);
        }

        if (nextArrivalRequest != null) {
            nextArrivalRequest.cancel();
            nextArrivalRequest = null;
        }
    }

    public interface NextArrivalListener {
        void nextArrivalChanged();

        void startRequest();

        void stopRequest();
    }

    public void addListener(StopViewerNextArrivalManager.NextArrivalListener listener) {
        if (!nextArrivalListeners.contains(listener)) {
            nextArrivalListeners.add(listener);
        }
    }

    public void removeListener(StopViewerNextArrivalManager.NextArrivalListener listener) {
        nextArrivalListeners.remove(listener);
    }

    private void fireNextArrivalChanged() {
        for (StopViewerNextArrivalManager.NextArrivalListener listener : nextArrivalListeners) {
            listener.nextArrivalChanged();
        }
    }

    private void fireNextArrivalStartRequest() {
        for (StopViewerNextArrivalManager.NextArrivalListener listener : nextArrivalListeners) {
            listener.startRequest();
        }
    }

    private void fireNextArrivalStopRequest() {
        for (StopViewerNextArrivalManager.NextArrivalListener listener : nextArrivalListeners) {
            listener.stopRequest();
        }
    }
}
