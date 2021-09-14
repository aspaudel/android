package phoenixCorp.taka;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QuestionDeleteDialog extends DialogFragment {
    private Button mCancel;
    private Button mDelete;
    private ProgressBar mProgressBar;
    private static Question Question;
    private static Activity mActivity;
    private static boolean condition = true;
    private static final String URLD = "https://genericcodeforumkai.000webhostapp.com/question_delete.php";

    public static QuestionDeleteDialog newInstance(Question mQuestion, Activity activity) {
        Bundle args = new Bundle();
        Question = mQuestion;
        mActivity = activity;
        QuestionDeleteDialog fragment = new QuestionDeleteDialog();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.delete_or_cancel, null);
        mCancel = v.findViewById(R.id.cancel_button);
        mProgressBar = v.findViewById(R.id.delete_dialog_progress_bar);
        mProgressBar.setMax(100);
        mProgressBar.setVisibility(View.GONE);
        mDelete = v.findViewById(R.id.delete_button);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        checkQuestionPresent();
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable()) {
                    mProgressBar.setVisibility(View.VISIBLE);
                String toast = "";
                try {
                    if (condition && isNetworkAvailable()) {
                        sqlDelete();
                    } else {
                        if(condition) {
                            toast = "Internet Connection Required";
                        } else {
                            toast = "Delete all your Personal Uploads first";
                            mProgressBar.setVisibility(View.GONE);
                        }
                        Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();
                    }
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                }
        });
        return new AlertDialog.Builder(getActivity()).setView(v).create();
    }

    public void checkQuestionPresent() {
        File File = getContext().getFilesDir();
        File[] Files = File.listFiles();
        for(int i = 0; i < Files.length; i++) {
            if(Files[i].getName().contains(Question.getId().toString())) {
                condition = false;
            }
        }
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
    private void sendResult(int resultCode) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    final private void sqlDelete() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String Response = jsonObject.getString("response");
                    Toast.makeText(mActivity, Response, Toast.LENGTH_SHORT).show();
                    QuestionLab questionLab = QuestionLab.get(getActivity());
                    questionLab.deleteQuestion(Question);
                    sendResult(Activity.RESULT_OK);
                    dismiss();
                    mProgressBar.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", Question.getQuestion());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
    }
}
