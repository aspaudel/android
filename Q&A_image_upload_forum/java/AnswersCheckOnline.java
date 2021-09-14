package phoenixCorp.taka;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okio.Utf8;

public class AnswersCheckOnline extends Fragment {

    private RecyclerView parentRView;
    private RecyclerView childRView;
    private TextView mTextView;
    private TextView textViewOnline;
    private String mQuestion;
    private ProgressBar mProgressBar;
    private static final String ARG_QUESTION = "questionId";
    private static final String URL1 = "https://genericcodeforumkai.000webhostapp.com/php_download_images_name.php";
    private static final String URL2 = "https://genericcodeforumkai.000webhostapp.com/users_name_fetch.php";
    private List<String> mBitmapImages = new ArrayList<>();
    private List<String> mUsers = new ArrayList<>();
    public static AnswersCheckOnline newInstance(String question) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_QUESTION, question);
        AnswersCheckOnline ACO = new AnswersCheckOnline();
        ACO.setArguments(args);
        return ACO;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_answer_online, container, false);
        mProgressBar =  v.findViewById(R.id.answer_online_progress_bar);
        mProgressBar.setMax(100);
        parentRView = v.findViewById(R.id.app_recycler_view_parent);
        parentRView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTextView = v.findViewById(R.id.online_question_text);
        mQuestion = (String)getArguments().getSerializable(ARG_QUESTION);
        mTextView.setText(mQuestion);
        downloadImagesNames();
        return v;
    }
    public void afterNetworkTask1(List<String> bitmapImages) {
        mBitmapImages = bitmapImages;
        if(mBitmapImages.size() == 0) {
            Toast.makeText(getContext(), "No Uploads yet", Toast.LENGTH_SHORT).show();
            mProgressBar.setVisibility(View.GONE);
        }
    }
    public void afterNetworkTask2(List<String> users) {
        mUsers = users;
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
    class downloadIN extends AsyncTask<String, String, String> {
        List<String> images = new ArrayList<>();
        @Override
        protected String doInBackground(String... strings) {
            if(isNetworkAvailable()) {
            String response = "";
            try {
                URL url = new URL(URL1);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("&");
                stringBuilder.append(URLEncoder.encode("question", "UTF-8"));
                stringBuilder.append("=");
                stringBuilder.append(URLEncoder.encode(mQuestion, "UTF-8"));
                bufferedWriter.write(stringBuilder.toString());
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    response = bufferedReader.readLine();
                } else {
                    response = "Error Registering";
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }else {
                return null;
            }}
        @Override
        protected void onPostExecute(String result) {
            if(result == null) {
                Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            } else {
            try {
                JSONArray jsonArray = new JSONArray(result);
                for(int i = 0; i < jsonArray.length(); i ++) {
                    images.add(jsonArray.getString(i));
                }
                afterNetworkTask1(images);
                setupAdapter();
            } catch(JSONException e) {
                e.printStackTrace();
                mProgressBar.setVisibility(View.GONE);
            }
        }}
    }
    public void downloadImagesNames() {
        if(isNetworkAvailable()) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL2, new Response.Listener<String>() {
            List<String> userNames = new ArrayList<>();
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for(int i = 0; i < jsonArray.length(); i++) {
                        userNames.add(jsonArray.getString(i));
                    }
                    afterNetworkTask2(userNames);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new downloadIN().execute();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
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
        boolean userSort = false;
        List<String> newUsers = new ArrayList<>();
        for(int i = 0; i < mUsers.size(); i++) {
            for(int j = 0; j < mBitmapImages.size(); j++) {
                if(mBitmapImages.get(j).contains(mUsers.get(i))) {
                    userSort = true;
                }
            }
            if(userSort) {
                newUsers.add(mUsers.get(i));
            }
            userSort = false;
        }
        parentRView.setAdapter(new ActivityAdapter(newUsers));
    }
    private class ActivityHolder extends RecyclerView.ViewHolder {
        List<String> newUsers;
        public ActivityHolder(LayoutInflater inflater, ViewGroup parent, List<String> nUsers) {
            super(inflater.inflate(R.layout.list_online_layout, parent, false));
            textViewOnline = itemView.findViewById(R.id.textViewOnline);
            childRView = itemView.findViewById(R.id.app_recycler_view_child);
            newUsers = nUsers;
        }

        public void bind(int position) {
            List<String> newBitmapImages = new ArrayList<>();
            for(int i = 0 ; i < mBitmapImages.size(); i++) {
                if (mBitmapImages.get(i).contains(newUsers.get(position))) {
                    newBitmapImages.add(mBitmapImages.get(i));
                }
            }
            textViewOnline.setText(newUsers.get(position));
            childRView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
            childRView.setAdapter(new ActivityAdapter2(newBitmapImages));
        }
    }
    private class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder> {
        List<String> users;
        public ActivityAdapter(List<String> pUsers) {
            users = pUsers;
        }

        @NonNull
        @Override
        public ActivityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new ActivityHolder(inflater, parent, users);
        }

        @Override
        public void onBindViewHolder(@NonNull ActivityHolder holder, int position) {
            holder.bind(position);
            holder.setIsRecyclable(false);
        }

        @Override
        public int getItemCount() {
            return users.size();
        }
    }
    private class ActivityHolder2 extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView mImageView;
        private String urlImage;
        public ActivityHolder2(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_online_images, parent, false));
            mImageView = itemView.findViewById(R.id.online_image);
        }
        public void bind2(String imageName) {
            urlImage = "https://genericcodeforumkai.000webhostapp.com/images/" + imageName;
            Picasso.get().load(urlImage).into(mImageView);
            mProgressBar.setVisibility(View.GONE);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            Intent i = ZoomActivity.newIntent(getActivity(), urlImage);
            startActivity(i);
        }
    }
    private class ActivityAdapter2 extends RecyclerView.Adapter<ActivityHolder2> {
        List<String> imagesName;
        public ActivityAdapter2(List<String> pImgNames) {
            imagesName = pImgNames;
        }
        @NonNull
        @Override
        public ActivityHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new ActivityHolder2(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ActivityHolder2 holder, int position) {
            holder.bind2(imagesName.get(position));
            holder.setIsRecyclable(false);
        }

        @Override
        public int getItemCount() {
            return imagesName.size();
        }
    }
}
