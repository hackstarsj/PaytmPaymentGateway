package paytmdemo.supercoders.in.paytmpaymentgateway;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.paytm.pgsdk.PaytmClientCertificate;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button=(Button)findViewById(R.id.pay);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServerResponse();
            }
        });
    }

    public void ServerResponse(){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(com.android.volley.Request.Method.POST, "http://192.168.43.110/Php/generateChecksum.php", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //pro   gressDialog.dismiss();
                try {
                    Map<String,String> map=new HashMap<>();
                    JSONObject jsonObj = new JSONObject(response);
                    for(int i=0;i<jsonObj.length();i++){
                        map.put("MID",jsonObj.getString("MID"));
                        map.put("ORDER_ID",jsonObj.getString("ORDER_ID"));
                        map.put("CUST_ID",jsonObj.getString("CUST_ID"));
                        map.put("INDUSTRY_TYPE_ID",jsonObj.getString("INDUSTRY_TYPE_ID"));
                        map.put("CHANNEL_ID",jsonObj.getString("CHANNEL_ID"));
                        map.put("TXN_AMOUNT",jsonObj.getString("TXN_AMOUNT"));
                        map.put("WEBSITE",jsonObj.getString("WEBSITE"));
                        map.put("CALLBACK_URL",jsonObj.getString("CALLBACK_URL"));
                        map.put("EMAIL",jsonObj.getString("EMAIL"));
                        map.put("MOBILE_NO",jsonObj.getString("MOBILE_NO"));
                        map.put("CHECKSUMHASH",jsonObj.getString("CHECKSUMHASH"));
                        PaytmPay(map);
                    }



                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, "Failed To Parse Response", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                Log.d("Response", response);

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Errors", String.valueOf(error));
            }
        });
        queue.add(sr);
        sr.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void PaytmPay(Map<String,String> paramMap) {
        PaytmPGService Service = null;
        Service = PaytmPGService.getStagingService();
        PaytmOrder Order = new PaytmOrder((HashMap<String, String>) paramMap);
        Service.initialize(Order, null);
        Service.startPaymentTransaction(this, true, true, new PaytmPaymentTransactionCallback() {

            @Override
            public void someUIErrorOccurred(String inErrorMessage) {
                Log.d("LOG", "UI Error Occur.");
                Toast.makeText(getApplicationContext(), " UI Error Occur. ", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTransactionResponse(Bundle inResponse) {
                Log.d("LOG", "Payment Transaction : " + inResponse);
                Toast.makeText(getApplicationContext(), "Payment Transaction response "+inResponse.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void networkNotAvailable() {
                Log.d("LOG", "UI Error Occur.");
                Toast.makeText(getApplicationContext(), " UI Error Occur. ", Toast.LENGTH_LONG).show();
            }

            @Override
            public void clientAuthenticationFailed(String inErrorMessage) {
                Log.d("LOG", "UI Error Occur.");
                Toast.makeText(getApplicationContext(), " Severside Error "+ inErrorMessage, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onErrorLoadingWebPage(int iniErrorCode,
                                              String inErrorMessage, String inFailingUrl) {
                Log.d("LOG",inErrorMessage);
            }
            @Override
            public void onBackPressedCancelTransaction() {
                Log.d("LOG","Back");
// TODO Auto-generated method stub
            }

            @Override
            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                Log.d("LOG", "Payment Transaction Failed " + inErrorMessage);
                Toast.makeText(getBaseContext(), "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
            }

        });

    }


}
