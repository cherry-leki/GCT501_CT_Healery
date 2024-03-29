/*  Copyright (C) 2015-2018 Andreas Shimokawa, Carsten Pfeiffer, Daniele
    Gobbetti, Lem Dulfo

    This file is part of Gadgetbridge.

    Gadgetbridge is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Gadgetbridge is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package healery.gadgetbridge.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import healery.gadgetbridge.activities.ConfigureAlarms;
import healery.gadgetbridge.activities.charts.ChartsActivity;
import healery.gadgetbridge.devices.DeviceCoordinator;
import healery.gadgetbridge.devices.DeviceManager;
import healery.gadgetbridge.GBApplication;
import healery.gadgetbridge.R;
import healery.healery.ActivityMainNavi;
import healery.gadgetbridge.impl.GBDevice;
import healery.gadgetbridge.model.BatteryState;
import healery.gadgetbridge.model.RecordedDataTypes;
import healery.gadgetbridge.util.DeviceHelper;
import healery.gadgetbridge.util.GB;


/**
 * Adapter for displaying GBDevice instances.
 */
public class GBDeviceAdapterv2 extends RecyclerView.Adapter<GBDeviceAdapterv2.ViewHolder> {

    private final Context context;
    private List<GBDevice> deviceList;
    private int expandedDevicePosition = RecyclerView.NO_POSITION;
    private ViewGroup parent;

    public GBDeviceAdapterv2(Context context, List<GBDevice> deviceList) {
        this.context = context;
        this.deviceList = deviceList;
    }

    @NonNull
    @Override
    public GBDeviceAdapterv2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_itemv2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final GBDevice device = deviceList.get(position);
        final DeviceCoordinator coordinator = DeviceHelper.getInstance().getCoordinator(device);

