package com.zeppatech.p2pdemo.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.zeppatech.p2pdemo.MainActivity;
import com.zeppatech.p2pdemo.R;
import com.zeppatech.p2pdemo.Utils;
import com.zeppatech.p2pdemo.data.DataSingleton;
import com.zeppatech.p2pdemo.data.DeviceInfo;
import com.zeppatech.p2pdemo.data.DevicesAdapter;
import com.zeppatech.p2pdemo.p2p.MessageSenderService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by PSchuette on 8/25/16.
 */
public class SendFragment extends Fragment implements View.OnClickListener, DataSingleton.WiFiDevicesListener {


    DevicesAdapter mDeviceAdapter;
    Date selectedDate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_send, container, false);

        // Set click listeners
        v.findViewById(R.id.send_birthday).setOnClickListener(this);
        v.findViewById(R.id.send_button).setOnClickListener(this);

        if(selectedDate==null)
            selectedDate = new Date();

        DataSingleton.getInstance().registerWiFiDevicesListener(this);
        if(mDeviceAdapter==null)
            mDeviceAdapter = new DevicesAdapter(getContext(),DataSingleton.getInstance().getConnectedDevices(),true );


        return v;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        drawDevices();
        setSelectedDateText();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        DataSingleton.getInstance().unregisterWiFiDevicesListener(this);
    }


    @Override
    public void onClick(View view) {

        switch(view.getId()){

            case R.id.send_birthday:
                Calendar cal = Calendar.getInstance();
                cal.setTime(selectedDate);

                DatePickerDialog dpd = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, month);
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        selectedDate = cal.getTime();
                        setSelectedDateText();
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
                dpd.show();

                break;

            case R.id.send_button:
                // TODO: dispatch message to selected recipients
                EditText editText = (EditText) getView().findViewById(R.id.send_name);
                String name = null;
                if(editText!=null) {
                    name = editText.getText().toString();
                }
                if (name==null || name.isEmpty()) {
                    // TODO: notify user they must enter a name
                    ((MainActivity) getActivity()).displayAlertMessage("Name Invalid", "Must enter a name associate with this birthday");
                    break;
                }

                List<DeviceInfo> recipientDevices = mDeviceAdapter.getSelectedDevices();
                if(recipientDevices.isEmpty()){
                    // TODO: notify user they must select recipient devices
                    ((MainActivity) getActivity()).displayAlertMessage("Devices Invalid", "Must select at least 1 recipient device");

                    break;
                }




                // Start an intent to dispatch this message to the recipient device
                String groupOwnerAddress = DataSingleton.getInstance().getGroupOwnerIPAddress();

                if(groupOwnerAddress!=null) {
                    for(DeviceInfo device: recipientDevices) {
                        try {
                            // build the recipient json message
                            JSONObject json = new JSONObject();
                            json.put("from", DataSingleton.getInstance().getDeviceIPAddress());
                            json.put("to", device.getIp());
                            json.put("name", name);
                            json.put("date", selectedDate.getTime());

                            if(DataSingleton.getInstance().isGroupOwner()){
                                // this is the group owner, just add message to the map
                            }

                            Intent serviceIntent = new Intent(getActivity(), MessageSenderService.class);
                            serviceIntent.setAction(MessageSenderService.ACTION_SEND_MESSAGE);
                            serviceIntent.putExtra(MessageSenderService.EXTRAS_JSON, json.toString());
                            serviceIntent.putExtra(MessageSenderService.EXTRAS_GROUP_OWNER_ADDRESS,
                                    groupOwnerAddress);
                            serviceIntent.putExtra(MessageSenderService.EXTRAS_RECIPIENT_IP, device.getIp());
                            serviceIntent.putExtra(MessageSenderService.EXTRAS_GROUP_OWNER_PORT, 8988);
                            getActivity().startService(serviceIntent);

                        } catch (JSONException e){
                            ((MainActivity) getActivity()).displayAlertMessage("Internal Error", "Issue building message, try again");
                            break;
                        }

                    }

                }
                break;
        }

    }

    @Override
    public void onWifiP2pDeviceListUpdated(List<DeviceInfo> deviceInfoList) {
        // Update the list of available devices
        mDeviceAdapter.updateDeviceList(deviceInfoList);
        drawDevices();
    }

    /**
     * Draw the available devices
     */
    protected void drawDevices(){

        if(mDeviceAdapter!=null) {
            LinearLayout layout = (LinearLayout) getView().findViewById(R.id.send_recipients_holder);
            if(layout!=null){
                // Clear all the views from the layout
                layout.removeAllViews();
                for(int i=0; i< mDeviceAdapter.getCount();i++){
                    View v = mDeviceAdapter.getView(i, null, layout);
                    layout.addView(v);
                    v.setOnClickListener(mDeviceAdapter);
                }

            }
        }

    }

    /**
     * Set the birthday button text to the selected date
     */
    protected void setSelectedDateText(){
        Button birthdayButton = (Button) getView().findViewById(R.id.send_birthday);
        if(birthdayButton!=null) {
            birthdayButton.setText("Select birthday (" + Utils.formatDate(selectedDate) + ")");
        }
    }
}
