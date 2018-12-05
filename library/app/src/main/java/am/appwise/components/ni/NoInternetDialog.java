package am.appwise.components.ni;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DimenRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Random;

/**
 * Created by robertlevonyan on 11/20/17.
 */

public class NoInternetDialog extends Dialog implements View.OnClickListener, ConnectionListener, ConnectionCallback {
    public static final int GRADIENT_LINEAR = 0;
    public static final int GRADIENT_RADIAL = 1;
    public static final int GRADIENT_SWEEP = 2;
    public static final float NO_RADIUS = -1f;
    private static final float RADIUS = 12f;
    private static final float GHOST_X_ANIMATION_VALUE = 320f;
    private static final float GHOST_Y_ANIMATION_VALUE = -100f;
    private static final float GHOST_SCALE_ANIMATION_VALUE = 1.3f;
    private static final long ANIMATION_DURATION = 1500;
    private static final long ANIMATION_DELAY = 800;
    private static final float FLIGHT_THERE_START = -200f;
    private static final float FLIGHT_THERE_END = 1300f;
    private static final float FLIGHT_BACK_START = 1000f;
    private static final float FLIGHT_BACK_END = -400f;
    private static final long FLIGHT_DURATION = 2500;

    @Retention(RetentionPolicy.RUNTIME)
    @IntDef({ORIENTATION_TOP_BOTTOM,
            ORIENTATION_BOTTOM_TOP,
            ORIENTATION_RIGHT_LEFT,
            ORIENTATION_LEFT_RIGHT,
            ORIENTATION_BL_TR,
            ORIENTATION_TR_BL,
            ORIENTATION_BR_TL,
            ORIENTATION_TL_BR})
    @interface Orientation {
    }

    public static final int ORIENTATION_TOP_BOTTOM = 10;
    public static final int ORIENTATION_BOTTOM_TOP = 11;
    public static final int ORIENTATION_RIGHT_LEFT = 12;
    public static final int ORIENTATION_LEFT_RIGHT = 13;
    public static final int ORIENTATION_BL_TR = 14;
    public static final int ORIENTATION_TR_BL = 15;
    public static final int ORIENTATION_BR_TL = 16;
    public static final int ORIENTATION_TL_BR = 17;

    private Guideline topGuide;
    private FrameLayout root;
    private AppCompatImageView close;
    private AppCompatImageView plane;
    private AppCompatImageView moon;
    private AppCompatImageView ghost;
    private AppCompatImageView tomb;
    private AppCompatImageView ground;
    private AppCompatImageView pumpkin;
    private AppCompatImageView wifiIndicator;
    private AppCompatTextView noInternet;
    private AppCompatTextView noInternetBody;
    private AppCompatTextView turnOn;
    private AppCompatButton wifiOn;
    private AppCompatButton mobileOn;
    private AppCompatButton airplaneOff;
    private ProgressBar wifiLoading;

    private int bgGradientStart;
    private int bgGradientCenter;
    private int bgGradientEnd;
    private int bgGradientOrientation;
    private int bgGradientType;
    private float dialogRadius;
    private Typeface titleTypeface;
    private Typeface messageTypeface;
    private int buttonColor;
    private int buttonTextColor;
    private int buttonIconsColor;
    private int wifiLoaderColor;
    private boolean cancelable;

    private boolean isHalloween;
    private boolean isWifiOn;
    private int direction;
    private WifiReceiver wifiReceiver;
    private NetworkStatusReceiver networkStatusReceiver;
    private ObjectAnimator wifiAnimator;
    private ConnectionCallback connectionCallback;

