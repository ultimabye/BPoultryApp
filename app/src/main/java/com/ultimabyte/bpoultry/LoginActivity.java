package com.ultimabyte.bpoultry;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ultimabyte.bpoultry.api.AccessToken;
import com.ultimabyte.bpoultry.api.Authentication;
import com.ultimabyte.bpoultry.api.BPoultryApi;
import com.ultimabyte.bpoultry.api.RestService;
import com.ultimabyte.bpoultry.data.User;
import com.ultimabyte.bpoultry.databinding.ActivityLoginBinding;
import com.ultimabyte.bpoultry.utils.ErrorUtils;
import com.ultimabyte.bpoultry.utils.Logger;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * log tag.
     */
    private static final String TAG = LoginActivity.class.getSimpleName();

    public static final String KEY_STARTUP_ERROR = "KEY_STARTUP_ERROR";

    private Animation mShakeAnimation;
    private ActivityLoginBinding mBinding;
    private String mStartupError;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        if (getIntent() != null) {
            mStartupError = getIntent().getStringExtra(KEY_STARTUP_ERROR);
        }
        // Give the blurring view a reference to the blurred view.
        //mBinding.blurringView.setBlurredView(mBinding.blurredView);

        this.disableLoginButton();

        //set click listeners
        mBinding.viewPasswordButton.setOnClickListener(this);
        mBinding.loginButton.setOnClickListener(this);
        //add text change listener on user name field.
        mBinding.inputUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }


            /**
             * Called when text in input field in changed
             */
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mBinding.inputPassword.length() > 0 && mBinding.inputUserName.length() > 0) {
                    LoginActivity.this.enableLoginButton();
                } else {
                    LoginActivity.this.disableLoginButton();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //add text change listener on password field.
        mBinding.inputPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            /**
             * Called when text in input field in changed
             */
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mBinding.inputPassword.length() > 0 &&
                        mBinding.inputUserName.length() > 0) {
                    LoginActivity.this.enableLoginButton();
                } else {
                    LoginActivity.this.disableLoginButton();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mShakeAnimation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.shake_slow);
        KeyboardVisibilityEvent.setEventListener(this, isOpen -> mBinding.setIsShowingKeyboard(isOpen));
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mStartupError != null && !mStartupError.isEmpty()) {
            mBinding.setError(mStartupError);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    public static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.viewPasswordButton) {
            int inputType = mBinding.inputPassword.getInputType();
            if (inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                //clear password input button clicked, reset text in password field.
                mBinding.inputPassword.setInputType(
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                mBinding.inputPassword.setSelection(mBinding.inputPassword.length());
                mBinding.viewPasswordButton.setImageResource(R.drawable.ic_view_password);
            } else {
                //clear password input button clicked, reset text in password field.
                mBinding.inputPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                mBinding.inputPassword.setSelection(mBinding.inputPassword.length());
                mBinding.viewPasswordButton.setImageResource(R.drawable.ic_hide_password);
            }
        } else if (view.getId() == R.id.loginButton) {
            //login button clicked.
            //hide soft keyboard
            hideSoftKeyboard();
            //init login call
            login();
        }
    }


    private void disableLoginButton() {
        GradientDrawable backgroundDrawable = (GradientDrawable)
                mBinding.loginButton.getBackground().getCurrent();
        backgroundDrawable.setColor(
                ContextCompat.getColor(this, R.color.login_button_disabled)
        );
    }


    private void enableLoginButton() {
        GradientDrawable backgroundDrawable = (GradientDrawable)
                mBinding.loginButton.getBackground().getCurrent();
        backgroundDrawable.setColor(
                ContextCompat.getColor(this, R.color.login_button_enabled)
        );
    }


    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager)
                    getSystemService(INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(
                        getCurrentFocus().getWindowToken(), 0);
            }
        }
    }


    /**
     * Animates the login input fields and button to let the user know that something is wrong with the input.
     * There is also a toast displayed in the situation so the animation just makes the better user experience.
     */
    private void animateScreen() {
        mBinding.inputContainer.startAnimation(mShakeAnimation);
    }


    /**
     *
     */
    private void login() {
        //check if user name input is empty.
        if (mBinding.inputUserName.length() == 0) {
            //display error message.
            showError(getString(R.string.enter_user_name));
            animateScreen();
            return;
        }
        //check if user entered a valid email address.
        if (mBinding.inputUserName.getText() == null) {
            //indicate error
            showError(getString(R.string.invalid_email));
            animateScreen();
            return;
        }


        //check if password input is empty.
        if (mBinding.inputPassword.length() == 0) {
            //display error message.
            showError(getString(R.string.enter_password));
            animateScreen();
            //early return.
            return;
        }


        if (mBinding.inputPassword.getText() == null) {
            //indicate error
            showError(getString(R.string.enter_password));
            animateScreen();
            return;
        }

        //hide input container
        hideInputContainer();

        //get user email
        final String email = mBinding.inputUserName.getText().toString();

        //get user password
        String password = mBinding.inputPassword.getText().toString();

        //enqueue the api call passing in the username and password.
        RestService.getAuthenticationService().getNewAccessToken(email, password)
                .enqueue(new Callback<AccessToken>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<AccessToken> call,
                            @NonNull Response<AccessToken> response
                    ) {
                        if (isDestroyed()) {
                            return;
                        }
                        //check http response code.
                        if (response.isSuccessful()) {
                            //if response is 200, the call succeeded.
                            //get access token from response.
                            AccessToken token = response.body();
                            if (token != null) {
                                handleTokenResponse(token, email);
                            }
                        } else {
                            int responseCode = response.code();
                            //api call failed for some reason, update UI to display an error message.
                            if (responseCode == HTTP_BAD_REQUEST || responseCode == HTTP_UNAUTHORIZED) {
                                tryAgain(getString(R.string.invalid_user_name_password));
                            } else {
                                tryAgain(ErrorUtils.getErrorMessageSafely(
                                        ErrorUtils.getErrorBody(response, TAG), TAG));
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<AccessToken> call, @NonNull Throwable t) {
                        if (isDestroyed()) {
                            return;
                        }
                        //api call failed for some reason, update UI to display an error message.
                        tryAgain(ErrorUtils.getErrorMessage(LoginActivity.this, t));
                    }
                });
    }


    protected void fetchUser() {
        RestService.getAPI(this).user().enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull
            Response<User> response) {
                Logger.d(TAG, response.toString());
                if (isDestroyed()) {
                    return;
                }

                if (!response.isSuccessful()) {
                    tryAgain(getString(R.string.unknown_error));
                    return;
                }

                User userResponse = response.body();
                if (userResponse == null) {
                    tryAgain(getString(R.string.unknown_error));
                    return;
                }
                startApp(userResponse);
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                t.printStackTrace();
                tryAgain(getString(R.string.unknown_error));
            }
        });
    }


    private void handleTokenResponse(AccessToken token, String email) {
        //save access token to SharedPreferences
        AppSettings.saveAccessToken(LoginActivity.this, token);
        AppSettings.saveUserEmail(LoginActivity.this, email);

        RestService.invalidate();

        fetchUser();
    }


    private void startApp(User user) {
        AppSettings.saveUserId(this, user.id);
        //launch account chooser.
        Intent accountChooser = new Intent(LoginActivity.this, MainActivity.class);
        accountChooser.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(accountChooser);

        //close this screen.
        finish();
    }


    /**
     * Hides input layout container and display a progress view container.
     */
    private void hideInputContainer() {
        mBinding.setIsLoading(true);
        mBinding.errorTV.setVisibility(View.GONE);
    }


    /**
     * Hides progress view container and displays input layout container.
     */
    private void showInputContainer() {
        mBinding.setIsLoading(false);
    }


    /**
     * Displays a {@link android.widget.Toast} and resets UI to it's original state.
     *
     * @param msg text to display in {@link android.widget.Toast}
     */
    private void tryAgain(String msg) {
        showError(msg);
        showInputContainer();
    }


    private void showError(String msg) {
        mBinding.setError("Failed to login, error: " + msg);
    }
}
