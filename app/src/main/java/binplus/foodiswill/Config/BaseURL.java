package binplus.foodiswill.Config;


public class BaseURL {
    static final String APP_NAME = "FoodWill ";
    public static final String PREFS_NAME = "GroceryLoginPrefs";
    public static final String PREFS_NAME2 = "GroceryLoginPrefs2";
    public static final String IS_LOGIN = "isLogin";
    public static final String KEY_NAME = "user_fullname";
    public static final String KEY_EMAIL = "user_email";
    public static final String TOTAL_AMOUNT = "TOTAL_AMOUNT";
    public static final String WALLET_TOTAL_AMOUNT = "WALLET_TOTAL_AMOUNT";
    public static final String COUPON_TOTAL_AMOUNT = "COUPON_TOTAL_AMOUNT";
    public static final String KEY_ID = "user_id";
    public static final String KEY_MOBILE = "user_phone";
    public static final String KEY_IMAGE = "user_image";
    public static final String KEY_WALLET_Ammount = "wallet_ammount";
    public static final String KEY_REWARDS_POINTS = "rewards_points";
    public static final String KEY_PAYMENT_METHOD = "payment_method";
    public static final String KEY_PINCODE = "pincode";
    public static final String KEY_SOCITY_ID = "Socity_id";
    public static final String KEY_USER_CITY_ID = "user_city_id";
    public static final String KEY_USER_CITY = "user_city";

    public static final String KEY_REWARDS = "rewards";
    public static final String KEY_SOCITY_NAME = "socity_name";
    public static final String KEY_HOUSE = "house_no";
    public static final String KEY_DATE = "date";
    public static final String KEY_TIME = "time";
    public static final String KEY_CNT = "img_count";
    public static final String KEY_CAT = "category_id";
    public static final String IS_CITY = "is_city";
    public static final String CITY_ID = "city_id";
    public static final String CITY_NAME = "city_name";
    //Store Selection

    public static final String KEY_STORE_COUNT = "STORE_COUNT";
    public static final String KEY_NOTIFICATION_COUNT = "NOTIFICATION_COUNT";

    //Firebase
    public static final String SHARED_PREF = "ah_firebase";
    public static final String TOPIC_GLOBAL = "global";
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public  static  final  int  REQUEST_TIMEOUT=10000 ;
    public static final String KEY_PASSWORD = "password";

    //City and Store Id

    public static final String STORE_ID = "STORE_ID";

//    public static String BASE_URL = "https://www.myjewelstuff.in/";
    public static String BASE_URL = "https://www.test.myjewelstuff.in/";
    public static String BASE_URL2 = "apis/Api1/";

//    public static String BASE_URL = "https://www.myjewelstuff.in/mobile_register_otp_verification";


    public static String IMG_SLIDER_URL = BASE_URL +  "uploads/sliders/";
    public static String IMG_CATEGORY_URL = BASE_URL + "uploads/category/";
    public static String IMG_PRODUCT_URL = BASE_URL + "uploads/products/";
    public static String IMG_EXTRA_URL = BASE_URL + "uploads/order_notification/";

    public static String IMG_PROFILE_URL = BASE_URL + "uploads/profile/";
    public static String IMG_CITY_URL = BASE_URL +  "uploads/city_admin/";
    public static String GET_SLIDER_URL = BASE_URL + BASE_URL2 + "get_sliders";
    public static String GET_FEAATURED_SLIDER_URL = BASE_URL + BASE_URL2 + "get_feature_banner";
    public static String GET_BANNER_URL = BASE_URL + BASE_URL2 + "get_banner";

    public static String WALLET_REFRESH = BASE_URL + BASE_URL2 + "wallet?user_id=";
    public static String REWARDS_REFRESH = BASE_URL + BASE_URL2 + "rewards?user_id=";



    public static String PUT_SUGGESTION_URL = BASE_URL + BASE_URL2 + "put_suggestion";
    public static String GET_CATEGORY_URL = BASE_URL + BASE_URL2 + "get_categories";
    public static String GET_ALL_CATEGORY_URL = BASE_URL + BASE_URL2 + "get_all_categories";
    public static String GET_SLIDER_CATEGORY_URL = BASE_URL + BASE_URL2 + "get_sub_cat";
    public static String GET_CATEGORY_ICON_URL = BASE_URL + BASE_URL2 + "icon";
    public static String COUPON_CODE = BASE_URL + BASE_URL2 + "get_coupons";
    public static String GET_LINKS = BASE_URL + BASE_URL2 + "links";
    public static String URL_SEND_OTP = BASE_URL + BASE_URL2 + "login_with_otp";
    public static String URL_REG_OTP = BASE_URL + BASE_URL2 + "register_with_otp";

    //Home PAGE


    public static String GET_TOP_SELLING_PRODUCTS = BASE_URL + BASE_URL2 + "top_selling_product";
    public static String GET_DEAL_PRODUCTS = BASE_URL + BASE_URL2 + "getDealProducts";
    public static String GET_NEW_PRODUCTS = BASE_URL + BASE_URL2 + "new_product";
    public static String GET_DEAL_OF_THE_DAY =BASE_URL + BASE_URL2 + "get_deal_products";
    public static String GET_ALL_TOP_SELLING_PRODUCTS = BASE_URL + BASE_URL2 + "get_all_top_selling_product";


