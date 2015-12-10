package dev.mirror.library.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.List;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class IpUtil {


	@SuppressWarnings("deprecation")
	public String FormatIP (int ip) {
		return Formatter.formatIpAddress(ip);
	}

	// 获取Android手机中SD卡存储信息 获取剩余空间
	public void getSDCardInfo() {
		// 在manifest.xml文件中要添加
		/*
		 * <uses-permission
		 * android:name="android.permission.WRITE_EXTERNAL_STORAGE">
		 * </uses-permission>
		 */
		// 需要判断手机上面SD卡是否插好，如果有SD卡的情况下，我们才可以访问得到并获取到它的相关信息，当然以下这个语句需要用if做判断
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// 取得sdcard文件路径
			File path = Environment.getExternalStorageDirectory();
			StatFs statfs = new StatFs(path.getPath());
			// 获取block的SIZE
			long blocSize = statfs.getBlockSize();
			// 获取BLOCK数量
			long totalBlocks = statfs.getBlockCount();
			// 空闲的Block的数量
			long availaBlock = statfs.getAvailableBlocks();
			// 计算总空间大小和空闲的空间大小
			// 存储空间大小跟空闲的存储空间大小就被计算出来了。
			long availableSize = blocSize * availaBlock;
			// (availableBlocks * blockSize)/1024 KIB 单位
			// (availableBlocks * blockSize)/1024 /1024 MIB单位
			long allSize = blocSize * totalBlocks;
		}

	}

	// 获取手机ip method-1
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return null;
	} // 需要权限<uses-permission
	// android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
	// <uses-permission
	// android:name="android.permission.INTERNET"></uses-permission>

	// 获取手机ip method-2
	// 首先设置用户权限
	// <uses-permission
	// android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
	// <uses-permission
	// android:name="android.permission.CHANGE_WIFI_STATE"></uses-permission>
	// <uses-permission
	// android:name="android.permission.WAKE_LOCK"></uses-permission>
	public static  String getLocalIpAddress2(Context context) {
		// 获取wifi服务
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		// 判断wifi是否开启
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ip = intToIp(ipAddress);
		return ip;
	}

	private static String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + (i >> 24 & 0xFF);
	}

	// 查看本机外网IP
	/*
	 * 该方法需要设备支持上网 查看
	 * System.out.println((GetNetIp("http://fw.qq.com/ipaddress"))); 加权限
	 * <uses-permission
	 * android:name="android.permission.INTERNET"></uses-permission>
	 * 通过获取http://fw.qq.com/ipaddress网页取得外网IP 这里有几个查看IP的网址然后提取IP试试。
	 * http://ip168.com/ http://www.cmyip.com/ http://city.ip138.com/ip2city.asp
	 */
	public static String GetNetIp(String ipaddr) {
		URL infoUrl = null;
		InputStream inStream = null;
		try {
			infoUrl = new URL(ipaddr);
			URLConnection connection = infoUrl.openConnection();
			HttpURLConnection httpConnection = (HttpURLConnection) connection;
			int responseCode = httpConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				inStream = httpConnection.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inStream, "utf-8"));
				StringBuilder strber = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null)
					strber.append(line + "\n");
				inStream.close();
				return strber.toString();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// 获取手机MAC地址
	public static String getMacAddress(Context context) {
		String result = "";
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		result = wifiInfo.getMacAddress();
		// Log.i(TAG, "macAdd:" + result);
		return result;
	}

	// 获取手机屏幕高度
	public String getWeithAndHeight(Context context) {
		// 这种方式在service中无法使用，
		DisplayMetrics dm = new DisplayMetrics();
		int width = dm.widthPixels; // 宽
		int height = dm.heightPixels; // 高
		float density = dm.density; // 屏幕密度（0.75 / 1.0 / 1.5）
		int densityDpi = dm.densityDpi; // 屏幕密度DPI（120 / 160 / 240）
		// 在service中也能得到高和宽
		WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		width = mWindowManager.getDefaultDisplay().getWidth();
		height = mWindowManager.getDefaultDisplay().getHeight();

		return "(像素)宽:" + width + "\n" + "(像素)高:" + height + "\n"
		+ "屏幕密度（0.75 / 1.0 / 1.5）:" + density + "\n"
		+ "屏幕密度DPI（120 / 160 / 240）:" + densityDpi + "\n";
		/*
		 * 下面的代码即可获取屏幕的尺寸。 在一个Activity的onCreate方法中，写入如下代码：
      DisplayMetrics metric   = new DisplayMetrics();
		 * getWindowManager().getDefaultDisplay().getMetrics(metric);
		 * int width  = metric.widthPixels; // 屏幕宽度（像素）
		 * int height = metric.heightPixels;   // 屏幕高度（像素）
		 * float density = metric.density; // 屏幕密度（0.75 / 1.0 / 1.5）
		 * int densityDpi = metric.densityDpi; // 屏幕密度DPI（120 / 160 / 240）
		 *
		 * 但是，需要注意的是，在一个低密度的小屏手机上，仅靠上面的代码是不能获取正确的尺寸的。
		 * 比如说，一部240x320像素的低密度手机，如果运行上述代码，获取到的屏幕尺寸是320x427。
		 * 因此，研究之后发现，若没有设定多分辨率支持的话
		 * ，Android系统会将240x320的低密度（120）尺寸转换为中等密度（160）对应的尺寸，
		 * 这样的话就大大影响了程序的编码。所以，需要在工程的AndroidManifest
		 * .xml文件中，加入supports-screens节点，具体的内容如下： <supports-screens
		 * android:smallScreens="true" android:normalScreens="true"
		 * android:largeScreens="true" android:resizeable="true"
		 * android:anyDensity="true" />
		 * 这样的话，当前的Android程序就支持了多种分辨率，那么就可以得到正确的物理尺寸了。
		 */
	}



	// 获取信号强度
	public static void getPhoneState(Context context) {
		// 1. 创建telephonyManager 对象。
		TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		// 2. 创建PhoneStateListener 对象
		PhoneStateListener MyPhoneListener = new PhoneStateListener() {
			@Override
			public void onCellLocationChanged(CellLocation location) {
				if (location instanceof GsmCellLocation) {// gsm网络
					int CID = ((GsmCellLocation) location).getCid();
				} else if (location instanceof CdmaCellLocation) {// 其他CDMA等网络
					int ID = ((CdmaCellLocation) location).getBaseStationId();
				}
			}

			@Override
			public void onServiceStateChanged(ServiceState serviceState) {
				super.onServiceStateChanged(serviceState);
			}

			@Override
			public void onSignalStrengthsChanged(SignalStrength signalStrength) {
				int asu = signalStrength.getGsmSignalStrength();
				int dbm = -113 + 2 * asu; // 信号强度
				super.onSignalStrengthsChanged(signalStrength);
			}
		};
		// 3. 监听信号改变
		telephonyManager.listen(MyPhoneListener,
				PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

		/*
		 * 可能需要的权限 <uses-permission
		 * android:name="android.permission.WAKE_LOCK"></uses-permission>
		 * <uses-permission
		 * android:name="android.permission.ACCESS_COARSE_LOCATION"/>
		 * <uses-permission
		 * android:name="android.permission.ACCESS_FINE_LOCATION"/>
		 * <uses-permission android:name="android.permission.READ_PHONE_STATE"
		 * /> <uses-permission
		 * android:name="android.permission.ACCESS_NETWORK_STATE" />
		 */
	}


	/*
	 * 一、Android 获取手机中已安装apk文件信息(PackageInfo、ResolveInfo)(应用图片、应用名、包名等)
	 * 1、通过PackageManager可获取手机端已安装的apk文件的信息，具体代码如下: PackageManager
	 * packageManager = this.getPackageManager(); List<PackageInfo>
	 * packageInfoList = packageManager.getInstalledPackages(0);
	 * 通过上述方法，可得到手机中安装的所有应用程序，包括手动安装的apk包的信息、、系统预装的应用软件的信息，要区分这两类软件可使用以下方法:
	 * （a）从packageInfoList获取的packageInfo
	 * ，再通过packageInfo.applicationInfo获取applicationInfo。
	 * （b）判断(applicationInfo.flags &
	 * ApplicationInfo.FLAG_SYSTEM)的值，该值大于0时，表示获取的应用为系统预装的应用，反之则为手动安装的应用。
	 * (1)获取应用的代码: public static List<PackageInfo> getAllApps(Context context) {
	 * List<PackageInfo> apps = new ArrayList<PackageInfo>(); PackageManager
	 * pManager = context.getPackageManager(); //获取手机内所有应用 List<PackageInfo>
	 * paklist = pManager.getInstalledPackages(0); for (int i = 0; i <
	 * paklist.size(); i++) { PackageInfo pak = (PackageInfo) paklist.get(i);
	 * //判断是否为非系统预装的应用程序 if ((pak.applicationInfo.flags &
	 * pak.applicationInfo.FLAG_SYSTEM) <= 0) { apps.add(pak); } } return apps;
	 * } (2)、获取图片、应用名、包名: PackageManager pManager =
	 * MessageSendActivity.this.getPackageManager(); List<PackageInfo> appList =
	 * Utils.getAllApps(MessageSendActivity.this); for(int
	 * i=0;i<appList.size();i++) { PackageInfo pinfo = appList.get(i); shareItem
	 * = new ShareItemInfo();
	 * shareItem.setIcon(pManager.getApplicationIcon(pinfo.applicationInfo));
	 * shareItem
	 * .setLabel(pManager.getApplicationLabel(pinfo.applicationInfo).toString
	 * ()); shareItem.setPackageName(pinfo.applicationInfo.packageName); }
	 * 其中ShareItemInfo 类自己写的，各位可以忽略 (3)获取支持分享的应用的代码： public static
	 * List<ResolveInfo> getShareApps(Context context){ List<ResolveInfo> mApps
	 * = new ArrayList<ResolveInfo>(); Intent intent=new
	 * Intent(Intent.ACTION_SEND,null);
	 * intent.addCategory(Intent.CATEGORY_DEFAULT);
	 * intent.setType("text/plain"); PackageManager pManager =
	 * context.getPackageManager(); mApps =
	 * pManager.queryIntentActivities(intent
	 * ,PackageManager.COMPONENT_ENABLED_STATE_DEFAULT); return mApps; }
	 * 由于该方法，返回的并不是PackageInfo 对象。而是ResolveInfo。因此获取图片、应用名、包名的方法不一样，如下：
	 * PackageManager pManager = MessageSendActivity.this.getPackageManager();
	 * List<ResolveInfo> resolveList =
	 * Utils.getShareApps(MessageSendActivity.this); for(int
	 * i=0;i<resolveList.size();i++) { ResolveInfo resolve = resolveList.get(i);
	 * ShareItemInfo shareItem = new ShareItemInfo(); //set Icon
	 * shareItem.setIcon(resolve.loadIcon(pManager)); //set Application Name
	 * shareItem.setLabel(resolve.loadLabel(pManager).toString()); //set Package
	 * Name shareItem.setPackageName(resolve.activityInfo.packageName); } 总结： 通过
	 * PackageInfo 获取具体信息方法： 包名获取方法：packageInfo.packageName
	 * icon获取获取方法：packageManager.getApplicationIcon(applicationInfo)
	 * 应用名称获取方法：packageManager.getApplicationLabel(applicationInfo)
	 * 使用权限获取方法：packageManager
	 * .getPackageInfo(packageName,PackageManager.GET_PERMISSIONS)
	 * .requestedPermissions 通过 ResolveInfo 获取具体信息方法：
	 * 包名获取方法：resolve.activityInfo.packageName
	 * icon获取获取方法：resolve.loadIcon(packageManager)
	 * 应用名称获取方法：resolve.loadLabel(packageManager).toString()
	 */
	public static String getSimCardInfo(Context context) {
		// 在manifest.xml文件中要添加
		// <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
		/*
		 * TelephonyManager类主要提供了一系列用于访问与手机通讯相关的状态和信息的get方法。其中包括手机SIM的状态和信息
		 * 、电信网络的状态及手机用户的信息。
		 * 在应用程序中可以使用这些get方法获取相关数据。TelephonyManager类的对象可以通过Context
		 * .getSystemService(Context.TELEPHONY_SERVICE)
		 * 方法来获得，需要注意的是有些通讯信息的获取对应用程序的权限有一定的限制
		 * ，在开发的时候需要为其添加相应的权限。以下列出TelephonyManager类所有方法及说明：
		 * TelephonyManager提供设备上获取通讯服务信息的入口。 应用程序可以使用这个类方法确定的电信服务商和国家
		 * 以及某些类型的用户访问信息。 应用程序也可以注册一个监听器到电话收状态的变化。不需要直接实例化这个类
		 * 使用Context.getSystemService (Context.TELEPHONY_SERVICE)来获取这个类的实例。
		 */

		// 解释：
		// IMSI是国际移动用户识别码的简称(International Mobile Subscriber Identity)
		// IMSI共有15位，其结构如下：
		// MCC+MNC+MIN
		// MCC：Mobile Country Code，移动国家码，共3位，中国为460;
		// MNC:Mobile NetworkCode，移动网络码，共2位
		// 在中国，移动的代码为电00和02，联通的代码为01，电信的代码为03
		// 合起来就是（也是Android手机中APN配置文件中的代码）：
		// 中国移动：46000 46002
		// 中国联通：46001
		// 中国电信：46003
		// 举例，一个典型的IMSI号码为460030912121001

		// IMEI是International Mobile Equipment Identity （国际移动设备标识）的简称
		// IMEI由15位数字组成的”电子串号”，它与每台手机一一对应，而且该码是全世界唯一的
		// 其组成为：
		// 1. 前6位数(TAC)是”型号核准号码”，一般代表机型
		// 2. 接着的2位数(FAC)是”最后装配号”，一般代表产地
		// 3. 之后的6位数(SNR)是”串号”，一般代表生产顺序号
		// 4. 最后1位数(SP)通常是”0″，为检验码，目前暂备用

		TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
		/*
		 * 电话状态： 1.tm.CALL_STATE_IDLE=0 无活动，无任何状态时 2.tm.CALL_STATE_RINGING=1
		 * 响铃，电话进来时 3.tm.CALL_STATE_OFFHOOK=2 摘机
		 */
		tm.getCallState();// int

		/*
		 * 电话方位：
		 */
		// 返回当前移动终端的位置
		CellLocation location = tm.getCellLocation();
		// 请求位置更新，如果更新将产生广播，接收对象为注册LISTEN_CELL_LOCATION的对象，需要的permission名称为
		// ACCESS_COARSE_LOCATION。
		location.requestLocationUpdate();

		/**
		 * 获取数据活动状态
		 *
		 * DATA_ACTIVITY_IN 数据连接状态：活动，正在接受数据 DATA_ACTIVITY_OUT 数据连接状态：活动，正在发送数据
		 * DATA_ACTIVITY_INOUT 数据连接状态：活动，正在接受和发送数据 DATA_ACTIVITY_NONE
		 * 数据连接状态：活动，但无数据发送和接受
		 */
		tm.getDataActivity();

		/**
		 * 获取数据连接状态
		 *
		 * DATA_CONNECTED 数据连接状态：已连接 DATA_CONNECTING 数据连接状态：正在连接
		 * DATA_DISCONNECTED 数据连接状态：断开 DATA_SUSPENDED 数据连接状态：暂停
		 */
		tm.getDataState();

		/**
		 * 返回当前移动终端的唯一标识，设备ID
		 *
		 * 如果是GSM网络，返回IMEI；如果是CDMA网络，返回MEID Return null if device ID is not
		 * available.
		 */
		String Imei = tm.getDeviceId();// String

		/*
		 * 返回移动终端的软件版本，例如：GSM手机的IMEI/SV码。 设备的软件版本号： 例如：the IMEI/SV(software
		 * version) for GSM phones. Return null if the software version is not
		 * available.
		 */
		tm.getDeviceSoftwareVersion();// String

		/*
		 * 手机号： GSM手机的 MSISDN. Return null if it is unavailable.
		 */
		String phoneNum = tm.getLine1Number();// String

		/*
		 * 获取ISO标准的国家码，即国际长途区号。 注意：仅当用户已在网络注册后有效。 在CDMA网络中结果也许不可靠。
		 */
		tm.getNetworkCountryIso();// String

		/*
		 * MCC+MNC(mobile country code + mobile network code) 注意：仅当用户已在网络注册时有效。
		 * 在CDMA网络中结果也许不可靠。
		 */
		tm.getNetworkOperator();// String

		/*
		 * 按照字母次序的current registered operator(当前已注册的用户)的名字 注意：仅当用户已在网络注册时有效。
		 * 在CDMA网络中结果也许不可靠。
		 */

		tm.getNetworkOperatorName();// String

		/*
		 * 当前使用的网络类型： 例如： NETWORK_TYPE_UNKNOWN 网络类型未知 0 NETWORK_TYPE_GPRS GPRS网络
		 * 1 NETWORK_TYPE_EDGE EDGE网络 2 NETWORK_TYPE_UMTS UMTS网络 3
		 * NETWORK_TYPE_HSDPA HSDPA网络 8 NETWORK_TYPE_HSUPA HSUPA网络 9
		 * NETWORK_TYPE_HSPA HSPA网络 10 NETWORK_TYPE_CDMA CDMA网络,IS95A 或 IS95B. 4
		 * NETWORK_TYPE_EVDO_0 EVDO网络, revision 0. 5 NETWORK_TYPE_EVDO_A EVDO网络,
		 * revision A. 6 NETWORK_TYPE_1xRTT 1xRTT网络 7
		 */
		tm.getNetworkType();// int

		/*
		 * 手机类型： 例如： PHONE_TYPE_NONE 无信号 PHONE_TYPE_GSM GSM信号 PHONE_TYPE_CDMA
		 * CDMA信号
		 */
		tm.getPhoneType();// int

		/*
		 * Returns the ISO country code equivalent for the SIM provider's
		 * country code. 获取ISO国家码，相当于提供SIM卡的国家码。
		 */
		tm.getSimCountryIso();// String

		/*
		 * Returns the MCC+MNC (mobile country code + mobile network code) of
		 * the provider of the SIM. 5 or 6 decimal digits.
		 * 获取SIM卡提供的移动国家码和移动网络码.5或6位的十进制数字. SIM卡的状态必须是
		 * SIM_STATE_READY(使用getSimState()判断).
		 */
		tm.getSimOperator();// String

		/*
		 * 服务商名称： 例如：中国移动、联通 SIM卡的状态必须是 SIM_STATE_READY(使用getSimState()判断).
		 */
		tm.getSimOperatorName();// String

		/*
		 * SIM卡的序列号： 需要权限：READ_PHONE_STATE
		 */
		tm.getSimSerialNumber();// String

		/*
		 * SIM的状态信息： SIM_STATE_UNKNOWN 未知状态 0 SIM_STATE_ABSENT 没插卡 1
		 * SIM_STATE_PIN_REQUIRED 锁定状态，需要用户的PIN码解锁 2 SIM_STATE_PUK_REQUIRED
		 * 锁定状态，需要用户的PUK码解锁 3 SIM_STATE_NETWORK_LOCKED 锁定状态，需要网络的PIN码解锁 4
		 * SIM_STATE_READY 就绪状态 5
		 */
		tm.getSimState();// int

		/*
		 * 唯一的用户ID： 例如：IMSI(国际移动用户识别码) for a GSM phone. 需要权限：READ_PHONE_STATE
		 */
		tm.getSubscriberId();// String

		/*
		 * 取得和语音邮件相关的标签，即为识别符 需要权限：READ_PHONE_STATE
		 */

		tm.getVoiceMailAlphaTag();// String

		/*
		 * 获取语音邮件号码： 需要权限：READ_PHONE_STATE
		 */
		tm.getVoiceMailNumber();// String

		/*
		 * ICC卡是否存在
		 */
		tm.hasIccCard();// boolean

		/*
		 * 是否漫游: (在GSM用途下)
		 */
		tm.isNetworkRoaming();//

		String ProvidersName = null;
		// 返回唯一的用户ID;就是这张卡的编号神马的
		String IMSI = tm.getSubscriberId(); // 国际移动用户识别码
		// IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
		System.out.println(IMSI);
		if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
			ProvidersName = "中国移动";
		} else if (IMSI.startsWith("46001")) {

			ProvidersName = "中国联通";

		} else if (IMSI.startsWith("46003")) {

			ProvidersName = "中国电信";

		}
		// 返回当前移动终端附近移动终端的信息
		/*
		 * 附近的电话的信息: 类型：List<NeighboringCellInfo>
		 * 需要权限：android.Manifest.permission#ACCESS_COARSE_UPDATES
		 */
		List<NeighboringCellInfo> infos = tm.getNeighboringCellInfo();
		for (NeighboringCellInfo info : infos) {
			// 获取邻居小区号
			int cid = info.getCid();
			// 获取邻居小区LAC，LAC:
			// 位置区域码。为了确定移动台的位置，每个GSM/PLMN的覆盖区都被划分成许多位置区，LAC则用于标识不同的位置区。
			info.getLac();
			info.getNetworkType();
			info.getPsc();
			// 获取邻居小区信号强度
			info.getRssi();
		}
		return "手机号码:" + phoneNum + "\n" + "服务商：" + ProvidersName+"\n" + "IMEI：" + Imei;

	}
}