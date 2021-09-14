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

public class CodeFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerView2;
    private TextView mQuestionsDisplay;
    private Callback mCallback;
    private TextView spaceText;
    private ProgressBar mProgressBar;
    private TextView spaceTextOnline;
    private TextView mTextView2;
    private static Context mContext;
    private Callback2 mCallback2;
    public List<String> questions = new ArrayList<>();
    private static final String URL = "https://genericcodeforumkai.000webhostapp.com/code_online_questions.php";
    public interface Callback {
        void onQuestionIdSelected(UUID id);
    }
    public interface Callback2 {
        void onQuestionIdSelected2(String laPregunta);
    }
    public static CodeFragment newInstance(Context context) {
        mContext = context;
        return new CodeFragment();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (Callback) context;
        mCallback2 = (Callback2)context;
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
        mCallback2 = null;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_code, container, false);
        mProgressBar = v.findViewById(R.id.code_progress_bar);
        mProgressBar.setMax(100);
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView = v.findViewById(R.id.app_recycler_view2);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView2 = v.findViewById(R.id.app_recycler_view2_1);
        mRecyclerView2.setLayoutManager(new LinearLayoutManager(getActivity()));
        setupAdapter();
        return v;
    }
    public void afterNetworkTask(List<String> questionReturned) {
        questions = questionReturned;
        mProgressBar.setVisibility(View.GONE);
    }
    public void setupAdapter() {
        QuestionLab questionLab = QuestionLab.get(getActivity());
        List<Question> questionList = questionLab.getQuestions();
        mRecyclerView.setAdapter(new ActivityAdapter(questionList));
        online_questions_list();
    }
    private class ActivityHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Question mQuestion;
        public ActivityHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_code, parent, false));
            itemView.setOnClickListener(this);
            mQuestionsDisplay = itemView.findViewById(R.id.code_question_text);
            spaceText = itemView.findViewById(R.id.space_text);
        }
        public void bind(Question question) {
            mQuestion = question;
            mQuestionsDisplay.setText(mQuestion.getQuestion());
            spaceText.setText("    ");
        }
        @Override
        public void onClick(View view) {
            mCallback.onQuestionIdSelected(mQuestion.getId());
        }
    }
    private class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder> {
        private List<Question> mQuestions;
        public ActivityAdapter(List<Question> questions) {
            mQuestions = questions;
        }
        @NonNull
        @Override
        public ActivityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflaterLayout = LayoutInflater.from(getActivity());
            return new ActivityHolder(inflaterLayout, parent);
        }
        @Override
        public void onBindViewHolder(@NonNull ActivityHolder holder, int position) {
            Question question = mQuestions.get(position);
            holder.bind(question);
            holder.setIsRecyclable(false);
        }
        @Override
        public int getItemCount() {
            return mQuestions.size();
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
    public void online_questions_list() {
        if(isNetworkAvailable()) {
            mProgressBar.setVisibility(View.VISIBLE);
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
                setupAdapter2();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(mContext, "Failed to download question. Check Connectivity.", Toast.LENGTH_SHORT).show();
            }
        }) {
        };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
    }else {
            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }}
    public void setupAdapter2() {
        mRecyclerView2.setAdapter(new ActivityAdapter2(questions));
    }
    private class ActivityHolder2 extends RecyclerView.ViewHolder implements View.OnClickListener{
        String mString;
        public ActivityHolder2(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_online_questions, parent, false));
            itemView.setOnClickListener(this);
            mTextView2 = itemView.findViewById(R.id.online_question);
            spaceTextOnline = itemView.findViewById(R.id.space_text_online);
        }
        public void bind2(String question) {
            mTextView2.setText(question);
            mString = question;
            spaceTextOnline.setText(" ");
        }
        @Override
        public void onClick(View view) {
            mCallback2.onQuestionIdSelected2(mString);
        }
    }
    private class ActivityAdapter2 extends RecyclerView.Adapter<ActivityHolder2> {
        private List<String> mQuestions;
        public ActivityAdapter2(List<String> questions) {
            mQuestions = questions;
        }
        @NonNull
        @Override
        public ActivityHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflaterLayout = LayoutInflater.from(getActivity());
            return new ActivityHolder2(inflaterLayout, parent);
        }
        @Override
        public void onBindViewHolder(@NonNull ActivityHolder2 holder, int position) {
            String question = mQuestions.get(position);
            holder.bind2(question);
            holder.setIsRecyclable(false);
        }
        @Override
        public int getItemCount() {
            return mQuestions.size();
        }
    }
}
