package phoenixCorp.taka;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CFSAFragment extends Fragment {
    private TextView mQuestionText;
    private File mPhotoFile;
    private ImageButton mPhotoButton;
    private ImageView mImageView;
    private UUID mId;
    private File mFile;
    private File[] mFiles;
    private List<File> mFFiles = new ArrayList<>();
    private static final String ARG_QUESTION_ID = "questionId";
    private static final int REQUEST_PHOTO = 0;
    public static CFSAFragment newInstance(UUID quesitonId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_QUESTION_ID, quesitonId);
        CFSAFragment CFSA = new CFSAFragment();
        CFSA.setArguments(args);
        return CFSA;
    }
    @Override
    public void onResume() {
        super.onResume();
        updatePhotoView();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cfsa_fragment, container, false);
        mPhotoButton = view.findViewById(R.id.cfsa_imageButton);
        mImageView = view.findViewById(R.id.cfsa_imageView);
        mQuestionText = view.findViewById(R.id.cfsa_textView);
        final UUID questionId = (UUID)getArguments().getSerializable(ARG_QUESTION_ID);
        mId = questionId;
        final Question mQuestion = QuestionLab.get(getActivity()).getQuestion(questionId);
        mQuestionText.setText(mQuestion.getQuestion());
        PackageManager packageManager = getActivity().getPackageManager();
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int n = mQuestion.getI();
                mPhotoFile = QuestionLab.get(getActivity()).getPhotoFile(mQuestion, n);
                Uri uri = FileProvider.getUriForFile(getActivity(), "phoenixCorp.taka.fileprovider", mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                List<ResolveInfo> cameraActivities = getActivity().getPackageManager().queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);
                for(ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });
        updatePhotoView();
        return view;
    }
    private void updatePhotoView() {
        mFFiles.clear();
        mFile = getContext().getFilesDir();
        mFiles = mFile.listFiles();
        for(int i = 0;i < mFiles.length;i++) {
            String name = mFiles[i].getName();
            if(name.contains(mId.toString())) {
                mFFiles.add(mFiles[i]);
            }
        }
        if(mFFiles.size() == 0) {
            mImageView.setImageDrawable(getResources().getDrawable(R.mipmap.text));
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(),R.string.no_image,Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mFFiles.get(0).getPath(), getActivity());
            mImageView.setImageBitmap(bitmap);
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = ImageViewActivity.newIntent(getContext(), mId);
                    startActivity(i);
                }
            });
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) {

        }
        if(requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(), "phoenixCorp.taka.fileprovider", mPhotoFile);
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
        }
    }
}
