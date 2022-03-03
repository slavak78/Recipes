package ru.slava.recipes;

import android.app.Application;

import androidx.annotation.NonNull;

import org.solovyev.android.checkout.Billing;

public class Recipes extends Application {

    private static Recipes sInstance;

    private final Billing mBilling = new Billing(this, new Billing.DefaultConfiguration() {
        @NonNull
        @Override public String getPublicKey() {
            return "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAu6b6P6htwXtWwpzBHqjcddfCLMtZP3AChvyEDMvh1Z2eSOFUPhQRXY9qkHNRjmpOV9INvujJNkW3Pj6hZSRl/C1VoV/i633Msl8bZrmX+QTzSpEjctJ7FtQ+8Su/N/+CtIxieULUHGyHiuMYkNRe7N2AfGEI/uMJ0nYlQ5c7wx98AIlYMtTvrRE+JUifNXJsrlF/vQMmnRU+H3BhT0bH0MiARRwrb1lYLlDnxFiMjPFfnNCJxK0Yc6dez+3hZL6k7usYdwIIZg/g270qBAGt6BB5J2PNLZ8hF+2Vwv3Z/9UhSjFm3VNMWpg9iZDfzd1PxTeBi1O93tLTdibOTQpDFwIDAQAB";
        }

    });

    public Recipes() {
        sInstance = this;
    }

    public static Recipes get() {
        return sInstance;
    }

    public Billing getBilling() {
        return mBilling;
    }
}
