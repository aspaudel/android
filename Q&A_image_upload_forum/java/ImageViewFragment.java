package phoenixCorp.taka;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ImageViewFragment extends Fragment {
    private static final String QUESTION_ID = "photofilename";
    private RecyclerView mRecyclerView;
    private ImageView mImageView;
    private UUID questionId;
    private File mFile;
    private File[] mFiles;
    private ImageButton dButton;
    private ImageButton uploadButton;
    private ProgressBar mProgressBar;
    private Bitmap tBitmap;
    private List<File> mFFiles = new ArrayList<>();
    private static final String URL = "https://genericcodeforumkai.000webhostapp.com/login.php";
    private static final String URL_DELETE = "https://genericcodeforumkai.000webhostapp.com/delete_images2.php";

    public static ImageViewFragment newInstance(UUID QuestionId) {
        Bundle args = new Bundle();
        args.putSerializable(QUESTION_ID, QuestionId);
        ImageViewFragment IVF = new ImageViewFragment();
        IVF.setArguments(args);
        return IVF;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_view, container, false);
        mRecyclerView = v.findViewById(R.id.app_recycler_view3);
        mProgressBar = v.findViewById(R.id.image_view_progress_bar);
        mProgressBar.setMax(100);
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        questionId = (UUID) getArguments().getSerializable(QUESTION_ID);
        mFile = getContext().getFilesDir();
        mFiles = mFile.listFiles();
        tBitmap = PictureUtils.getScaledBitmap(mFiles[0].getPath(), getActivity());
        for (int i = 0; i < mFiles.length; i++) {
            String name = mFiles[i].getName();
            if (name.contains(questionId.toString())) {
                mFFiles.add(mFiles[i]);
            }
        }
        setupAdapter();
        return v;
    }

    public void setupAdapter() {
        mRecyclerView.setAdapter(new ActivityAdapter(mFFiles));
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
        private File mFile;

        public ActivityHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_image_view, parent, false));
            dButton = itemView.findViewById(R.id.delete_photo);
            mImageView = itemView.findViewById(R.id.image_view);
            uploadButton = itemView.findViewById(R.id.upload_button);
        }

        public void bind(File file) {
            mFile = file;
            final Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mFile.getPath(), getActivity());
            mImageView.setImageBitmap(bitmap);
            dButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isNetworkAvailable()) {
                        mProgressBar.setVisibility(View.VISIBLE);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DELETE, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                QuestionLab.get(getActivity()).deletePhotoFile(mFile);
                                for (int i = 0; i < mFFiles.size(); i++) {
                                    if (mFFiles.get(i).getName().equals(mFile.getName())) {
                                        mFFiles.remove(i);
                                        setupAdapter();
                                        break;
                                    }
                                }
                                JSONObject jsonObject = new JSONObject(response);
                                String Response = jsonObject.getString("response");
                                Toast.makeText(getActivity(), Response, Toast.LENGTH_SHORT).show();
                                mProgressBar.setVisibility(View.GONE);
                                setupAdapter();
                            }catch(JSONException e) {
                                e.printStackTrace();
                                mProgressBar.setVisibility(View.GONE);
                            } catch(Exception e) {
                                e.printStackTrace();
                                mProgressBar.setVisibility(View.GONE);
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
                            params.put("image_name", USPreferences.getStoredUS(getActivity()) + mFile.getName());
                            return params;
                        }
                    };
                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
                }else {
                        QuestionLab.get(getActivity()).deletePhotoFile(mFile);
                        for (int i = 0; i < mFFiles.size(); i++) {
                            if (mFFiles.get(i).getName().equals(mFile.getName())) {
                                mFFiles.remove(i);
                                break;
                            }
                        }
                        setupAdapter();
                }}
            });
            uploadButton.setOnClickListener(new View.OnClickListener() {
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
                            } catch (JSONException e) {
                                e.printStackTrace();
                                mProgressBar.setVisibility(View.GONE);
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
                            params.put("name", USPreferences.getStoredUS(getContext()) + mFile.getName());
                            params.put("image", ImageToString(bitmap));
                            return params;

                        }
                    };
                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
                }else {
                    Toast.makeText(getActivity(), "Internet Connection Required", Toast.LENGTH_SHORT).show();
                    }
            }});
        }
    }

    private class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder> {
        List<File> mFFFiles;

        ActivityAdapter(List<File> files) {
            mFFFiles = files;
        }

        @NonNull
        @Override
        public ActivityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ActivityHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ActivityHolder holder, int position) {
            File file = mFFFiles.get(position);
            holder.bind(file);
            holder.setIsRecyclable(false);
        }

        @Override
        public int getItemCount() {
            return mFFFiles.size();
        }
    }

    private String ImageToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}
