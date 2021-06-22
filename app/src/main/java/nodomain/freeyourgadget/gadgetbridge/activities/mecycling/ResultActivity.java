package nodomain.freeyourgadget.gadgetbridge.activities.mecycling;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.activities.base.BaseActivity;

public class ResultActivity extends BaseActivity {

    private TextView tv_result_distance;
    private TextView tv_result_speed_avg;
    private TextView tv_result_calorie_burn;
    private TextView tv_result_calorie_needs;
    private LineGraphSeries<DataPoint> series;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);


        initLayout();

        // Using the GGraphView Library
        GraphView graph = (GraphView) findViewById(R.id.graph);

        //Create the Datapoins
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);

        // customize the viewport
        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(60);
        viewport.setMaxY(100);
        viewport.setScrollable(true);

        String distance = getIntent().getStringExtra("distance");
        String speed = getIntent().getStringExtra("speed");
        String calorie_burn = getIntent().getStringExtra("cal_burn");
        String calorie_need = getIntent().getStringExtra("cal_need");

        tv_result_distance.setText(getString(R.string.distance_placehorder, distance));
        tv_result_speed_avg.setText(getString(R.string.speed_placehorder, speed));
        tv_result_calorie_burn.setText(getString(R.string.cal_burn_placehorder, calorie_burn));
        tv_result_calorie_needs.setText(getString(R.string.cal_need_placehorder, calorie_need));
    }

    private void initLayout(){
        tv_result_distance = findViewById(R.id.tv_result_distance);
        tv_result_speed_avg = findViewById(R.id.tv_result_speed);
        tv_result_calorie_burn = findViewById(R.id.tv_calorie_burn);
        tv_result_calorie_needs = findViewById(R.id.tv_calorie_need);
    }

}

