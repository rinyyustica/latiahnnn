package unhas.informatics.monitoringapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import unhas.informatics.monitoringapp.Model.User;
import unhas.informatics.monitoringapp.Preference.SharedPrefManager;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.input_username)
    EditText _username;
    @BindView(R.id.input_password)
    EditText _password;
    @BindView(R.id.btn_login)
    Button _loginButton;

    SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        sharedPrefManager = new SharedPrefManager(getApplicationContext());

        if (isLogin()) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }


        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                requestLogin();
            }
        });

    }
    private boolean isLogin() {
        return sharedPrefManager.getPrefBoolean("login_status");
    }

    private void requestLogin() {
        if (!validate()) {
            return;
        }

        _loginButton.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Mengautentikasi...");
        progressDialog.show();

        String username = _username.getText().toString();
        String password = _password.getText().toString();

        ApiEndpoint apiService = ApiClient.getClient().create(ApiEndpoint.class);
        Call<User> call = apiService.requestLogin(username, password);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                try {
                    int success = response.body().getSuccess();
                    if (success == 1) {
                        onLoginSuccess();
                        sharedPrefManager.setPrefBoolean("login_status", true);
                        progressDialog.dismiss();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    } else {
                        onLoginFailed();
                        progressDialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onLoginFailed();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

    private void onLoginSuccess() {
        Toast.makeText(getBaseContext(), "Login berhasil", Toast.LENGTH_SHORT).show();
        _loginButton.setEnabled(true);
    }

    private void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login gagal", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    private boolean validate() {
        boolean valid = true;

        String username = _username.getText().toString();
        String password = _password.getText().toString();

        if (username.isEmpty()) {
            _username.setError("Username harus diisi");
            valid = false;
        } else {
            _username.setError(null);
        }

        if (password.isEmpty()) {
            _password.setError("Password harus diisi");
            valid = false;
        } else {
            _username.setError(null);
        }

        return valid;
    }
}
