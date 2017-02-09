package com.exarcplus.sys.horti.Question_answer;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.exarcplus.sys.horti.Home_activity.MainActivity;
import com.exarcplus.sys.horti.News_detailes_activity;
import com.exarcplus.sys.horti.R;
import com.exarcplus.sys.horti.Register_one;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import Adapters.Chat_Question_list_adapter;
import Adapters.Chat_main_list_adpter;


import Array_modules.Question_list;
import connection.SessionManager;
import imageCropPackage.CropActivity;
import materialEditext.MaterialEditText;

import static com.exarcplus.sys.horti.R.id.emptyElement;
import static com.facebook.FacebookSdk.getApplicationContext;
import static connection.MyFileContentProvider.CONTENT_URI;

public class Chat_second_list extends AppCompatActivity {

    RequestQueue requestQueue;
    ArrayList<Question_list> ChatListArrayList = new ArrayList<>();
    Chat_Question_list_adapter chat_Question_list_adapter;
    GridView gridView;

    MaterialEditText questions_ask_id;
    LinearLayout attach_click,click_attach_hide,pic_image_id,back_button_id,send_question_click,click_video;
    ImageView attach_click_iamge;
    private boolean attach_fecility = false;
    TextView crop_name_id;
    String attach="no",crop_name="",crop_id="",user_id="";
    public static final int CAMERA_PIC_REQUEST = 7452;
    public static final int GALLERY_PIC_REQ = 54454;
    private static final int SELECT_VIDEO =21123;
    private String mImagePath;
    private Uri mImageUri = null;