    private NoInternetDialog(@NonNull Context context, int bgGradientStart, int bgGradientCenter, int bgGradientEnd,
                             int bgGradientOrientation, int bgGradientType, float dialogRadius,
                             @Nullable Typeface titleTypeface, @Nullable Typeface messageTypeface,
                             int buttonColor, int buttonTextColor, int buttonIconsColor, int wifiLoaderColor,
                             boolean cancelable) {
        super(context);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        isHalloween = NoInternetUtils.getCurrentDate().equals("10-31");

        this.bgGradientStart = bgGradientStart == 0
                ? (isHalloween
                ? ContextCompat.getColor(getContext(), R.color.colorNoInternetGradStartH)
                : ContextCompat.getColor(getContext(), R.color.colorNoInternetGradStart))
                : bgGradientStart;
        this.bgGradientCenter = bgGradientCenter == 0
                ? (isHalloween
                ? ContextCompat.getColor(getContext(), R.color.colorNoInternetGradCenterH)
                : ContextCompat.getColor(getContext(), R.color.colorNoInternetGradCenter))
                : bgGradientCenter;
        this.bgGradientEnd = bgGradientEnd == 0
                ? (isHalloween
                ? ContextCompat.getColor(getContext(), R.color.colorNoInternetGradEndH)
                : ContextCompat.getColor(getContext(), R.color.colorNoInternetGradEnd))
                : bgGradientEnd;
        this.bgGradientOrientation = bgGradientOrientation < 10 || bgGradientOrientation > 17 ? ORIENTATION_TOP_BOTTOM : bgGradientOrientation;
        this.bgGradientType = bgGradientType <= 0 || bgGradientType > 2 ? GRADIENT_LINEAR : bgGradientType;
        this.dialogRadius = dialogRadius == 0 ? RADIUS : dialogRadius;
        if (dialogRadius == NO_RADIUS) {
            this.dialogRadius = 0f;
        }
        this.titleTypeface = titleTypeface;
        this.messageTypeface = messageTypeface;

        this.buttonColor = buttonColor == 0
                ? (isHalloween
                ? ContextCompat.getColor(getContext(), R.color.colorNoInternetGradCenterH)
                : ContextCompat.getColor(getContext(), R.color.colorAccent))
                : buttonColor;
        this.buttonTextColor = buttonTextColor == 0
                ? ContextCompat.getColor(getContext(), R.color.colorWhite)
                : buttonTextColor;
        this.buttonIconsColor = buttonIconsColor == 0
                ? ContextCompat.getColor(getContext(), R.color.colorWhite)
                : buttonIconsColor;
        this.wifiLoaderColor = wifiLoaderColor == 0
                ? ContextCompat.getColor(getContext(), R.color.colorWhite)
                : wifiLoaderColor;

        this.cancelable = cancelable;

        initReceivers(context);
    }

