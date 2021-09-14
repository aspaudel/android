package phoenixCorp.taka;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class ZoomImage extends Fragment {
    private static final String URL = "url";
    private Bitmap mBitmap;
    private String url;
    private ProgressBar mProgressBar;
    private SubsamplingScaleImageView mImageView;
    public static ZoomImage newInstance(String url) {
        Bundle args = new Bundle();
        args.putSerializable(URL, url);
        ZoomImage zoomImage = new ZoomImage();
        zoomImage.setArguments(args);
        return zoomImage;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle) {
        View v = inflater.inflate(R.layout.image_zoom, container, false);
        mProgressBar = v.findViewById(R.id.image_zoom_progress_bar);
        mProgressBar.setMax(100);
        mImageView = v.findViewById(R.id.zoom_image);
        url = (String)getArguments().getSerializable(URL);
        Picasso.get().load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                mBitmap = bitmap;
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Toast.makeText(getContext(), "Failed to download Image. Check Internet Connection", Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
        mImageView.setImage(ImageSource.bitmap(mBitmap));
        return v;
    }
}
