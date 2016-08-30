package com.zeppatech.p2pdemo.data;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.zeppatech.p2pdemo.R;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by PSchuette on 8/28/16.
 */
public class DevicesAdapter extends BaseAdapter implements AdapterView.OnItemClickListener, View.OnClickListener {

    private static final String TAG = DevicesAdapter.class.getSimpleName();

    Context context;
    List<DeviceInfo> devices;
    boolean isSelectable;
    LinkedList<DeviceInfo> selectedDevices;

    public DevicesAdapter(Context context, List<DeviceInfo> devices, boolean isSelectable) {
        super();
        this.context = context;
        this.isSelectable = true;
        this.devices = devices;
        selectedDevices = new LinkedList<DeviceInfo>();

    }

    // Update the device list
    public void updateDeviceList(Collection<DeviceInfo> deviceList){
        this.devices.clear();
        this.devices.addAll(deviceList);
    }

    @Override
    public int getCount() {
        if(devices==null){
            return 0;
        } else {
            return devices.size();
        }
    }

    @Override
    public DeviceInfo getItem(int i) {
        if(devices==null){
            return null;
        } else {
            return devices.get(i);
        }
    }

    @Override
    public long getItemId(int i) {
        if(devices==null){
            return -1;
        } else {
            return getItem(i).hashCode();
        }
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.view_device, viewGroup, false);
        }

        DeviceInfo device = getItem(i);
        view.setTag(device);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.device_checkbox);
        if(isSelectable){
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setChecked(selectedDevices.contains(device));
        } else {
            checkBox.setVisibility(View.GONE);
        }

        TextView textView = (TextView) view.findViewById(R.id.device_name);
        textView.setText(device.getName()+ " (" + device.getIp()+")");

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        onClick(view);
    }

    @Override
    public void onClick(View view) {
        if(!isSelectable)
            return;
        CheckBox box = (CheckBox) view.findViewById(R.id.device_checkbox);
        DeviceInfo device = (DeviceInfo) view.getTag();
        if(device==null){
            Log.e(TAG, "Device not tagged to item");
        } else if ((box == null)){
            Log.e(TAG, "Checkbox item not found");
        } else {
            if(selectedDevices.contains(device)){
                selectedDevices.remove(device);
                box.setChecked(false);
            } else {
                selectedDevices.add(device);
                box.setChecked(true);
            }
        }

    }

    /**
     * Get a list of wifi devices selected
     * @return
     */
    public LinkedList<DeviceInfo> getSelectedDevices() {
        return selectedDevices;
    }

    /**
     * Clear all the selected devices and notify adapter dataset changed
     */
    public void clearSelection(){
        this.selectedDevices.clear();
        this.notifyDataSetChanged();
    }
}