    private void initReceivers(Context context) {
        wifiReceiver = new WifiReceiver();
        context.registerReceiver(wifiReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        networkStatusReceiver = new NetworkStatusReceiver();
        context.registerReceiver(networkStatusReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        wifiReceiver.setConnectionListener(this);
        networkStatusReceiver.setConnectionCallback(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_no_internet);

        initMainWindow();
        initView();
        initGuideLine();
        initBackground();
        initButtonStyle();
        initListeners();
        initHalloweenTheme();
        initAnimations();
        initTypefaces();
        initWifiLoading();
        initClose();
    }

    private void initMainWindow() {
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    private void initView() {
        root = findViewById(R.id.root);
        close = findViewById(R.id.close);
        moon = findViewById(R.id.moon);
        plane = findViewById(R.id.plane);
        ghost = findViewById(R.id.ghost);
        tomb = findViewById(R.id.tomb);
        ground = findViewById(R.id.ground);
        pumpkin = findViewById(R.id.pumpkin);
        wifiIndicator = findViewById(R.id.wifi_indicator);
        noInternet = findViewById(R.id.no_internet);
        noInternetBody = findViewById(R.id.no_internet_body);
        turnOn = findViewById(R.id.turn_on);
        wifiOn = findViewById(R.id.wifi_on);
        mobileOn = findViewById(R.id.mobile_on);
        airplaneOff = findViewById(R.id.airplane_off);
        wifiLoading = findViewById(R.id.wifi_loading);
        topGuide = findViewById(R.id.top_guide);
    }

    private void initGuideLine() {
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) topGuide.getLayoutParams();
        lp.guidePercent = isHalloween ? 0.34f : 0.3f;
        topGuide.setLayoutParams(lp);
    }

    private void initBackground() {
        GradientDrawable.Orientation orientation = getOrientation();

        GradientDrawable drawable = new GradientDrawable(orientation, new int[]{bgGradientStart, bgGradientCenter, bgGradientEnd});
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(dialogRadius);

        switch (bgGradientType) {
            case GRADIENT_RADIAL:
                drawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
                break;
            case GRADIENT_SWEEP:
                drawable.setGradientType(GradientDrawable.SWEEP_GRADIENT);
                break;
            default:
                drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
                break;
        }

        if (isHalloween) {
            drawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
            drawable.setGradientRadius(getContext().getResources().getDimensionPixelSize(R.dimen.dialog_height) / 2);
        } else {
            drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            root.setBackground(drawable);
        } else {
            root.setBackgroundDrawable(drawable);
        }
    }

    private void initButtonStyle() {
        wifiOn.getBackground().mutate().setColorFilter(buttonColor, PorterDuff.Mode.SRC_IN);
        mobileOn.getBackground().mutate().setColorFilter(buttonColor, PorterDuff.Mode.SRC_IN);
        airplaneOff.getBackground().mutate().setColorFilter(buttonColor, PorterDuff.Mode.SRC_IN);

        wifiOn.setTextColor(buttonTextColor);
        mobileOn.setTextColor(buttonTextColor);
        airplaneOff.setTextColor(buttonTextColor);

        Drawable wifi = ContextCompat.getDrawable(getContext(), R.drawable.ic_wifi_white);
        Drawable mobileData = ContextCompat.getDrawable(getContext(), R.drawable.ic_4g_white);
        Drawable airplane = ContextCompat.getDrawable(getContext(), R.drawable.ic_airplane_off);

        wifi.mutate().setColorFilter(buttonIconsColor, PorterDuff.Mode.SRC_ATOP);
        mobileData.mutate().setColorFilter(buttonIconsColor, PorterDuff.Mode.SRC_ATOP);
        airplane.mutate().setColorFilter(buttonIconsColor, PorterDuff.Mode.SRC_ATOP);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wifiOn.setCompoundDrawablesRelativeWithIntrinsicBounds(wifi, null, null, null);
            mobileOn.setCompoundDrawablesRelativeWithIntrinsicBounds(mobileData, null, null, null);
            airplaneOff.setCompoundDrawablesRelativeWithIntrinsicBounds(airplane, null, null, null);
        } else {
            wifiOn.setCompoundDrawablesWithIntrinsicBounds(wifi, null, null, null);
            mobileOn.setCompoundDrawablesWithIntrinsicBounds(mobileData, null, null, null);
            airplaneOff.setCompoundDrawablesWithIntrinsicBounds(airplane, null, null, null);
        }
    }

    private GradientDrawable.Orientation getOrientation() {
        GradientDrawable.Orientation orientation;
        switch (bgGradientOrientation) {
            case ORIENTATION_BOTTOM_TOP:
                orientation = GradientDrawable.Orientation.BOTTOM_TOP;
                break;
            case ORIENTATION_RIGHT_LEFT:
                orientation = GradientDrawable.Orientation.RIGHT_LEFT;
                break;
            case ORIENTATION_LEFT_RIGHT:
                orientation = GradientDrawable.Orientation.LEFT_RIGHT;
                break;
            case ORIENTATION_BL_TR:
                orientation = GradientDrawable.Orientation.BL_TR;
                break;
            case ORIENTATION_TR_BL:
                orientation = GradientDrawable.Orientation.TR_BL;
                break;
            case ORIENTATION_BR_TL:
                orientation = GradientDrawable.Orientation.BR_TL;
                break;
            case ORIENTATION_TL_BR:
                orientation = GradientDrawable.Orientation.TL_BR;
                break;
            default:
                orientation = GradientDrawable.Orientation.TOP_BOTTOM;
                break;
        }

        return orientation;
    }

    private void initListeners() {
        close.setOnClickListener(this);
        wifiOn.setOnClickListener(this);
        mobileOn.setOnClickListener(this);
        airplaneOff.setOnClickListener(this);
    }

    private void initHalloweenTheme() {
        if (!isHalloween) {
            return;
        }

        Bitmap bmp = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ground);
        RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(getContext().getResources(), bmp);
        dr.setCornerRadius(dialogRadius);
        dr.setAntiAlias(true);

        ground.setBackgroundDrawable(dr);

        plane.setImageResource(R.drawable.witch);
        tomb.setImageResource(R.drawable.tomb_hw);
        moon.setVisibility(View.VISIBLE);
        ground.setVisibility(View.VISIBLE);
        pumpkin.setVisibility(View.VISIBLE);
        wifiOn.getBackground().mutate().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorNoInternetGradCenterH), PorterDuff.Mode.SRC_IN);
        mobileOn.getBackground().mutate().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorNoInternetGradCenterH), PorterDuff.Mode.SRC_IN);
        airplaneOff.getBackground().mutate().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorNoInternetGradCenterH), PorterDuff.Mode.SRC_IN);
    }

    private void initAnimations() {
        direction = animateDirection();
        final ObjectAnimator ghostXAnimator = ObjectAnimator.ofFloat(ghost, "translationX", 1f, GHOST_X_ANIMATION_VALUE * direction);
        ObjectAnimator ghostYAnimator = ObjectAnimator.ofFloat(ghost, "translationY", 1f, GHOST_Y_ANIMATION_VALUE);
        ObjectAnimator scaleXGhostAnimator = ObjectAnimator.ofFloat(ghost, "scaleX", 1f, GHOST_SCALE_ANIMATION_VALUE);
        ObjectAnimator scaleYGhostAnimator = ObjectAnimator.ofFloat(ghost, "scaleY", 1f, GHOST_SCALE_ANIMATION_VALUE);
        final ObjectAnimator ghostXAnimatorReverse = ObjectAnimator.ofFloat(ghost, "translationX", GHOST_X_ANIMATION_VALUE * direction, 1f);
        ObjectAnimator ghostYAnimatorReverse = ObjectAnimator.ofFloat(ghost, "translationY", GHOST_Y_ANIMATION_VALUE, 1f);
        ObjectAnimator scaleXGhostAnimatorReverse = ObjectAnimator.ofFloat(ghost, "scaleX", GHOST_SCALE_ANIMATION_VALUE, 1f);
        ObjectAnimator scaleYGhostAnimatorReverse = ObjectAnimator.ofFloat(ghost, "scaleY", GHOST_SCALE_ANIMATION_VALUE, 1f);

        final AnimatorSet ghostSet = new AnimatorSet();
        ghostSet.playTogether(ghostXAnimator, ghostYAnimator, scaleXGhostAnimator, scaleYGhostAnimator);
        ghostSet.setDuration(ANIMATION_DURATION);
        ghostSet.setStartDelay(ANIMATION_DELAY);
        ghostSet.setInterpolator(new DecelerateInterpolator());

        final AnimatorSet ghostSetReverse = new AnimatorSet();
        ghostSetReverse.playTogether(ghostXAnimatorReverse, ghostYAnimatorReverse, scaleXGhostAnimatorReverse, scaleYGhostAnimatorReverse);
        ghostSetReverse.setDuration(ANIMATION_DURATION);
        ghostSetReverse.setStartDelay(ANIMATION_DELAY);
        ghostSetReverse.setInterpolator(new DecelerateInterpolator());

        ghostSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ghostXAnimatorReverse.setFloatValues(GHOST_X_ANIMATION_VALUE * direction, 1f);
                ghostSetReverse.start();
            }
        });

        ghostSetReverse.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                direction = animateDirection();
                ghostXAnimator.setFloatValues(1f, GHOST_X_ANIMATION_VALUE * direction);
                ghostSet.start();
            }
        });

        ghostSet.start();
        startFlight();
    }

    private void startFlight() {
        if (!NoInternetUtils.isAirplaneModeOn(getContext())) {
            plane.setVisibility(View.GONE);
            return;
        }

        plane.setVisibility(View.VISIBLE);
        noInternetBody.setText(R.string.airplane_on);
        turnOn.setText(R.string.turn_off);
        airplaneOff.setVisibility(View.VISIBLE);
        wifiOn.setVisibility(View.INVISIBLE);
        mobileOn.setVisibility(View.INVISIBLE);

        final ObjectAnimator flightThere = ObjectAnimator.ofFloat(plane, "translationX", FLIGHT_THERE_START, FLIGHT_THERE_END);
        final ObjectAnimator flightBack = ObjectAnimator.ofFloat(plane, "translationX", FLIGHT_BACK_START, FLIGHT_BACK_END);

        flightThere.setDuration(FLIGHT_DURATION);
        flightBack.setDuration(FLIGHT_DURATION);

        flightThere.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                plane.setRotationY(180);
                flightBack.start();
            }
        });

        flightBack.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                plane.setRotationY(0);
                flightThere.start();
            }
        });

        flightThere.start();
    }

    private void initTypefaces() {
        if (titleTypeface != null) {
            noInternet.setTypeface(titleTypeface);
        }

        if (messageTypeface != null) {
            noInternetBody.setTypeface(messageTypeface);
        }
    }

    private void initWifiLoading() {
        wifiLoading.getIndeterminateDrawable().setColorFilter(wifiLoaderColor, PorterDuff.Mode.SRC_IN);
        ViewCompat.setElevation(wifiLoading, 10);
    }

    private void initClose() {
        setCancelable(cancelable);
        close.setVisibility(cancelable ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.close) {
            dismiss();
        } else if (id == R.id.wifi_on) {
            turnOnWifiWithAnimation();
        } else if (id == R.id.mobile_on) {
            NoInternetUtils.turnOn3g(getContext());
            dismiss();
        } else if (id == R.id.airplane_off) {
            NoInternetUtils.turnOffAirplaneMode(getContext());
            dismiss();
        }
    }

    private void turnOnWifiWithAnimation() {
        ValueAnimator widthAnimator = ValueAnimator.ofFloat(
                getContext().getResources().getDimensionPixelSize(R.dimen.button_width),
                getContext().getResources().getDimensionPixelSize(R.dimen.button_width) + 10,
                getContext().getResources().getDimensionPixelSize(R.dimen.button_size2));
        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                RelativeLayout.LayoutParams lpWifi = (RelativeLayout.LayoutParams) wifiOn.getLayoutParams();
                lpWifi.width = (int) value;
                wifiOn.setLayoutParams(lpWifi);
            }
        });
        ObjectAnimator translateXAnimatorWifi = ObjectAnimator.ofFloat(wifiOn, "translationX", 1f, 110f);
        ObjectAnimator translateYAnimatorWifi = ObjectAnimator.ofFloat(wifiOn, "translationY", 1f, 0f);
        ObjectAnimator translateXAnimatorLoading = ObjectAnimator.ofFloat(wifiLoading, "translationX", 1f, 104f);
        ObjectAnimator translateYAnimatorLoading = ObjectAnimator.ofFloat(wifiLoading, "translationY", 1f, -10f);

        ValueAnimator textSizeAnimator = ValueAnimator.ofFloat(13f, 0f);
        textSizeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                wifiOn.setTextSize(value);
            }
        });

        translateXAnimatorWifi.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                wifiLoading.setVisibility(View.VISIBLE);
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.playTogether(widthAnimator, translateXAnimatorWifi, translateYAnimatorWifi, translateXAnimatorLoading, translateYAnimatorLoading, textSizeAnimator);
        set.setDuration(400);
        set.start();
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                NoInternetUtils.turnOnWifi(getContext());
                animateWifi();
            }
        });
    }

    private void animateWifi() {
        wifiAnimator = ObjectAnimator.ofFloat(wifiIndicator, "alpha", 0f, 0.5f);
        wifiAnimator.setDuration(1500);
        wifiAnimator.setRepeatMode(ValueAnimator.RESTART);
        wifiAnimator.setRepeatCount(ValueAnimator.INFINITE);

        wifiAnimator.start();
    }

    private int animateDirection() {
        Random r = new Random();
        return r.nextInt(2);
    }

    @Override
    public void onWifiTurnedOn() {
        if (wifiAnimator != null && wifiAnimator.isStarted()) {
            wifiAnimator.cancel();
            wifiIndicator.setImageResource(R.drawable.ic_wifi);
            wifiIndicator.setAlpha(0.5f);
            isWifiOn = true;
            getContext().unregisterReceiver(wifiReceiver);
        }
    }

    @Override
    public void onWifiTurnedOff() {

    }

    @Override
    public void hasActiveConnection(boolean hasActiveConnection) {
        if(this.connectionCallback != null)
            this.connectionCallback.hasActiveConnection(hasActiveConnection);
        if (!hasActiveConnection) {
            showDialog();
        } else {
            dismiss();
        }
    }

    @Override
    public void show() {
        super.show();
        startFlight();
    }

    public void showDialog() {
        Ping ping = new Ping();
        ping.setConnectionCallback(new ConnectionCallback() {
            @Override
            public void hasActiveConnection(boolean hasActiveConnection) {
                if (!hasActiveConnection) {
                    show();
                }
            }
        });
        ping.execute(getContext());
    }

    @Override
    public void dismiss() {
        reset();
        super.dismiss();
    }

    private void reset() {
        if (airplaneOff != null) {
            airplaneOff.setVisibility(View.GONE);
        }
        if (wifiOn != null) {
            wifiOn.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams wifiParams = (RelativeLayout.LayoutParams) wifiOn.getLayoutParams();
            wifiParams.width = getContext().getResources().getDimensionPixelSize(R.dimen.button_width);
            wifiOn.setLayoutParams(wifiParams);
            wifiOn.setTextSize(13f);
            wifiOn.setTranslationX(1f);
            wifiOn.setTranslationY(1f);
        }
        if (mobileOn != null) {
            mobileOn.setVisibility(View.VISIBLE);
        }
        if (turnOn != null) {
            turnOn.setText(R.string.turn_on);
        }
        if (wifiLoading != null) {
            wifiLoading.setTranslationX(1f);
            wifiLoading.setVisibility(View.INVISIBLE);
        }
        if (ghost != null) {
            ghost.setTranslationY(1f);
        }
    }

    public void onDestroy() {
        try {
            getContext().unregisterReceiver(networkStatusReceiver);
        }catch(Exception e){ }
        try {
            getContext().unregisterReceiver(wifiReceiver);
        }catch (Exception e){}
    }

    public void setConnectionCallback(ConnectionCallback connectionCallback) {
        this.connectionCallback = connectionCallback;
    }

    public static class Builder {
        private Context context;
        private int bgGradientStart;
        private int bgGradientCenter;
        private int bgGradientEnd;
        private int bgGradientOrientation;
        private int bgGradientType;
        private float dialogRadius;
        private Typeface titleTypeface;
        private Typeface messageTypeface;
        private int buttonColor;
        private int buttonTextColor;
        private int buttonIconsColor;
        private int wifiLoaderColor;
        private ConnectionCallback connectionCallback;
        private boolean cancelable;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder(Fragment fragment) {
            this.context = fragment.getContext();
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        public Builder(android.app.Fragment fragment) {
            this.context = fragment.getContext();
        }

        public Builder setBgGradientStart(@ColorInt int bgGradientStart) {
            this.bgGradientStart = bgGradientStart;
            return this;
        }

        public Builder setBgGradientCenter(@ColorInt int bgGradientCenter) {
            this.bgGradientCenter = bgGradientCenter;
            return this;
        }

        public Builder setBgGradientEnd(@ColorInt int bgGradientEnd) {
            this.bgGradientEnd = bgGradientEnd;
            return this;
        }

        public Builder setBgGradientOrientation(@Orientation int bgGradientOrientation) {
            this.bgGradientOrientation = bgGradientOrientation;
            return this;
        }

        public Builder setBgGradientType(int bgGradientType) {
            this.bgGradientType = bgGradientType;
            return this;
        }

        public Builder setDialogRadius(float dialogRadius) {
            this.dialogRadius = dialogRadius;
            return this;
        }

        public Builder setDialogRadius(@DimenRes int dialogRadiusDimen) {
            this.dialogRadius = context.getResources().getDimensionPixelSize(dialogRadiusDimen);
            return this;
        }

        public Builder setTitleTypeface(Typeface titleTypeface) {
            this.titleTypeface = titleTypeface;
            return this;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public Builder setTitleTypeface(int titleTypefaceId) {
            this.titleTypeface = context.getResources().getFont(titleTypefaceId);
            return this;
        }

        public Builder setMessageTypeface(Typeface messageTypeface) {
            this.messageTypeface = messageTypeface;
            return this;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public Builder setMessageTypeface(int messageTypefaceId) {
            this.messageTypeface = context.getResources().getFont(messageTypefaceId);
            return this;
        }

        public Builder setButtonColor(int buttonColor) {
            this.buttonColor = buttonColor;
            return this;
        }

        public Builder setButtonTextColor(int buttonTextColor) {
            this.buttonTextColor = buttonTextColor;
            return this;
        }

        public Builder setButtonIconsColor(int buttonIconsColor) {
            this.buttonIconsColor = buttonIconsColor;
            return this;
        }

        public Builder setWifiLoaderColor(int wifiLoaderColor) {
            this.wifiLoaderColor = wifiLoaderColor;
            return this;
        }

        public Builder setConnectionCallback(ConnectionCallback callback) {
            this.connectionCallback = callback;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public NoInternetDialog build() {
            NoInternetDialog dialog = new NoInternetDialog(context, bgGradientStart, bgGradientCenter, bgGradientEnd,
                    bgGradientOrientation, bgGradientType, dialogRadius, titleTypeface, messageTypeface,
                    buttonColor, buttonTextColor, buttonIconsColor, wifiLoaderColor, cancelable);
            dialog.setConnectionCallback(connectionCallback);

            return dialog;
        }
    }
}
