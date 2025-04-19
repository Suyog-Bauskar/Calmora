package com.suyogbauskar.calmora.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.suyogbauskar.calmora.R;

import java.util.ArrayList;

public class LeaderBoardFragment extends Fragment {

    private BarChart barChart;

    public LeaderBoardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leader_board, container, false);

        barChart = view.findViewById(R.id.barChart);
        setupBarChart();

        return view;
    }

    private void setupBarChart() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, 15f)); // Monday
        entries.add(new BarEntry(1f, 30f)); // Tuesday
        entries.add(new BarEntry(2f, 25f)); // Wednesday
        entries.add(new BarEntry(3f, 40f)); // Thursday
        entries.add(new BarEntry(4f, 50f)); // Friday
        entries.add(new BarEntry(5f, 35f)); // Saturday
        entries.add(new BarEntry(6f, 20f)); // Sunday

        BarDataSet dataSet = new BarDataSet(entries, "Weekly Progress");
        dataSet.setColor(Color.parseColor("#4CAF50"));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        // Customize X-Axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(7);

        xAxis.setValueFormatter(new ValueFormatter() {
            private final String[] days = {"M", "T", "W", "T", "F", "S", "S"};

            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < days.length) {
                    return days[index];
                } else {
                    return "";
                }
            }
        });

        barChart.invalidate(); // Refresh chart
    }
}
