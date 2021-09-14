package phoenixCorp.taka;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class QuestionsUploadFragment extends Fragment {
    private RecyclerView mRecyclerView;
    ;
    private TextView mTextView;
    private TextView mId;
    private Question mQuestion;
    private Button mButton;
    private TextView mSTextView;
    private EditText mQuestionName;
    private ImageButton mDeleteButton;
    private ImageButton mUploadButton;
    private static final int REQUEST_CODE = 0;
    private static final String URL = "https://genericcodeforumkai.000webhostapp.com/question_upload.php";
    String temp = "";
    private boolean editTextPressed = false;
    private ProgressBar mProgressBar;

    public static QuestionsUploadFragment newInstance() {
        return new QuestionsUploadFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_questions_upload, container, false);
        mProgressBar = v.findViewById(R.id.fragment_question_progress_bar);
        mProgressBar.setMax(100);
        mSTextView = v.findViewById(R.id.special_character_text);
        mSTextView.setText("Please do not use any of the following characters:\n( / \\ : *  ?  < >  | )");
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView = v.findViewById(R.id.app_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mQuestionName = v.findViewById(R.id.question_name);
        mButton = v.findViewById(R.id.set_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextPressed == true) {
                    mQuestion = new Question();
                    QuestionLab questionLab = QuestionLab.get(getActivity());
                    mQuestion.setQuestion(temp);
                    questionLab.addQuestion(mQuestion);
                    setupAdapter();
                } else {
                    Toast.makeText(getActivity(), "Nothing Typed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mQuestionName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                temp = charSequence.toString();
                editTextPressed = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        setupAdapter();
        return v;
    }

    public void setupAdapter() {
        QuestionLab questionLab = QuestionLab.get(getActivity());
        List<Question> questions = questionLab.getQuestions();
        mRecyclerView.setAdapter(new ActivityAdapter(questions));

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
    private class ActivityHolder extends RecyclerView.ViewHolder {
        private Question mQuestion;

        public ActivityHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_questions_upload, parent, false));
            mTextView = itemView.findViewById(R.id.question_text);
            mId = itemView.findViewById(R.id.id_text);
            mDeleteButton = itemView.findViewById(R.id.question_delete);
            mUploadButton = itemView.findViewById(R.id.upload_button);
        }

        public void bind(final Question question) {
            mQuestion = question;
            mTextView.setText(mQuestion.getQuestion());
            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*int position = getAdapterPosition();
                    Toast.makeText(getActivity(), Integer.toString(position), Toast.LENGTH_SHORT).show();
                    */FragmentManager manager = getFragmentManager();
                    QuestionDeleteDialog dialog = QuestionDeleteDialog.newInstance(mQuestion, getActivity());
                    dialog.setTargetFragment(QuestionsUploadFragment.this, REQUEST_CODE);
                    dialog.show(manager, "Dialog");
                }
            });
            mUploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isNetworkAvailable()) {
                        mProgressBar.setVisibility(View.VISIBLE);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String Response = jsonObject.getString("response");
                                Toast.makeText(getActivity(), Response, Toast.LENGTH_SHORT).show();
                                mProgressBar.setVisibility(View.GONE);
                                mUploadButton.setEnabled(false);

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
                            params.put("name", mQuestion.getQuestion());
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
        }
    }

    private class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder> {
        private List<Question> mQuestions;

        public ActivityAdapter(List<Question> questions) {
            mQuestions = questions;
        }

        @Override
        public ActivityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ActivityHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ActivityHolder holder, int position) {
            Question question = mQuestions.get(position);
            holder.bind(question);
            holder.setIsRecyclable(false);
        }

        @Override
        public int getItemCount() {
            return mQuestions.size();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE) {
            setupAdapter();
        }
    }
}
