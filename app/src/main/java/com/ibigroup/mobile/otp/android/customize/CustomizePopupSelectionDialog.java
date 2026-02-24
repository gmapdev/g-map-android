package com.ibigroup.mobile.otp.android.customize;


import static com.ibigroup.mobile.otp.android.utility.Constants.POPUP_TYPE_SINGLE_SELECTION;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ibigroup.mobile.otp.android.R;
import com.ibigroup.mobile.otp.android.json.SelectionStatus;
import com.ibigroup.mobile.otp.android.utility.Convert;
import com.ibigroup.mobile.otp.android.utility.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CustomizePopupSelectionDialog extends Dialog {
    private Activity context = null;
    private String gTitle = null;

    private String gPositiveButton = null;
    private String gNegativeButton = null;
    private View.OnClickListener gPositiveButtonOnClickListener = null;
    private View.OnClickListener gNegativeButtonOnClickListener = null;

    private ListView lvSingleSelection, lvMultipleSelection;
    private int gType = POPUP_TYPE_SINGLE_SELECTION;
    private ArrayList<SelectionStatus> selectionStatuses = null;
    private SingleSelectionListAdapter singleSelectionListAdapter = null;
    private MultipleSelectionListAdapter multipleSelectionListAdapter = null;

    private boolean gShowTickInsteadofRadio = true;// default change to show tick for single selection

    private String emptyListHintText = "";

    public CustomizePopupSelectionDialog(Activity context) {
        super(context);
        this.context = context;
    }

    public CustomizePopupSelectionDialog(Activity context, String title, String statuses, String positiveButton,
                                         String negativeButton,
                                         View.OnClickListener positiveButtonOnClickListener,
                                         View.OnClickListener negativeButtonOnClickListener, int type) {
        super(context);
        this.context = context;
        gTitle = title;

        gPositiveButton = positiveButton;
        gNegativeButton = negativeButton;
        gPositiveButtonOnClickListener = positiveButtonOnClickListener;
        gNegativeButtonOnClickListener = negativeButtonOnClickListener;
        gType = type;

        if (statuses != null && !statuses.isEmpty()) {
            try {
                Gson gson = new Gson();
                selectionStatuses = gson.fromJson(statuses,
                        new TypeToken<List<SelectionStatus>>() {
                        }.getType());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (selectionStatuses == null) {
            selectionStatuses = new ArrayList<>();
        }
    }


    public CustomizePopupSelectionDialog(Activity context, String title, String statuses, String positiveButton,
                                         String negativeButton,
                                         View.OnClickListener positiveButtonOnClickListener,
                                         View.OnClickListener negativeButtonOnClickListener, int type, boolean showTickInsteadofRadioButton) {
        super(context);
        this.context = context;
        gTitle = title;

        gPositiveButton = positiveButton;
        gNegativeButton = negativeButton;
        gPositiveButtonOnClickListener = positiveButtonOnClickListener;
        gNegativeButtonOnClickListener = negativeButtonOnClickListener;
        gType = type;

        if (statuses != null && !statuses.isEmpty()) {
            try {
                Gson gson = new Gson();
                selectionStatuses = gson.fromJson(statuses,
                        new TypeToken<List<SelectionStatus>>() {
                        }.getType());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (selectionStatuses == null) {
            selectionStatuses = new ArrayList<>();
        }

        gShowTickInsteadofRadio = showTickInsteadofRadioButton;

    }

    public CustomizePopupSelectionDialog(Activity context, String title, String statuses, String positiveButton,
                                         String negativeButton,
                                         View.OnClickListener positiveButtonOnClickListener,
                                         View.OnClickListener negativeButtonOnClickListener, int type,
                                         boolean showTickInsteadofRadioButton, String emptyListHintText) {
        super(context);
        this.context = context;
        gTitle = title;

        gPositiveButton = positiveButton;
        gNegativeButton = negativeButton;
        gPositiveButtonOnClickListener = positiveButtonOnClickListener;
        gNegativeButtonOnClickListener = negativeButtonOnClickListener;
        gType = type;

        if (statuses != null && !statuses.isEmpty()) {
            try {
                Gson gson = new Gson();
                selectionStatuses = gson.fromJson(statuses,
                        new TypeToken<List<SelectionStatus>>() {
                        }.getType());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (selectionStatuses == null) {
            selectionStatuses = new ArrayList<>();
        }

        gShowTickInsteadofRadio = showTickInsteadofRadioButton;

        this.emptyListHintText = emptyListHintText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_custom_popup_selection);
        if (this.getWindow() != null) {
            this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);

        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvPositive = findViewById(R.id.tv_positive);
        TextView tvNegative = findViewById(R.id.tv_negative);
        lvSingleSelection = findViewById(R.id.lv_single_selection);
        lvMultipleSelection = findViewById(R.id.lv_multiple_selection);

        DisplayMetrics gDm = new DisplayMetrics();

        context.getWindowManager().getDefaultDisplay().getMetrics(gDm);
        Convert.mDensity = context.getResources().getDisplayMetrics().density;

        int margin = Convert.dpToPx(16);
        int popupWidth = gDm.widthPixels - 2 * margin;

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) tvTitle.getLayoutParams();
        layoutParams.width = popupWidth;
        tvTitle.setLayoutParams(layoutParams);

        if (gTitle == null || gTitle.isEmpty()) {
            layoutParams.height = 0;
        }
        tvTitle.setText(gTitle);

        if (gPositiveButton != null && !gPositiveButton.isEmpty()) {
            tvPositive.setVisibility(View.VISIBLE);
            tvPositive.setText(gPositiveButton);

            tvPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setTag(selectionStatuses);
                    if (gPositiveButtonOnClickListener != null) {
                        gPositiveButtonOnClickListener.onClick(v);
                    }
                    dismiss();
                }
            });
        } else {
            tvPositive.setVisibility(View.GONE);
        }

        if (gNegativeButton != null && !gNegativeButton.isEmpty()) {
            tvNegative.setVisibility(View.VISIBLE);
            tvNegative.setText(gNegativeButton);
            tvNegative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (gNegativeButtonOnClickListener != null) {
                        gNegativeButtonOnClickListener.onClick(v);
                    }
                    dismiss();
                }
            });
        } else {
            tvNegative.setVisibility(View.GONE);
        }

        if (gType == POPUP_TYPE_SINGLE_SELECTION) {
            lvSingleSelection.setVisibility(View.VISIBLE);
            lvMultipleSelection.setVisibility(View.GONE);

            singleSelectionListAdapter = getSingleSelectionListAdapter();
            lvSingleSelection.setAdapter(singleSelectionListAdapter);
            Utilities.setListViewTotalHeight(lvSingleSelection, gDm.widthPixels - 3 * margin);
        } else {
            lvSingleSelection.setVisibility(View.GONE);
            lvMultipleSelection.setVisibility(View.VISIBLE);

            multipleSelectionListAdapter = getMultipleSelectionListAdapter();
            lvMultipleSelection.setAdapter(multipleSelectionListAdapter);
            Utilities.setListViewTotalHeight(lvMultipleSelection, gDm.widthPixels - 3 * margin);
        }

        if (emptyListHintText != null && !emptyListHintText.isEmpty()) {
            TextView tvEmptyList = findViewById(R.id.tv_empty_list);

            tvEmptyList.setText(emptyListHintText);

            if (selectionStatuses != null && selectionStatuses.size() > 0) {
                tvEmptyList.setVisibility(View.GONE);
            } else {
                tvEmptyList.setVisibility(View.VISIBLE);
            }
        }
    }

    private SingleSelectionListAdapter getSingleSelectionListAdapter() {

        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < selectionStatuses.size(); i++) {
            SelectionStatus selectionStatus = selectionStatuses.get(i);
            if (selectionStatus != null) {
                HashMap<String, Object> map = new HashMap<String, Object>();

                if (selectionStatus.getName() != null) {
                    map.put("tv_name", selectionStatus.getName());
                } else {
                    map.put("tv_name", "");
                }

                data.add(map);
            }
        }

        SingleSelectionListAdapter singleSelectionListAdapter = new SingleSelectionListAdapter(
                context, data,
                R.layout.item_single_selection,
                new String[]{"tv_name"}, new int[]{R.id.tv_name});

        return singleSelectionListAdapter;
    }

    class SingleSelectionListAdapter extends SimpleAdapter {
        Context mContext;
        private final LayoutInflater l_Inflater;

        public SingleSelectionListAdapter(Context context,
                                          List<? extends Map<String, ?>> data, int resource,
                                          String[] from, int[] to) {
            super(context, data, resource, from, to);

            mContext = context;
            l_Inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return selectionStatuses.size();
        }

        @Override
        public View getView(int position, View convertView, final ViewGroup parent) {
            SelectionStatus selectionStatus = selectionStatuses.get(position);

            if (convertView == null) {
                convertView = l_Inflater.inflate(
                        R.layout.item_single_selection, null);
            }

            TextView tvName = convertView.findViewById(R.id.tv_name);
            RadioButton rbSelection = convertView.findViewById(R.id.rb_selection);
            ImageView ivSelection = convertView.findViewById(R.id.iv_selection);

            if (gShowTickInsteadofRadio) {

                if (selectionStatus.isSelected()) {
                    ivSelection.setVisibility(View.VISIBLE);
                } else {
                    ivSelection.setVisibility(View.INVISIBLE);
                }

                rbSelection.setVisibility(View.GONE);
            } else {

                rbSelection.setVisibility(View.VISIBLE);
                ivSelection.setVisibility(View.GONE);
                rbSelection.setChecked(selectionStatus.isSelected());
            }

            tvName.setText(selectionStatus.getName());
            convertView.setTag(position);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (Integer) view.getTag();

                    if (position < selectionStatuses.size()) {
                        boolean isSelected = selectionStatuses.get(position).isSelected();

                        if (isSelected) {
                            selectionStatuses.get(position).setSelected(false);
                        } else {
                            for (SelectionStatus selectionStatus : selectionStatuses) {
                                selectionStatus.setSelected(false);
                            }
                            selectionStatuses.get(position).setSelected(true);
                        }
                    }

                    if (singleSelectionListAdapter != null) {
                        singleSelectionListAdapter.notifyDataSetChanged();
                    }
                }
            });
            return convertView;
        }
    }

    private MultipleSelectionListAdapter getMultipleSelectionListAdapter() {

        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < selectionStatuses.size(); i++) {
            SelectionStatus selectionStatus = selectionStatuses.get(i);
            if (selectionStatus != null) {
                HashMap<String, Object> map = new HashMap<String, Object>();

                if (selectionStatus.getName() != null) {
                    map.put("tv_name", selectionStatus.getName());
                } else {
                    map.put("tv_name", "");
                }

                data.add(map);
            }
        }

        MultipleSelectionListAdapter multipleSelectionListAdapter = new MultipleSelectionListAdapter(
                context, data,
                R.layout.item_multiple_selection,
                new String[]{"tv_name"}, new int[]{R.id.tv_name});

        return multipleSelectionListAdapter;
    }

    class MultipleSelectionListAdapter extends SimpleAdapter {
        Context mContext;
        private final LayoutInflater l_Inflater;

        public MultipleSelectionListAdapter(Context context,
                                            List<? extends Map<String, ?>> data, int resource,
                                            String[] from, int[] to) {
            super(context, data, resource, from, to);

            mContext = context;
            l_Inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return selectionStatuses.size();
        }

        @Override
        public View getView(int position, View convertView, final ViewGroup parent) {
            SelectionStatus selectionStatus = selectionStatuses.get(position);

            if (convertView == null) {
                convertView = l_Inflater.inflate(
                        R.layout.item_multiple_selection, null);
            }

            TextView tvName = convertView.findViewById(R.id.tv_name);
            CheckBox cbSelection = convertView.findViewById(R.id.cb_selection);

            tvName.setText(selectionStatus.getName());
            cbSelection.setChecked(selectionStatus.isSelected());

            convertView.setTag(position);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (Integer) view.getTag();

                    if (position < selectionStatuses.size()) {
                        SelectionStatus selectionStatus = selectionStatuses.get(position);
                        selectionStatus.setSelected(!selectionStatus.isSelected());
                    }

                    if (multipleSelectionListAdapter != null) {
                        multipleSelectionListAdapter.notifyDataSetChanged();
                    }
                }
            });
            return convertView;
        }
    }
}
