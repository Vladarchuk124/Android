package com.example.lab5;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    private Compass compassView;
    private TextView tvAzimuth;
    private TextView tvDirection;

    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];

    private boolean hasAccelerometerData = false;
    private boolean hasMagnetometerData = false;

    private final float[] rotationMatrix = new float[9];
    private final float[] remappedRotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];

    private float currentAzimuth = 0f;
    private boolean isFirstUpdate = true;

    private static final float ALPHA = 0.15f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        compassView = findViewById(R.id.compassView);
        tvAzimuth = findViewById(R.id.tvAzimuth);
        tvDirection = findViewById(R.id.tvDirection);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }

        if (accelerometer == null || magnetometer == null) {
            Toast.makeText(this, "На пристрої відсутні потрібні сенсори", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
        if (magnetometer != null) {
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.length);
            hasAccelerometerData = true;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.length);
            hasMagnetometerData = true;
        }

        if (!hasAccelerometerData || !hasMagnetometerData) {
            return;
        }

        boolean success = SensorManager.getRotationMatrix(
                rotationMatrix,
                null,
                accelerometerReading,
                magnetometerReading
        );

        if (!success) {
            return;
        }

        int rotation = getDisplayRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                SensorManager.remapCoordinateSystem(
                        rotationMatrix,
                        SensorManager.AXIS_X,
                        SensorManager.AXIS_Y,
                        remappedRotationMatrix
                );
                break;

            case Surface.ROTATION_90:
                SensorManager.remapCoordinateSystem(
                        rotationMatrix,
                        SensorManager.AXIS_Y,
                        SensorManager.AXIS_MINUS_X,
                        remappedRotationMatrix
                );
                break;

            case Surface.ROTATION_180:
                SensorManager.remapCoordinateSystem(
                        rotationMatrix,
                        SensorManager.AXIS_MINUS_X,
                        SensorManager.AXIS_MINUS_Y,
                        remappedRotationMatrix
                );
                break;

            case Surface.ROTATION_270:
                SensorManager.remapCoordinateSystem(
                        rotationMatrix,
                        SensorManager.AXIS_MINUS_Y,
                        SensorManager.AXIS_X,
                        remappedRotationMatrix
                );
                break;

            default:
                System.arraycopy(rotationMatrix, 0, remappedRotationMatrix, 0, rotationMatrix.length);
                break;
        }

        SensorManager.getOrientation(remappedRotationMatrix, orientationAngles);

        float azimuthInRadians = orientationAngles[0];
        float azimuthInDegrees = (float) Math.toDegrees(azimuthInRadians);

        if (azimuthInDegrees < 0) {
            azimuthInDegrees += 360;
        }

        currentAzimuth = smoothAzimuth(currentAzimuth, azimuthInDegrees);

        int roundedAzimuth = Math.round(currentAzimuth);
        String direction = getDirectionName(roundedAzimuth);

        compassView.setAzimuth(currentAzimuth);
        tvAzimuth.setText("Кут: " + roundedAzimuth + "°");
        tvDirection.setText("Напрямок телефона: " + direction);
    }

    private int getDisplayRotation() {
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) return Surface.ROTATION_0;
        return windowManager.getDefaultDisplay().getRotation();
    }

    private float smoothAzimuth(float oldValue, float newValue) {
        if (isFirstUpdate) {
            isFirstUpdate = false;
            return newValue;
        }

        float delta = newValue - oldValue;

        if (delta > 180) {
            delta -= 360;
        } else if (delta < -180) {
            delta += 360;
        }

        float result = oldValue + ALPHA * delta;

        if (result < 0) result += 360;
        if (result >= 360) result -= 360;

        return result;
    }

    private String getDirectionName(int degrees) {
        if (degrees >= 337 || degrees < 23) return "N";
        if (degrees < 68) return "NE";
        if (degrees < 113) return "E";
        if (degrees < 158) return "SE";
        if (degrees < 203) return "S";
        if (degrees < 248) return "SW";
        if (degrees < 293) return "W";
        return "NW";
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}