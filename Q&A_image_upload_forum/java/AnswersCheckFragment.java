package phoenixCorp.taka;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AnswersCheckFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private TextView mQuestionText;
    private List<String> mQuestionsOnline;
    private AnswerCallback mAnswerCallback;
    private ProgressBar mProgressBar;
    private static final String URL = "https://genericcodeforumkai.000webhostapp.com/code_online_questions.php";
    public interface AnswerCallback {
        void onQuestionIdSelectedAnswers(String id);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAnswerCallback = (AnswerCallback) context;
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mAnswerCallback = null;
    }

    public static AnswersCheckFragment newInstance() {
        return new AnswersCheckFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_answers_check, container, false);
        mProgressBar = v.findViewById(R.id.fragment_check_progress_bar);
        mProgressBar.setMax(100);
        mRecyclerView = v.findViewById(R.id.app_recycler_view4);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        downloadQuestions();
        return v;
    }
    private void afterNetworkTask(List<String> questions) {
        mQuestionsOnline = questions;
        mProgressBar.setVisibility(View.GONE);
        if(mQuestionsOnline.size() == 0) {
            Toast.makeText(getContext(), "No Uploads Yet", Toast.LENGTH_SHORT).show();
        }else {
            setupAdapter();
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
    public void downloadQuestions() {
        if(isNetworkAvailable()) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                List<String> questionNames = new ArrayList<>();
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for(int i = 0; i < jsonArray.length(); i++) {
                            questionNames.add(jsonArray.getString(i));
                        }
                        afterNetworkTask(questionNames);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(getContext(), "Failed to download question. Check Connectivity.", Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.GONE);
                }
            }) {
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
        }else {
            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }}
    private void setupAdapter() {
        mRecyclerView.setAdapter(new ActivityAdapter(mQuestionsOnline));
    }
    private class ActivityHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private String pQuestion;
        public ActivityHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_answers, parent, false));
            itemView.setOnClickListener(this);
            mQuestionText = itemView.findViewById(R.id.answer_question_text);
        }
        public void bind(String question) {
            mQuestionText.setText(question);
            pQuestion = question;
        }
        @Override
        public void onClick(View v) {
            mAnswerCallback.onQuestionIdSelectedAnswers(pQuestion);
        }
    }
    private class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder> {
        private List<String> mQuestions;
        public ActivityAdapter(List<String> questions) {
            mQuestions = questions;
        }
        @NonNull
        @Override
        public ActivityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new ActivityHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ActivityHolder holder, int position) {
            String question = mQuestions.get(position);
            holder.bind(question);
            holder.setIsRecyclable(false);
        }

        @Override
        public int getItemCount() {
            return mQuestions.size();
        }
    }
}
