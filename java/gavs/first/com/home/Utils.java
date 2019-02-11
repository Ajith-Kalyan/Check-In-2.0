package gavs.first.com.home;

public class Utils {

    //Local server at training room
    //public static final String host_ip = "http://192.168.1.147:8081";

    //Central server
    public static final String host_ip = "http://10.0.100.223:8081";

    public static final String server_ip = "http://10.0.100.225:8089";
    public static final String test_server_ip = "http://10.0.100.225:8081";

    // CustomDialogFragment
    public static final String url_item_claim = host_ip+"/gavs/api/LostFound/ItemClaim";

    // CustomDialogFragment // HomeActivity
    public static final String url_authorize = host_ip+"/gavs/api/employee/authorize";

    // ClaimFragment
    public static final String url_itemdetails_claim = host_ip+"/gavs/api/lostitem/details";

    //Medicine_Activity
    public static final String url_medicine = host_ip+"/gavs/api/Medicine/data";

    //pm_Activity
    public static final String url_pmActivity = host_ip+"/gavs/api/women/employee";

    //Receive_Fragment
    public static final String url_courier_receive = host_ip+"/gavs/api/Courier/Receive";

    //Report_Fragment
    public static final String url_lost_report = host_ip+"/gavs/api/LostFound/Item";

    //Scanner_Class
   // public static final String url_barcode = server_ip+"/api/AssetScan/GetAssetDtlsBasedonBarcode?strAssetID=";
    public static final String url_barcode = host_ip+"/gavs/api/scan/assetId/";

    // Send_Fragment
    public static final String url_courier_send = host_ip+"/gavs/api/Courier/Send";

    // Umbrella_Activity
    public static final String url_umbrella = host_ip+"/gavs/api/Umbrella/data";

    //weekend attendance
    public static final String url_weekend_attendance = host_ip+"/gavs/api/Weekend/Checkin";
}