        holder.container.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (device.isInitialized() || device.isConnected()) {
                    showTransientSnackbar(R.string.controlcenter_snackbar_need_longpress);
                } else {
                    showTransientSnackbar(R.string.controlcenter_snackbar_connecting);
                    GBApplication.deviceService().connect(device);
                }
            }
        });

        holder.container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (device.getState() != GBDevice.State.NOT_CONNECTED) {
                    showTransientSnackbar(R.string.controlcenter_snackbar_disconnecting);
                    GBApplication.deviceService().disconnect();
                }
                return true;
            }
        });
        holder.deviceImageView.setImageResource(R.drawable.level_list_device);
        //level-list does not allow negative values, hence we always add 100 to the key.
        holder.deviceImageView.setImageLevel(device.getType().getKey() + 100 + (device.isInitialized() ? 100 : 0));

        holder.deviceNameLabel.setText(getUniqueDeviceName(device));

        if (device.isBusy()) {
            holder.deviceStatusLabel.setText(device.getBusyTask());
            holder.busyIndicator.setVisibility(View.VISIBLE);
        } else {
            holder.deviceStatusLabel.setText(device.getStateString());
            holder.busyIndicator.setVisibility(View.INVISIBLE);
        }

        //begin of action row
        //battery
        holder.batteryStatusBox.setVisibility(View.GONE);
        short batteryLevel = device.getBatteryLevel();
        if (batteryLevel != GBDevice.BATTERY_UNKNOWN) {
            holder.batteryStatusBox.setVisibility(View.VISIBLE);
            holder.batteryStatusLabel.setText(device.getBatteryLevel() + "%");
            BatteryState batteryState = device.getBatteryState();
            if (BatteryState.BATTERY_CHARGING.equals(batteryState) ||
                    BatteryState.BATTERY_CHARGING_FULL.equals(batteryState)) {
                holder.batteryIcon.setImageLevel(device.getBatteryLevel() + 100);
            } else {
                holder.batteryIcon.setImageLevel(device.getBatteryLevel());
            }
        }

        //heartrate
        holder.showHeartRate.setVisibility(View.GONE);
        if(device.isInitialized() && coordinator.supportsActivityDataFetching()) {
            holder.showHeartRate.setVisibility(View.VISIBLE);
        }
        holder.showHeartRate.setImageLevel(ActivityMainNavi.stressCount);
        System.out.println("holderImageLevel: " + ActivityMainNavi.stressCount);
        holder.showHeartRate.setOnClickListener(new View.OnClickListener()

                                                {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent startIntent;
                                                        //startIntent = new Intent(context, HealeryActivity.class);
                                                        startIntent = new Intent(context, ActivityMainNavi.class);
                                                        startIntent.putExtra(GBDevice.EXTRA_DEVICE, device);
                                                        context.startActivity(startIntent);
                                                    }
                                                }
        );


        //fetch activity data
        holder.fetchActivityDataBox.setVisibility((device.isInitialized() && coordinator.supportsActivityDataFetching()) ? View.VISIBLE : View.GONE);
        holder.fetchActivityData.setOnClickListener(new View.OnClickListener()

                                                    {
                                                        @Override
                                                        public void onClick(View v) {
                                                            showTransientSnackbar(R.string.busy_task_fetch_activity_data);
                                                            GBApplication.deviceService().onFetchRecordedData(RecordedDataTypes.TYPE_ACTIVITY);
                                                        }
                                                    }
        );

        //set alarms
        holder.setAlarmsView.setVisibility(coordinator.supportsAlarmConfiguration() ? View.VISIBLE : View.GONE);
        holder.setAlarmsView.setOnClickListener(new View.OnClickListener()

                                                {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent startIntent;
                                                        startIntent = new Intent(context, ConfigureAlarms.class);
                                                        startIntent.putExtra(GBDevice.EXTRA_DEVICE, device);
                                                        context.startActivity(startIntent);
                                                    }
                                                }
        );

        //show graphs
        holder.showActivityGraphs.setVisibility(coordinator.supportsActivityTracking() ? View.VISIBLE : View.GONE);
        holder.showActivityGraphs.setOnClickListener(new View.OnClickListener()

                                                     {
                                                         @Override
                                                         public void onClick(View v) {
                                                             Intent startIntent;
                                                             startIntent = new Intent(context, ChartsActivity.class);
                                                             startIntent.putExtra(GBDevice.EXTRA_DEVICE, device);
                                                             context.startActivity(startIntent);
                                                         }
                                                     }
        );

        ItemWithDetailsAdapter infoAdapter = new ItemWithDetailsAdapter(context, device.getDeviceInfos());
        infoAdapter.setHorizontalAlignment(true);
        holder.deviceInfoList.setAdapter(infoAdapter);
        justifyListViewHeightBasedOnChildren(holder.deviceInfoList);
        holder.deviceInfoList.setFocusable(false);

        final boolean detailsShown = position == expandedDevicePosition;
        boolean showInfoIcon = device.hasDeviceInfos() && !device.isBusy();
        holder.deviceInfoView.setVisibility(showInfoIcon ? View.VISIBLE : View.GONE);
        holder.deviceInfoBox.setActivated(detailsShown);
        holder.deviceInfoBox.setVisibility(detailsShown ? View.VISIBLE : View.GONE);
        holder.deviceInfoView.setOnClickListener(new View.OnClickListener() {
                                                     @Override
                                                     public void onClick(View v) {
                                                         expandedDevicePosition = detailsShown ? -1 : position;
                                                         TransitionManager.beginDelayedTransition(parent);
                                                         notifyDataSetChanged();
                                                     }
                                                 }

        );

        holder.findDevice.setVisibility(device.isInitialized() ? View.VISIBLE : View.GONE);
        holder.findDevice.setOnClickListener(new View.OnClickListener()

                                             {
                                                 @Override
                                                 public void onClick(View v) {
                                                     GBApplication.deviceService().onFindDevice(true);
                                                     //TODO: extract string resource if we like this solution.
                                                     Snackbar.make(parent, R.string.control_center_find_lost_device, Snackbar.LENGTH_INDEFINITE).setAction("Found it!", new View.OnClickListener() {
                                                         @Override
                                                         public void onClick(View v) {
                                                             GBApplication.deviceService().onFindDevice(false);
                                                         }
                                                     }).setCallback(new Snackbar.Callback() {
                                                         @Override
                                                         public void onDismissed(Snackbar snackbar, int event) {
                                                             GBApplication.deviceService().onFindDevice(false);
                                                             super.onDismissed(snackbar, event);
                                                         }
                                                     }).show();
//                                                     ProgressDialog.show(
//                                                             context,
//                                                             context.getString(R.string.control_center_find_lost_device),
//                                                             context.getString(R.string.control_center_cancel_to_stop_vibration),
//                                                             true, true,
//                                                             new DialogInterface.OnCancelListener() {
//                                                                 @Override
//                                                                 public void onCancel(DialogInterface dialog) {
//                                                                     GBApplication.deviceService().onFindDevice(false);
//                                                                 }
//                                                             });
                                                 }
                                             }

        );

        //remove device, hidden under details
        holder.removeDevice.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setCancelable(true)
                        .setTitle(context.getString(R.string.controlcenter_delete_device_name, device.getName()))
                        .setMessage(R.string.controlcenter_delete_device_dialogmessage)
                        .setPositiveButton(R.string.Delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    DeviceCoordinator coordinator = DeviceHelper.getInstance().getCoordinator(device);
                                    if (coordinator != null) {
                                        coordinator.deleteDevice(device);
                                    }
                                    DeviceHelper.getInstance().removeBond(device);
                                } catch (Exception ex) {
                                    GB.toast(context, "Error deleting device: " + ex.getMessage(), Toast.LENGTH_LONG, GB.ERROR, ex);
                                } finally {
                                    Intent refreshIntent = new Intent(DeviceManager.ACTION_REFRESH_DEVICELIST);
                                    LocalBroadcastManager.getInstance(context).sendBroadcast(refreshIntent);
                                }
                            }
                        })
                        .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }
        });

    }



    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        CardView container;

        ImageView deviceImageView;
        TextView deviceNameLabel;
        TextView deviceStatusLabel;

        //actions
        LinearLayout batteryStatusBox;
        TextView batteryStatusLabel;
        ImageView batteryIcon;
        LinearLayout fetchActivityDataBox;
        ImageView fetchActivityData;
        ProgressBar busyIndicator;
        ImageView setAlarmsView;
        ImageView showActivityGraphs;

        ImageView deviceInfoView;
        //overflow
        final RelativeLayout deviceInfoBox;
        ListView deviceInfoList;
        ImageView findDevice;
        ImageView removeDevice;

        ImageView showHeartRate;

        ViewHolder(View view) {
            super(view);
            container = view.findViewById(R.id.card_view);

            deviceImageView = view.findViewById(R.id.device_image);
            deviceNameLabel = view.findViewById(R.id.device_name);
            deviceStatusLabel = view.findViewById(R.id.device_status);

            //actions
            batteryStatusBox = view.findViewById(R.id.device_battery_status_box);
            batteryStatusLabel = view.findViewById(R.id.battery_status);
            batteryIcon = view.findViewById(R.id.device_battery_status);
            fetchActivityDataBox = view.findViewById(R.id.device_action_fetch_activity_box);
            fetchActivityData = view.findViewById(R.id.device_action_fetch_activity);
            busyIndicator = view.findViewById(R.id.device_busy_indicator);
            setAlarmsView = view.findViewById(R.id.device_action_set_alarms);
            showActivityGraphs = view.findViewById(R.id.device_action_show_activity_graphs);
            deviceInfoView = view.findViewById(R.id.device_info_image);

            deviceInfoBox = view.findViewById(R.id.device_item_infos_box);
            //overflow
            deviceInfoList = view.findViewById(R.id.device_item_infos);
            findDevice = view.findViewById(R.id.device_action_find);
            removeDevice = view.findViewById(R.id.device_action_remove);

            showHeartRate = view.findViewById(R.id.show_heartRate);
        }

    }

    private void justifyListViewHeightBasedOnChildren(ListView listView) {
        ArrayAdapter adapter = (ArrayAdapter) listView.getAdapter();

        if (adapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }

    private String getUniqueDeviceName(GBDevice device) {
        String deviceName = device.getName();
        if (!isUniqueDeviceName(device, deviceName)) {
            if (device.getModel() != null) {
                deviceName = deviceName + " " + device.getModel();
                if (!isUniqueDeviceName(device, deviceName)) {
                    deviceName = deviceName + " " + device.getShortAddress();
                }
            } else {
                deviceName = deviceName + " " + device.getShortAddress();
            }
        }
        return deviceName;
    }

    private boolean isUniqueDeviceName(GBDevice device, String deviceName) {
        for (int i = 0; i < deviceList.size(); i++) {
            GBDevice item = deviceList.get(i);
            if (item == device) {
                continue;
            }
            if (deviceName.equals(item.getName())) {
                return false;
            }
        }
        return true;
    }

    private void showTransientSnackbar(int resource) {
        Snackbar snackbar = Snackbar.make(parent, resource, Snackbar.LENGTH_SHORT);

        //View snackbarView = snackbar.getView();

        // change snackbar text color
        //int snackbarTextId = android.support.design.R.id.snackbar_text;
        //TextView textView = snackbarView.findViewById(snackbarTextId);
        //textView.setTextColor();
        //snackbarView.setBackgroundColor(Color.MAGENTA);
        snackbar.show();
    }

}
