package com.example.lam.handin3;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class Compass extends ActionBarActivity implements SensorEventListener {

    private ImageView compassImage;
    private SensorManager sensormanager;
    private Sensor accelerometer;
    private Sensor magnometer;
    private float currentCompassAngle = 0;
    private float[] readingmagnometer = new float[3];
    private float[] readingaccelerometer = new float[3];

    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        compassImage = (ImageView) findViewById(R.id.compass);
        sensormanager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnometer = sensormanager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        backButton = (ImageView)findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onResume(){
        super.onResume();
        sensormanager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensormanager.registerListener(this, magnometer, SensorManager.SENSOR_DELAY_GAME);
    }

    protected void onPause(){
        super.onPause();
        sensormanager.unregisterListener(this, accelerometer);
        sensormanager.unregisterListener(this, magnometer);
    }

    private void doAnimation(float from, float to, View rotateme){

        RotateAnimation ra = new RotateAnimation(
                from,
                to,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(200);
        ra.setFillAfter(true);
        rotateme.startAnimation(ra);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float[] rotation = new float[9];
        float[] orientation = new float[3];

        if(event.sensor == accelerometer){
            readingaccelerometer[0] = event.values[0];
            readingaccelerometer[1] = event.values[1];
            readingaccelerometer[2] = event.values[2];
        }
        if(event.sensor == magnometer){
            readingmagnometer[0] = event.values[0];
            readingmagnometer[1] = event.values[1];
            readingmagnometer[2] = event.values[2];
        }

        sensormanager.getRotationMatrix(rotation,null,readingaccelerometer, readingmagnometer);
        sensormanager.getOrientation(rotation,orientation);
        float azimuthRadians = orientation[0];
        float azimuthDegrees = -(float) (Math.toDegrees(azimuthRadians)+360)%360;

        doAnimation(currentCompassAngle, azimuthDegrees, compassImage);
        currentCompassAngle = azimuthDegrees;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
