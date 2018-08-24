package com.example.meek.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private TextView messageView;
    private Button addButton;
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("MainActivity", "afterTextChanged s=" + s.toString());
                messageView.setText("Hello, " + s.toString());
            }
        });
        messageView = findViewById(R.id.messageView);
        messageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchData();
            }
        });
        addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData();
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        jsonArray = new JSONArray();
        mAdapter = new MyAdapter(jsonArray);
        mRecyclerView.setAdapter(mAdapter);

        // Restore state.
        if (savedInstanceState != null) {
            editText.setText(savedInstanceState.getString("editText"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("editText", editText.getText().toString());
    }

    @Override
    protected void onResume() {
        super.onResume();

        ShareCompat.IntentReader intentReader = ShareCompat.IntentReader.from(this);
        if (intentReader.isShareIntent()) {
            editText.setText(intentReader.getText());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_menu_item:
                fetchData();
                return true;
            case R.id.share_menu_item:
                shareText();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareText() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(editText.getText().toString())
                .getIntent();
        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(shareIntent);
        }
    }

    private void fetchData() {
        messageView.setText("Fetching...");

        final RequestQueue queue = Volley.newRequestQueue(this);
        final String urlString = "https://meekimobile-node.herokuapp.com/api/contacts";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsons = new JSONArray(response);
                            messageView.setText("Total Contacts = " + jsons.length());
                            mAdapter.setJsonArray(jsons);
                            mAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            messageView.setText("Error!");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        messageView.setText("Error!");
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void sendData() {
        if (editText.getText().toString().length() == 0) {
            messageView.setText("Please input a name.");
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("name", editText.getText().toString());
        } catch (JSONException e) {
            return;
        }

        addButton.setEnabled(false);

        final RequestQueue queue = Volley.newRequestQueue(this);
        final String urlString = "https://meekimobile-node.herokuapp.com/api/contacts";

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, urlString, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        addButton.setEnabled(true);
                        try {
                            messageView.setText("Added Contact = " + response.optString("name"));
                        } catch (Exception e) {
                            messageView.setText("Error!");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        addButton.setEnabled(true);
                        try {
                            String body = new String(error.networkResponse.data, "utf-8");
                            JSONObject json = new JSONObject(body);
                            messageView.setText("Error: " + json.toString());
                        } catch (Exception e) {
                            messageView.setText("Error!");
                        }
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(req);
    }
}
