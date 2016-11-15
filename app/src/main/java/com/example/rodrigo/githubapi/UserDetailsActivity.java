package com.example.rodrigo.githubapi;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserDetailsActivity extends AppCompatActivity {

    String user_url;
    ImageView avatar;
    TextView username, stars, followers;
    Button back_button;

    ProgressDialog dialog;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        user_url = getIntent().getStringExtra("user_url");

        setComponents();

        setListeners();

        MySingleton.getInstance(this).addToRequestQueue(new JsonObjectRequest(Request.Method.GET, user_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    user = new User(response.getString("login"), response.getString("url"));

                    user.setFollowers(response.getInt("followers"));
                    user.setAvatar(response.getString("avatar_url"));

                    username.setText(user.getLogin());
                    followers.setText("Followers: " + user.getFollowers());

                    makeAvatarRequest();
                    makeStarsRequest();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }));
    }

    private void makeStarsRequest(){

        String url = Constants.USER + user.getLogin() + Constants.STARRED;

        JsonArrayRequest arrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                stars.setText("Stars: " + response.length());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(UserDetailsActivity.this, "Some problem occured.", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });

        MySingleton.getInstance(UserDetailsActivity.this).addToRequestQueue(arrayRequest);
    }

    private void makeAvatarRequest(){

        ImageRequest avatarRequest = new ImageRequest(user.getAvatar(), new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {

                avatar.setImageBitmap(response);

                dialog.cancel();
            }
        }, 0, 0, null,
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(UserDetailsActivity.this, "Some problem occured.", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });

        MySingleton.getInstance(UserDetailsActivity.this).addToRequestQueue(avatarRequest);
    }

    private void setComponents(){

        avatar = (ImageView) findViewById(R.id.avatar);
        username = (TextView) findViewById(R.id.username);
        stars = (TextView) findViewById(R.id.stars);
        followers = (TextView) findViewById(R.id.followers);
        back_button = (Button) findViewById(R.id.back_button);

        dialog = ProgressDialog.show(this, "Please wait", "Loading...", true, false);
    }

    private void setListeners(){

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
