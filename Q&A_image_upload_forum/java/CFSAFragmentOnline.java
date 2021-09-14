package phoenixCorp.taka;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CFSAFragmentOnline extends Fragment {

    private TextView mTextView;
    private RecyclerView mRecyclerView;
    private ImageView mImageView;
    private TextView uploadPrompt;
    private String question;
    private Toolbar mToolbar;
    public File mFile;
    private ImageButton deleteButton;
    private static ContentResolver mContentResolver;
    List<String> mImagenames = new ArrayList<>();
    private static final String FILE_NAME = "temp_saved_file_for_server_upload";
    private static final String ARG_QUESTION = "question";
    private static final String URL = "https://genericcodeforumkai.000webhostapp.com/php_download_images_name.php";
    private static final String URL_UPLOAD = "https://genericcodeforumkai.000webhostapp.com/login.php";
    private static final String URL_DELETE = "https://genericcodeforumkai.000webhostapp.com/delete_images.php";
    private static final int RequestPermissionCode = 0;
    private static final int REQUEST_CODE = 1;
    private ProgressBar mProgressBar;

    public static CFSAFragmentOnline newInstance(String question, ContentResolver contentResolver) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_QUESTION, question);
        mContentResolver = contentResolver;
        CFSAFragmentOnline CFSA = new CFSAFragmentOnline();
        CFSA.setArguments(args);
        return CFSA;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.online_cfsa, container, false);
        mProgressBar = v.findViewById(R.id.online_cfsa_progress_bar);
        mProgressBar.setMax(100);
        mProgressBar.setVisibility(View.GONE);
        mTextView = v.findViewById(R.id.code_online_question_text);
        mToolbar = v.findViewById(R.id.cfsa_toolbar);
        mToolbar.inflateMenu(R.menu.server_image_add);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add_more_images:
                        addImages();
                        return true;
                }
                return false;
            }
        });
        uploadPrompt = v.findViewById(R.id.upload_textView);
        uploadPrompt.setVisibility(View.INVISIBLE);
        mRecyclerView = v.findViewById(R.id.app_recycler_view5);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        question = (String) getArguments().getSerializable(ARG_QUESTION);
        mTextView.setText(question);
        mFile = new File(getContext().getFilesDir(), FILE_NAME);
        uploadFetch();
        return v;
    }

    public void setupAdapter() {
        mRecyclerView.setAdapter(new ActivityAdapter(mImagenames));
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
        public ActivityHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.cfsa_online_imageview, parent, false));
            mImageView = itemView.findViewById(R.id.online_cfsa_image_view);
            deleteButton = itemView.findViewById(R.id.online_delete_image);
        }

        public void bind(final String imgName) {
            String uri = "https://genericcodeforumkai.000webhostapp.com/images/" + imgName;
            Picasso.get().load(uri).into(mImageView);
            mProgressBar.setVisibility(View.GONE);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isNetworkAvailable()) {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DELETE, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String Response = jsonObject.getString("response");
                                Toast.makeText(getActivity(), Response, Toast.LENGTH_SHORT).show();
                                uploadFetch();
                            } catch (JSONException e) {
                                Toast.makeText(getActivity(), "Failed to delete the image", Toast.LENGTH_SHORT).show();
                                mProgressBar.setVisibility(View.GONE);
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
                            params.put("image_name", imgName);
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
        private List<String> imgNames;

        public ActivityAdapter(List<String> imageNames) {
            imgNames = imageNames;
        }

        @NonNull
        @Override
        public ActivityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ActivityHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ActivityHolder holder, int position) {
            String imgName = imgNames.get(position);
            holder.bind(imgName);
            holder.setIsRecyclable(false);
        }

        @Override
        public int getItemCount() {
            return imgNames.size();
        }
    }

    public void uploadFetch() {
        if(isNetworkAvailable()) {
            mProgressBar.setVisibility(View.VISIBLE);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    List<String> imageNames = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        imageNames.add(jsonArray.getString(i));
                    }
                    ImageNameSort(imageNames);
                } catch (Exception e) {
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
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("question", question);
                return params;
        }};
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
    }else {
          Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }}

    public void ImageNameSort(List<String> images) {
        mImagenames.clear();
        String userName = USPreferences.getStoredUS(getActivity());
        for (int i = 0; i < images.size(); i++) {
            if (images.get(i).contains(userName)) {
                mImagenames.add(images.get(i));
            }
        }
        if (mImagenames.size() == 0) {
            uploadPrompt.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            setupAdapter();
        } else {
            uploadPrompt.setVisibility(View.INVISIBLE);
            setupAdapter();
        }
    }

    public void addImages() {
        Uri uri = FileProvider.getUriForFile(getActivity(),"phoenixCorp.taka.fileprovider", mFile);
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        List<ResolveInfo> cameraActivities = getActivity().getPackageManager().queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
        for(ResolveInfo activity : cameraActivities) {
            getActivity().grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                try {
                    Uri uri = FileProvider.getUriForFile(getActivity(), "phoenixCorp.taka.fileprovider", mFile);
                    getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    Bitmap bitmap1 = PictureUtils.getScaledBitmap(mFile.getPath(), getActivity());
                    UploadBitmapToServer(bitmap1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void UploadBitmapToServer(final Bitmap bitmap) {
        mProgressBar.setVisibility(View.VISIBLE);
        if(isNetworkAvailable()) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPLOAD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String Response = jsonObject.getString("response");
                    Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                    uploadFetch();
                } catch (Exception e) {
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
                int random_int1 = (int)(Math.random() * (10000000 - 1 + 1) + 1);
                int random_int2 = (int)(Math.random() * (100 - 1 + 1) + 1);
                Map<String, String> params = new HashMap<>();
                params.put("name", USPreferences.getStoredUS(getActivity()) + question + +random_int1 + random_int2);
                params.put("image", ImageToString(bitmap));
                return params;
            }
        };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
    }else {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        }
    private String ImageToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}