    public static final String UPLOAD_URL= "http://adventure4us.com/hortiapp/upload_question.php";
    private int serverResponseCode;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.chat_second_screen);


        gridView = (GridView)findViewById(R.id.listview);
       TextView textView=(TextView)findViewById(emptyElement);
       textView.setText("No Questions!");
        gridView.setEmptyView(textView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.main_color));
        }

        Intent bundle=getIntent();
        crop_name=bundle.getStringExtra("crop_name");
        crop_id=bundle.getStringExtra("crop_id");

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        user_id = user.get(SessionManager.KEY_USERID);

       questions_ask_id=(MaterialEditText)findViewById(R.id.questions_ask_id);
       attach_click=(LinearLayout)findViewById(R.id.attach_click);
       click_attach_hide=(LinearLayout) findViewById(R.id.click_attach);
        click_video=(LinearLayout) findViewById(R.id.click_video);
       pic_image_id=(LinearLayout) findViewById(R.id.pic_image_id);
       attach_click_iamge=(ImageView)findViewById(R.id.attach_click_iamge);
        back_button_id=(LinearLayout)findViewById(R.id.back_button_id);
        send_question_click=(LinearLayout)findViewById(R.id.send_question_click);
        crop_name_id=(TextView) findViewById(R.id.crop_name_id);
        crop_name_id.setText(crop_name);

        questions_ask_id.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                questions_ask_id.setFocusableInTouchMode(true);
                return false;
            }
        });

        attach_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!attach_fecility)
                {
                    attach = "yes";
                    attach_fecility = true;
                    attach_click_iamge.setImageResource(R.drawable.close);
                    click_attach_hide.setVisibility(View.VISIBLE);

                    InputMethodManager inputManager = (InputMethodManager)Chat_second_list.this.
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(
                            attach_click.getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);


                }
                else
                {
                    InputMethodManager inputManager = (InputMethodManager)Chat_second_list.this
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(
                            attach_click.getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    attach_fecility = false;
                    attach = "no";
                    attach_click_iamge.setImageResource(R.drawable.add);
                    click_attach_hide.setVisibility(View.GONE);
                }
            }
        });

        pic_image_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                selectImage();
            }
        });

        click_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                selectvideo();
            }
        });


        send_question_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                InputMethodManager inputManager = (InputMethodManager)Chat_second_list.this
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(
                        send_question_click.getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);
                questions_ask_id.setFocusable(false);

                if (questions_ask_id.getText().toString().trim().isEmpty() )
                {
                    questions_ask_id.setError("Question is Empty");


                }
                else
                {
                     String url = "http://adventure4us.com/hortiapp/json/post_question.php?user_id="+user_id+"&crop_id="+crop_id+"&question="+questions_ask_id.getText().toString()+"&image=&video=";
                     post_question(url);
                }


            }
        });

        back_button_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
                overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
                InputMethodManager inputManager = (InputMethodManager)Chat_second_list.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(
                        attach_click.getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);

            }
        });

        getlist();

    }



    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery","Cancel"};

        createTempFile();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {

                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    try {
                        Uri mImageCaptureUri = null;
                        String state = Environment.getExternalStorageState();
                        if (Environment.MEDIA_MOUNTED.equals(state)) {
                            mImageCaptureUri = Uri.fromFile(mFileTemp);
                        } else
                        {

                            mImageCaptureUri = CONTENT_URI;
                        }
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                        takePictureIntent.putExtra("return-data", true);
                        startActivityForResult(takePictureIntent, CAMERA_PIC_REQUEST);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(Chat_second_list.this, "Can't take picture", Toast.LENGTH_LONG).show();
                    }

                    /*Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);*/

                } else if (options[item].equals("Choose from Gallery"))
                {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), GALLERY_PIC_REQ);

                    /*Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhotoIntent, GALLERY_PIC_REQ);*/
                }
                else if (options[item].equals("Video"))
                {

                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, SELECT_VIDEO);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }

    private void selectvideo() {
        final CharSequence[] options = {"Choose from Gallery", "Take Video","Cancel"};

        createTempFile();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Video!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Choose from Gallery")) {
                    try {

                        Intent intent = new Intent();
                        intent.setType("video/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select a Video "), SELECT_VIDEO);

                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(Chat_second_list.this, "Can't take picture", Toast.LENGTH_LONG).show();
                    }


                }
                else if (options[item].equals("Take Video"))
                {
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, SELECT_VIDEO);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        final String imageSelectStatus = "";
        if (requestCode == CAMERA_PIC_REQUEST && resultCode ==RESULT_OK) {

            mImagePath = mFileTemp.getPath();
            mImageUri = Uri.fromFile(new File(mImagePath));

            Log.e("ImageUri","-->"+mImageUri);

            startCrop(mImageUri);


        } else if (requestCode == GALLERY_PIC_REQ && resultCode ==RESULT_OK) {
            if (data != null)
            {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData()); // Got the bitmap .. Copy it to the temp file for cropping
                    FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
                    copyStream(inputStream, fileOutputStream);
                    fileOutputStream.close();
                    inputStream.close();
                } catch (Exception e) {
                    errored();
                }

                mImagePath = mFileTemp.getPath();
                mImageUri = Uri.fromFile(new File(mImagePath));
                String filePath =mImageUri.toString();

                Log.e("ImageUri","-->"+mImageUri);

                startCrop(mImageUri);

            }
         } else if (requestCode == SELECT_VIDEO  && resultCode ==RESULT_OK)
          {
              Uri selectedImageUri = data.getData();
              String selectedPath = getPath(selectedImageUri);
               uploadVideo(selectedPath);
          }
    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();

        return path;
    }

    private void createTempFile()
    {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(),"dummy.jpg");
        } else {
            mFileTemp = new File(getFilesDir(), "dummy.jpg");
        }
    }
    private static void copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }


    public void errored() {
        Toast.makeText(this,"Try again",Toast.LENGTH_SHORT).show();
    }

    private void startCrop(Uri imageUri){

        Log.e("imageUri",""+imageUri);
        Intent i = new Intent(Chat_second_list.this, CropActivity.class);
        String typeId=imageUri.toString();
        i.putExtra("typeId", typeId);

    }
    File mFileTemp;


    public void getlist() {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        requestQueue = Volley.newRequestQueue(this);
        String logUrl = "http://adventure4us.com/hortiapp/json/question_list.php?user_id="+user_id+"&crop_id="+crop_id;
        Log.e("logUrl", "" + logUrl);
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, logUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ChatListArrayList.clear();

                        try {

                            Log.e("", "" + response);

                            JSONArray ja = response.getJSONArray("result");

                            for (int i = 0; i < ja.length(); i++) {

                                JSONObject jsonObject = ja.getJSONObject(i);
                                String question_id = jsonObject.getString("question_id");
                                String question = jsonObject.getString("question");
                                String image = jsonObject.getString("image");
                                String video = jsonObject.getString("video");
                                String by = jsonObject.getString("by");
                                String date = jsonObject.getString("date");

                                Log.e("category_english", "-->" + question);

                                Question_list array_newsFeed = new Question_list(question_id, question, image, video,by,date);
                                ChatListArrayList.add(array_newsFeed);
                            }

                            Log.e("ARRAYSIZE", "-->" + ChatListArrayList.size());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        pDialog.dismiss();

                        chat_Question_list_adapter = new Chat_Question_list_adapter(Chat_second_list.this, ChatListArrayList);
                        gridView.setAdapter(chat_Question_list_adapter);

                           gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View v, int i, long id) {


                             /*   SessionManager session = new SessionManager(getApplicationContext());
                                HashMap<String, String> user = session.getUserDetails();
                                String lang = user.get(SessionManager.KEY_select_lang);

                                Intent i1 = new Intent(getActivity(), News_detailes_activity.class);
                                i1.putExtra("image", NewsListArrayList.get(i).getImage());
                                i1.putExtra("date", NewsListArrayList.get(i).getDate());
                                i1.putExtra("time", NewsListArrayList.get(i).getTime());
                                i1.putExtra("video", NewsListArrayList.get(i).getVideo());

                                if (lang != null) {
                                    if (lang.contains("Kannada")) {
                                        i1.putExtra("title", NewsListArrayList.get(i).getTitle_in_kannada());
                                        i1.putExtra("content", NewsListArrayList.get(i).getContent_in_kannada());
                                    }

                                    if (lang.contains("English")) {
                                        i1.putExtra("title", NewsListArrayList.get(i).getTitle_in_english());
                                        i1.putExtra("content", NewsListArrayList.get(i).getContent_in_english());
                                    }

                                } else {
                                    i1.putExtra("title", NewsListArrayList.get(i).getTitle_in_english());
                                    i1.putExtra("content", NewsListArrayList.get(i).getContent_in_english());
                                }

                                startActivity(i1);
*/


                                  Intent i1 = new Intent(Chat_second_list.this, Answer_list.class);
                                   i1.putExtra("question_id", ChatListArrayList.get(i).getQuestion_id());
                                   i1.putExtra("question", ChatListArrayList.get(i).getQuestion());
                                   i1.putExtra("crop_name", crop_name);
                                   startActivity(i1);

                            }
                        });
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        Toast.makeText(Chat_second_list.this, "Opps! Some error occured", Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    }
                }
        );

        requestQueue.add(jor);

    }


    public void post_question(String url) {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        requestQueue = Volley.newRequestQueue(this);

        Log.e("logUrl", "" + url);
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ChatListArrayList.clear();

                        try {

                            Log.e("", "" + response);

                                JSONArray ja = response.getJSONArray("result");
                                JSONObject jsonObject = ja.getJSONObject(0);
                                String message = jsonObject.getString("message");

                                Log.e("message", "-->" + message);


                            if (message.equalsIgnoreCase("success"))
                            {
                                Toast.makeText(Chat_second_list.this, "sucess", Toast.LENGTH_SHORT).show();
                                questions_ask_id.setText("");

                                 AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Chat_second_list.this);
                                 alertDialogBuilder.setTitle("Approval!");

                                // set dialog message
                                  alertDialogBuilder
                                        .setMessage("Your Question is been posted. Please wait for SME Approval ")
                                        .setCancelable(false)
                                        .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id)
                                            {
                                                dialog.cancel();

                                            }
                                        });


                                AlertDialog alertDialog = alertDialogBuilder.create();

                                alertDialog.show();


                            }

                            if (message.equalsIgnoreCase("values empty"))
                            {
                                Toast.makeText(Chat_second_list.this, "values empty", Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        pDialog.dismiss();


                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        Toast.makeText(Chat_second_list.this, "Opps! Some error occured", Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    }
                }
        );

        requestQueue.add(jor);

    }


    public String uploadVideo(String file) {

        String fileName = file;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        File sourceFile = new File(file);
        if (!sourceFile.isFile()) {
            Log.e("Huzza", "Source File Does not exist");
            return null;
        }

        try {
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(UPLOAD_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("myFile", fileName);
            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"myFile\";filename=\"" + fileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            Log.i("Huzza", "Initial .available : " + bytesAvailable);

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            serverResponseCode = conn.getResponseCode();

            fileInputStream.close();
            dos.flush();
            dos.close();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (serverResponseCode == 200) {
            StringBuilder sb = new StringBuilder();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn
                        .getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
            } catch (IOException ioex) {
            }
            return sb.toString();
        }else {
            return "Could not upload";
        }
    }
}