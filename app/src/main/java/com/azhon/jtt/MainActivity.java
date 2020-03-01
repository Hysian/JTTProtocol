package com.azhon.jtt;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.azhon.jtt808.JTT808Manager;
import com.azhon.jtt808.bean.JTT808Bean;
import com.azhon.jtt808.bean.TerminalParamsBean;
import com.azhon.jtt808.listener.OnConnectionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnConnectionListener,
        View.OnClickListener {

    private static final String TAG = "MainActivity";

    //终端手机号
    public static final String PHONE = "013496388888";
    //制造商ID
    public static final String MANUFACTURER_ID = "TYUAN";
    //终端型号
    public static final String TERMINAL_MODEL = "U47DPZAMSWAAHQCQ0000";
    //终端ID
    public static final String TERMINAL_ID = "LMY74DT";
    //经纬度
    public static long LAT = 31228068;
    public static long LNG = 121481323;
    private static int DEGREE = 1;

    private JTT808Manager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("部标JTT808,JTT1078,渝标协议封装");
        initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, 0x23);
        }

        manager = JTT808Manager.getInstance();
        manager.setOnConnectionListener(this)
                //http://47.108.30.48:8081/ivc/pages/admin.jsp 账号，jimu;密码，jm123456
                .init(PHONE, TERMINAL_ID, "113.207.109.61", 8085);
    }

    private void initView() {
        findViewById(R.id.btn_location).setOnClickListener(this);
        findViewById(R.id.btn_cy_alarm).setOnClickListener(this);
        findViewById(R.id.btn_call_alarm).setOnClickListener(this);
        findViewById(R.id.btn_zsqf_alarm).setOnClickListener(this);
        findViewById(R.id.btn_pljs_alarm).setOnClickListener(this);
        findViewById(R.id.btn_wzjsw_alarm).setOnClickListener(this);
        SeekBar seekBar = findViewById(R.id.sb);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                DEGREE = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    @Override
    public void onConnectionSateChange(int state) {
        switch (state) {
            case OnConnectionListener.CONNECTED:
                manager.register(MANUFACTURER_ID, TERMINAL_MODEL);
                break;
            case OnConnectionListener.DIS_CONNECT:
                Log.d(TAG, "断开连接");
                break;
            case OnConnectionListener.RE_CONNECT:
                Log.d(TAG, "重连");
                break;
            default:
                break;
        }
    }

    @Override
    public void receiveData(JTT808Bean jtt808Bean) {

    }

    @Override
    public void terminalParams(List<TerminalParamsBean> params) {
        for (TerminalParamsBean param : params) {
            int id = param.getId();
            if (Integer.class.equals(param.getClz())) {
                int value = (int) param.getValue();
            } else if (String.class.equals(param.getClz())) {
                String value = (String) param.getValue();
            } else if (Byte.class.equals(param.getClz())) {
                Byte value = (Byte) param.getValue();
            }
            switch (id) {
                //最高速度，单位为公里每小时(km/h)
                case 0x0055:
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public void onClick(View v) {
        if (manager == null) return;
        //附件列表
        List<File> files = new ArrayList<>();
        switch (v.getId()) {
            case R.id.btn_location:
                manager.uploadLocation(LAT, LNG);
                break;
            case R.id.btn_cy_alarm:
                files.add(new File(getExternalCacheDir() + "/2.png"));
                files.add(new File(getExternalCacheDir() + "/3.png"));
                manager.uploadAlarmInfoYB(LAT, LNG, 1, 1, 0, files);
                break;
            case R.id.btn_call_alarm:
                manager.uploadAlarmInfoYB(LAT, LNG, 2, 1, 0, files);
                break;
            case R.id.btn_zsqf_alarm:
                manager.uploadAlarmInfoYB(LAT, LNG, 3, 1, 0, files);
                break;
            case R.id.btn_pljs_alarm:
                manager.uploadAlarmInfoYB(LAT, LNG, 4, 1, DEGREE, files);
                break;
            case R.id.btn_wzjsw_alarm:
                manager.uploadAlarmInfoYB(LAT, LNG, 5, 1, 0, files);
                break;
            default:
                break;
        }
    }


}
