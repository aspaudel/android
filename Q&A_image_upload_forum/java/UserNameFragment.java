package phoenixCorp.taka;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UserNameFragment extends Fragment {

    private EditText mEditText;
    private Button mButton;
    private Button testB;
    private ProgressBar mProgressBar;
    private String userName = "";
    private static NavigationView mNavigationView;
    private static final String URL_QUESTION_DOWNLOAD = "https://genericcodeforumkai.000webhostapp.com/download_all_images.php";
    private static final String URL_IMAGES_NAMES_DOWNLOAD = "https://genericcodeforumkai.000webhostapp.com/code_online_questions.php";
    public interface nvCallback {
        void nv1();
        void nv2();
        void nv3();
    }
    nvCallback nv;
    public static UserNameFragment newInstance(NavigationView navigationView) {
        mNavigationView = navigationView;
        return new UserNameFragment();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        nv = (nvCallback)context;
    }
    @Override
    public void onDetach() {
        super.onDetach();
        nv = null;
    }
    public void ServerCleanUp() {
        final List<String> imagesNames = new ArrayList<>();
        final List<String> questionNames = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_IMAGES_NAMES_DOWNLOAD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        imagesNames.add(jsonArray.getString(i));
                    }
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_QUESTION_DOWNLOAD, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    questionNames.add(jsonArray.getString(i));
                                }
                                ResidualImages(imagesNames, questionNames);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                    MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
    }
    public void ResidualImages(List<String> imgNames, List<String> quesNames) {
        List<String> finalList = new ArrayList<>();
        for (int i = 0; i < imgNames.size(); i++) {
            for (int j = 0; j < quesNames.size(); j++) {
                if (!imgNames.get(i).contains(quesNames.get(j))) {
                    finalList.add(imgNames.get(i));
                }
            }
        }
        JSONArray jsonArray = new JSONArray(finalList);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, URL_IMAGES_NAMES_DOWNLOAD,jsonArray, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray1) {
                try {
                    Toast.makeText(getActivity(), jsonArray1.getString(0), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        MySingleton.getInstance(getContext()).addToRequestQueue(jsonArrayRequest);
    }
    private boolean isNetworkAvailable() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                return true;
            }
            return false;
        } catch(NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle) {
        View v = inflater.inflate(R.layout.user_name, container, false);
        mProgressBar = v.findViewById(R.id.userName_progress_bar);
        mProgressBar.setMax(100);
        mProgressBar.setVisibility(View.GONE);
        mEditText = v.findViewById(R.id.userName);
        mButton = v.findViewById(R.id.set_button);
        if(USPreferences.getStoredUS(getActivity()) != null) {
            mEditText.setEnabled(false);
            mButton.setEnabled(false);
            mEditText.setText(USPreferences.getStoredUS(getActivity()));
        }
        final MenuItem menuItem = MainActivity.userName;
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().length() == 0) {
                    userName = "";
                    mButton.setEnabled(false);
                } else {
                    userName = charSequence.toString();
                    mButton.setEnabled(true);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable()) {
                    mProgressBar.setVisibility(View.VISIBLE);
                String URL = "";
                    URL = "https://genericcodeforumkai.000webhostapp.com/userName_upload.php";
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String Response = jsonObject.getString("response");
                                Toast.makeText(getActivity(), Response, Toast.LENGTH_SHORT).show();
                                USPreferences.setStoredUS(getActivity(), userName);
                                menuItem.setTitle(userName);
                                mButton.setEnabled(false);
                                mProgressBar.setVisibility(View.GONE);
                                mNavigationView.getMenu().getItem(1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        nv.nv1();
                                        return true;
                                    }
                                });
                                mNavigationView.getMenu().getItem(2).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        nv.nv2();
                                        return true;
                                    }
                                });
                                mNavigationView.getMenu().getItem(3).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        nv.nv3();
                                        return true;
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("name", userName);
                            return params;
                        }
                    };
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);

                }else {
                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }}

        });
        return v;
    }
}
