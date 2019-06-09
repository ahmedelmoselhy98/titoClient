package com.OrxtraDev.TitoApp.view;

/**
 * Created by hazem on 10/27/2017.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.OrxtraDev.TitoApp.R;
import com.github.javiersantos.bottomdialogs.R.attr;

public class BottomDialog {
    protected final Builder mBuilder;

    public final Builder getBuilder() {
        return this.mBuilder;
    }

    protected BottomDialog(Builder builder) {
        this.mBuilder = builder;
        this.mBuilder.bottomDialog = this.initBottomDialog(builder);
    }

    @UiThread
    public void show() {
        if(this.mBuilder != null && this.mBuilder.bottomDialog != null) {
            this.mBuilder.bottomDialog.show();
        }

    }

    @UiThread
    public void dismiss() {
        if(this.mBuilder != null && this.mBuilder.bottomDialog != null) {
            this.mBuilder.bottomDialog.dismiss();
        }

    }

    @SuppressLint("WrongConstant")
    @UiThread
    private Dialog initBottomDialog(final Builder builder) {
        final Dialog bottomDialog = new Dialog(builder.context, com.github.javiersantos.bottomdialogs.R.style.BottomDialogs);
        View view = builder.activity.getLayoutInflater().inflate(R.layout.library_bottom_dialog, (ViewGroup)null);
        ImageView vIcon = (ImageView)view.findViewById(R.id.bottomDialog_icon);
        TextView vTitle = (TextView)view.findViewById(R.id.bottomDialog_title);
        TextView vContent = (TextView)view.findViewById(R.id.bottomDialog_content);
        FrameLayout vCustomView = (FrameLayout)view.findViewById(R.id.bottomDialog_custom_view);
        Button vNegative = (Button)view.findViewById(R.id.bottomDialog_cancel);
        Button vPositive = (Button)view.findViewById(R.id.bottomDialog_ok);
        if(builder.icon != null) {
            vIcon.setVisibility(0);
            vIcon.setImageDrawable(builder.icon);
        }

        if(builder.title != null) {
            vTitle.setText(builder.title);
        }

        if(builder.content != null) {
            vContent.setText(builder.content);
        }

        if(builder.customView != null) {
            if(builder.customView.getParent() != null) {
                ((ViewGroup)builder.customView.getParent()).removeAllViews();
            }

            vCustomView.addView(builder.customView);
            vCustomView.setPadding(builder.customViewPaddingLeft, builder.customViewPaddingTop, builder.customViewPaddingRight, builder.customViewPaddingBottom);
        }

        if(builder.btn_positive != null) {
            vPositive.setVisibility(0);
            vPositive.setText(builder.btn_positive);
            vPositive.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if(builder.btn_positive_callback != null) {
                        builder.btn_positive_callback.onClick(BottomDialog.this);
                    }

                    if(builder.isAutoDismiss) {
                        bottomDialog.dismiss();
                    }

                }
            });
            if(builder.btn_colorPositive != 0) {
                vPositive.setTextColor(builder.btn_colorPositive);
            }

            if(builder.btn_colorPositiveBackground == 0) {
                TypedValue buttonBackground = new TypedValue();
                boolean hasColorPrimary = builder.context.getTheme().resolveAttribute(attr.colorPrimary, buttonBackground, true);
                builder.btn_colorPositiveBackground = !hasColorPrimary?buttonBackground.data:ContextCompat.getColor(builder.context, R.color.colorPrimary);
            }

            Drawable buttonBackground1 = UtilsLibrary.createButtonBackgroundDrawable(builder.activity, builder.btn_colorPositiveBackground);
            if(VERSION.SDK_INT >= 16) {
                vPositive.setBackground(buttonBackground1);
            } else {
                vPositive.setBackgroundDrawable(buttonBackground1);
            }
        }

        if(builder.btn_negative != null) {
            vNegative.setVisibility(0);
            vNegative.setText(builder.btn_negative);
            vNegative.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if(builder.btn_negative_callback != null) {
                        builder.btn_negative_callback.onClick(BottomDialog.this);
                    }

                    if(builder.isAutoDismiss) {
                        bottomDialog.dismiss();
                    }

                }
            });
            if(builder.btn_colorNegative != 0) {
                vNegative.setTextColor(builder.btn_colorNegative);
            }
        }

        bottomDialog.setContentView(view);
        bottomDialog.setCancelable(builder.isCancelable);
        bottomDialog.getWindow().setLayout(-1, -2);
        bottomDialog.getWindow().setGravity(80);
        return bottomDialog;
    }

    public interface ButtonCallback {
        void onClick(@NonNull BottomDialog var1);
    }

    public static class Builder {
        protected Activity activity;
        protected Context context;
        protected Dialog bottomDialog;
        protected Drawable icon;
        protected CharSequence title;
        protected CharSequence content;
        protected CharSequence btn_negative;
        protected CharSequence btn_positive;
        protected ButtonCallback btn_negative_callback;
        protected ButtonCallback btn_positive_callback;
        protected boolean isAutoDismiss;
        protected int btn_colorNegative;
        protected int btn_colorPositive;
        protected int btn_colorPositiveBackground;
        protected View customView;
        protected int customViewPaddingLeft;
        protected int customViewPaddingTop;
        protected int customViewPaddingRight;
        protected int customViewPaddingBottom;
        protected boolean isCancelable;

        public Builder(@NonNull Context context) {
            this.activity = (Activity)context;
            this.context = context;
            this.isCancelable = true;
            this.isAutoDismiss = true;
        }

        public Builder setTitle(@StringRes int titleRes) {
            this.setTitle(this.context.getString(titleRes));
            return this;
        }

        public Builder setTitle(@NonNull CharSequence title) {
            this.title = title;
            return this;
        }

        public Builder setContent(@StringRes int contentRes) {
            this.setContent(this.context.getString(contentRes));
            return this;
        }

        public Builder setContent(@NonNull CharSequence content) {
            this.content = content;
            return this;
        }

        public Builder setIcon(@NonNull Drawable icon) {
            this.icon = icon;
            return this;
        }

        public Builder setIcon(@DrawableRes int iconRes) {
            this.icon = ResourcesCompat.getDrawable(this.context.getResources(), iconRes, (Theme)null);
            return this;
        }

        public Builder setPositiveBackgroundColorResource(@ColorRes int buttonColorRes) {
            this.btn_colorPositiveBackground = ResourcesCompat.getColor(this.context.getResources(), buttonColorRes, (Theme)null);
            return this;
        }

        public Builder setPositiveBackgroundColor(int color) {
            this.btn_colorPositiveBackground = color;
            return this;
        }

        public Builder setPositiveTextColorResource(@ColorRes int textColorRes) {
            this.btn_colorPositive = ResourcesCompat.getColor(this.context.getResources(), textColorRes, (Theme)null);
            return this;
        }

        public Builder setPositiveTextColor(int color) {
            this.btn_colorPositive = color;
            return this;
        }

        public Builder setPositiveText(@StringRes int buttonTextRes) {
            this.setPositiveText(this.context.getString(buttonTextRes));
            return this;
        }

        public Builder setPositiveText(@NonNull CharSequence buttonText) {
            this.btn_positive = buttonText;
            return this;
        }

        public Builder onPositive(@NonNull ButtonCallback buttonCallback) {
            this.btn_positive_callback = buttonCallback;
            return this;
        }

        public Builder setNegativeTextColorResource(@ColorRes int textColorRes) {
            this.btn_colorNegative = ResourcesCompat.getColor(this.context.getResources(), textColorRes, (Theme)null);
            return this;
        }

        public Builder setNegativeTextColor(int color) {
            this.btn_colorNegative = color;
            return this;
        }

        public Builder setNegativeText(@StringRes int buttonTextRes) {
            this.setNegativeText(this.context.getString(buttonTextRes));
            return this;
        }

        public Builder setNegativeText(@NonNull CharSequence buttonText) {
            this.btn_negative = buttonText;
            return this;
        }

        public Builder onNegative(@NonNull ButtonCallback buttonCallback) {
            this.btn_negative_callback = buttonCallback;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.isCancelable = cancelable;
            return this;
        }

        public Builder autoDismiss(boolean autodismiss) {
            this.isAutoDismiss = autodismiss;
            return this;
        }

        public Builder setCustomView(View customView) {
            this.customView = customView;
            this.customViewPaddingLeft = 0;
            this.customViewPaddingRight = 0;
            this.customViewPaddingTop = 0;
            this.customViewPaddingBottom = 0;
            return this;
        }

        public Builder setCustomView(View customView, int left, int top, int right, int bottom) {
            this.customView = customView;
            this.customViewPaddingLeft = UtilsLibrary.dpToPixels(this.context, left);
            this.customViewPaddingRight = UtilsLibrary.dpToPixels(this.context, right);
            this.customViewPaddingTop = UtilsLibrary.dpToPixels(this.context, top);
            this.customViewPaddingBottom = UtilsLibrary.dpToPixels(this.context, bottom);
            return this;
        }

        @UiThread
        public BottomDialog build() {
            return new BottomDialog(this);
        }

        @UiThread
        public BottomDialog show() {
            BottomDialog bottomDialog = this.build();
            bottomDialog.show();
            return bottomDialog;
        }
    }
}
