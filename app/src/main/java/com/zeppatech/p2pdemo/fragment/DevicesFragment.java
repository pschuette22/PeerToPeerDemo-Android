package com.zeppatech.p2pdemo.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.zeppatech.p2pdemo.MainActivity;
import com.zeppatech.p2pdemo.R;
import com.zeppatech.p2pdemo.data.DataSingleton;
import com.zeppatech.p2pdemo.data.DeviceInfo;
import com.zeppatech.p2pdemo.data.DevicesAdapter;

import java.util.List;

/**
 * Created by PSchuette on 8/25/16.
 *
 * Fragment for discovering other devices
 */
public class DevicesFragment extends Fragment implements DataSingleton.WiFiDevicesListener, View.OnClickListener {

    private static final String TAG = DevicesFragment.class.getSimpleName();

    DevicesAdapter mDeviceAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_devices, container, false);
        TextView devicesMsg = (TextView) v.findViewById(R.id.devices_msg);
        v.findViewById(R.id.devices_discover).setOnClickListener(this);

        // Initialize the devices layout items

        DataSingleton.getInstance().registerWiFiDevicesListener(this);
        ListView lv = (ListView) v.findViewById(R.id.device_list);
        mDeviceAdapter = new DevicesAdapter(getContext(),DataSingleton.getInstance().getConnectedDevices(),false );
        lv.setAdapter(mDeviceAdapter);
        setAvailableDevicesMessage(devicesMsg);


        return v;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Unregister the wifi devices listener
        DataSingleton.getInstance().unregisterWiFiDevicesListener(this);

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.devices_discover:

                Activity mActivity = getActivity();
                if(mActivity instanceof  MainActivity){
                    ((MainActivity) mActivity).getWiFiDirectBroadcastReceiver().updatePeers();
                } else {
                    Log.wtf(TAG, "Expected parent activity to be Main Activity because crummy tight coupling");
                }

                break;
        }
    }

    @Override
    public void onWifiP2pDeviceListUpdated(List<DeviceInfo> deviceInfoList) {
        // List of devices has changed, update the interface
        mDeviceAdapter.updateDeviceList(deviceInfoList);
        mDeviceAdapter.notifyDataSetChanged();

        TextView devicesMsg = (TextView) getView().findViewById(R.id.devices_msg);
        if(devicesMsg!=null)
            setAvailableDevicesMessage(devicesMsg);
    }

    /**
     * Set the text indicating the available devices
     */
    private void setAvailableDevicesMessage(TextView devicesMsg){

        if(mDeviceAdapter == null || mDeviceAdapter.getCount()==0){
            devicesMsg.setText("No Available Devices");
        } else {
            devicesMsg.setText(mDeviceAdapter.getCount() + " available device" + (mDeviceAdapter.getCount()==1?"":"s")+": ");
        }
    }
}
