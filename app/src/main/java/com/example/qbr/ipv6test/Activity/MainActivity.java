package com.example.qbr.ipv6test.Activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qbr.ipv6test.Threads.Blue_client;
import com.example.qbr.ipv6test.Threads.Blue_server;
import com.example.qbr.ipv6test.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

public class MainActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    TextView title,textView_info;
    private Button Button_s, Button_cl, Button_cm;
    private String IMSI;
    private String NextIp;
    private String MyIp;
    private String provider;
    // 用来保存搜索到的设备信息
    private List<String> bluetoothDevices = new ArrayList<>();
    // ListView组件
    private ListView lvDevices;
    // ListView的字符串数组适配器
    private ArrayAdapter<String> arrayAdapter;
    private TelephonyManager telephonyManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice selectDevice;
    private final String tag = "b_client";
    final Handler mMessageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                JSONObject jsonObj = new JSONObject(msg.obj.toString());
                NextIp = jsonObj.getString("IP");
                String p = jsonObj.getString("Provider");
                textView_info.append("\n对方的ip是：\n" + NextIp +"\n"
                        +"对方运营商：" + p +"\n");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        telephonyManager = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        title = (TextView) findViewById(R.id.title);
        textView_info = (TextView) findViewById(R.id.info);
        Button_s = (Button) findViewById(R.id.Server);
        Button_cl = (Button) findViewById(R.id.Client);
        Button_cm = (Button) findViewById(R.id.Command);
        lvDevices = (ListView) findViewById(R.id.lvDevices);
        // 用Set集合保持已绑定的设备
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        if (devices.size() > 0) {
            for (BluetoothDevice bluetoothDevice : devices) {
                // 保存到arrayList集合中
                bluetoothDevices.add(bluetoothDevice.getName() + ":"
                        + bluetoothDevice.getAddress() + "\n");
            }
        }
        // 为listview设置字符换数组适配器
        arrayAdapter = new ArrayAdapter<>(this, R.layout.array_adapter, bluetoothDevices);
        // 为listView绑定适配器
        lvDevices.setAdapter(arrayAdapter);
        // 为listView设置item点击事件侦听
        lvDevices.setOnItemClickListener(this);
        Button_s.setOnClickListener(this);
        Button_cl.setOnClickListener(this);
        Button_cm.setOnClickListener(this);
        MyIp = getLocalHostIp();
        provider = getProvidersName();
        textView_info.setText("本机的ip是：\n" + MyIp+"\n"+ "本机运营商："+ provider +"\n");
        // 注册广播接收器。接收蓝牙发现讯息
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        if (mBluetoothAdapter.startDiscovery()) {
            Log.d(tag, "启动蓝牙扫描设备...");
        }
        Blue_server blueServer = new Blue_server(mMessageHandler);
        blueServer.start();

    }

    // 广播接收发现蓝牙设备
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 判断这个设备是否是之前已经绑定过了，如果是则不需要添加，在程序初始化的时候已经添加了
                if (device.getBondState() != BluetoothDevice.BOND_BONDED && !bluetoothDevices.contains(device.getName() + ":"
                        + device.getAddress() + "\n")) {
                    // 设备没有绑定过，则将其保持到arrayList集合中
                    bluetoothDevices.add(device.getName() + ":"
                            + device.getAddress() + "\n");
                    // 更新字符串数组适配器，将内容显示在listView中
                    arrayAdapter.notifyDataSetChanged();
                }

//              //蓝牙搜索是非常消耗系统资源开销的过程，一旦发现了目标感兴趣的设备，可以考虑关闭扫描。
//                    mBluetoothAdapter.cancelDiscovery();
            }else if (action
                    .equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                setTitle("搜索完成");
            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Server:
                Intent intent_s = new Intent(MainActivity.this, ServerActivity.class);
                Dialog(intent_s);
                break;

            case R.id.Client:
                Intent intent_c = new Intent(MainActivity.this, ClientActivity.class);
                intent_c.putExtra("Ip_server",NextIp);
                Dialog(intent_c);
                break;

            case R.id.Command:
                Intent intent_cm = new Intent(MainActivity.this, CommandActivity.class);
                intent_cm.putExtra("Ip_server",NextIp);
                startActivity(intent_cm);
                break;

        }

    }

    public void Dialog(final Intent intent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择传输方式");
        final String[] items = {"TCP", "UDP"};
        //创建单选对话框
        //第一个参数:单选对话框中显示的条目所在的字符串数组
        //第二个参数:默认选择的条目的下标(-1表示默认没有选择任何条目)
        //第三个参数:设置事件监听
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            //which:用户所选的条目的下标；dialog:触发这个方法的对话框
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(
                        MainActivity.this,
                        "您选择的传输方式是:" + items[which],
                        Toast.LENGTH_SHORT).show();
                dialog.dismiss();    //关闭对话框
                intent.putExtra("tran",items[which]);
                startActivity(intent);
            }
        });
        builder.show();          //显示单选对话框
    }


    // 得到本机ip地址
    public String getLocalHostIp()
    {
        String ipaddress = "无";
        try
        {
            Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements())
            {
                NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (inet.hasMoreElements())
                {
                    InetAddress ip = inet.nextElement();
                    // 在这里如果不加isIPv4Address的判断,直接返回,在4.0上获取到的是类似于fe80::1826:66ff:fe23:48e%p2p0的ipv6的地址
                    if (!ip.isLoopbackAddress() && !ip.isLinkLocalAddress()
                            && ip instanceof Inet6Address)
                    {
                        return ip.getHostAddress();
                    }
                }
            }
        }
        catch (SocketException e)
        {
            Log.e("failed", "获取本地ip地址失败");
            e.printStackTrace();
        }
        return ipaddress;
    }

    public String getProvidersName() {
        String ProvidersName = null;
        // 返回唯一的用户ID;就是这张卡的编号神马的
        IMSI = telephonyManager.getSubscriberId();
        // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
        System.out.println(IMSI);
        if(IMSI!=null){
            if(IMSI.startsWith("46000") || IMSI.startsWith("46002") || IMSI.startsWith("46007")) {
                ProvidersName = "中国移动";
            } else if(IMSI.startsWith("46001")) {
                ProvidersName = "中国联通";
            } else if(IMSI.startsWith("46003")) {
                ProvidersName = "中国电信";
            }
        }
        return ProvidersName;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 获取到这个设备的信息
        String s = arrayAdapter.getItem(position);
        // 对其进行分割，获取到这个设备的地址
        String address = s.substring(s.indexOf(":") + 1).trim();
        // 判断当前是否还是正在搜索周边设备，如果是则暂停搜索
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        // 如果选择设备为空则代表还没有选择设备
        if (selectDevice == null) {
            //通过地址获取到该设备
            selectDevice = mBluetoothAdapter.getRemoteDevice(address);
        }
        new Thread(new Blue_client(selectDevice, MyIp, provider)).start();

    }
}