    public static String GET_PRODUCT_URL = BASE_URL + BASE_URL2 + "get_products";
    

    public static String GET_PRODUCT_DESC = BASE_URL + BASE_URL2 + "get_products_high";
    public static String GET_PRODUCT_ASC = BASE_URL + BASE_URL2 + "get_products_low";
    public static String GET_PRODUCT_NEWEST = BASE_URL + BASE_URL2 + "get_products_new";



    public static String GET_ABOUT_URL = BASE_URL + BASE_URL2 + "aboutus";

    public static String GET_SUPPORT_URL = BASE_URL + BASE_URL2 + "support";

    public static String GET_TERMS_URL = BASE_URL + BASE_URL2 + "terms";
    public static String GET_PRIVACY_URL = BASE_URL + BASE_URL2 + "privacy_policy";
    public static String GET_RETURN_POLICY_URL = BASE_URL + BASE_URL2 + "return_policy";

    public static String GET_TIME_SLOT_URL = BASE_URL + BASE_URL2 + "get_time_slt";

    public static String LOGIN_URL = BASE_URL + BASE_URL2 + "login";

    public static String REGISTER_URL = BASE_URL + BASE_URL2 + "signup";

    public static String GET_SOCITY_URL = BASE_URL + BASE_URL2 + "get_society";

    public static String EDIT_PROFILE_URL = BASE_URL + BASE_URL2 + "update_userdata";
    public static String WALLET_AMOUNT_URL = BASE_URL + BASE_URL2 + "wallet_amount";

    public static String ADD_ORDER_URL = BASE_URL + BASE_URL2 + "send_order";
    public static String Wallet_CHECKOUT = BASE_URL + BASE_URL2 + "wallet_at_checkout";
    public static String GET_ORDER_URL = BASE_URL + BASE_URL2 + "my_orders";

    public static String GET_DELIVERD_ORDER_URL = BASE_URL + BASE_URL2 + "delivered_complete";

    public static String ORDER_DETAIL_URL = BASE_URL + BASE_URL2 + "order_details";

    public static String DELETE_ORDER_URL = BASE_URL + BASE_URL2 + "cancel_order";

    public static String GET_LIMITE_SETTING_URL = BASE_URL + BASE_URL2 + "get_limit_settings";

    public static String ADD_ADDRESS_URL = BASE_URL + BASE_URL2 + "add_address";

    public static String GET_ADDRESS_URL = BASE_URL + BASE_URL2 + "get_address";

    public static String FORGOT_URL = BASE_URL + BASE_URL2 + "forgot_password";

    public static String JSON_RIGISTER_FCM = BASE_URL + BASE_URL2 + "register_fcm";

    public static String CHANGE_PASSWORD_URL = BASE_URL + BASE_URL2 + "change_password";

    public static String DELETE_ADDRESS_URL = BASE_URL + BASE_URL2 + "delete_address";

    public static String EDIT_ADDRESS_URL = BASE_URL + BASE_URL2 + "edit_address";


    // global topic to receive app wide push notifications

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";

    public static final String PUSH_NOTIFICATION = "pushNotification";

    // product details

    public static String PRODUCT_DETAILS = BASE_URL + BASE_URL2 + "get_products_details";
    public static String GET_UPLOAD = BASE_URL + BASE_URL2 + "upload_images";
    public static String GET_CANCEL_ORDERS = BASE_URL + BASE_URL2 + "my_cancel_orders";
    
    
    public static String URL_VERIFY_OTP = BASE_URL + BASE_URL2 + "mobile_otp_verification";
    public static String URL_VERIFY_REGISTER_OTP = BASE_URL + BASE_URL2 + "mobile_register_otp_verification";
    public static String URL_CANCEL_ORDER_DETAILS = BASE_URL + BASE_URL2 + "order_cancel_details";
    public static String URL_GET_COUPON = BASE_URL + BASE_URL2 + "get_coupon";
    public static String URL_GENERATE_PDF = BASE_URL + BASE_URL2 + "pdf_details";
    public static String URL_MASTER_SEARCH = BASE_URL + BASE_URL2 + "master_search";
    public static String GET_USER_STATUS = BASE_URL + BASE_URL2 + "get_user_status";
    public static String GET_GATEWAY_SETTING = BASE_URL + BASE_URL2 + "gateway_setting";


    //Payment Gateway urls

    public static String PAY_REDIRECT_URL = "https://www.myjewelstuff.in/merchant/ccavResponseHandler.php";

    public static String PAY_CANCEL_URL = "https://www.myjewelstuff.in/merchant/ccavResponseHandler.php";

    public static String PAY_RSA_URL = "https://www.myjewelstuff.in/merchant/GetRSA.php";
  
    //new apis
    public static String GET_CITY_URL = BASE_URL + BASE_URL2  +"city";
    public static String GET_VERSTION_DATA = BASE_URL + BASE_URL2  +"getVersionData";
    public static String URL_REGISTER_OTP = BASE_URL + BASE_URL2  +"fun_register_with_otp";
    public static String URL_GEN_OTP = BASE_URL + BASE_URL2  +"fun_gen_otp";
}
