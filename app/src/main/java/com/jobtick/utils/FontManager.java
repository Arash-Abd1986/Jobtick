package com.jobtick.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.lang.ref.WeakReference;

public class FontManager {

    private static FontManager Instance;
    WeakReference<Context> contextWeakReference;

//    private Typeface montserratBlack;
//    private Typeface montserratBold;
//    private Typeface montserratExtraBold;
//    private Typeface montserratHairline;
//    private Typeface montserratLight;
//    private Typeface montserratRegular;
//    private Typeface montserratSemiBold;
//    private Typeface montserratThin;
//    private Typeface montserratUltraLight;
    private Typeface poppinsBoldItalic;
    private Typeface poppinsItalic;
    private Typeface poppinsBold;
    private Typeface poppinsMedium;
    private Typeface poppinsRegular;
    private Typeface poppinsSemiBold;

    private FontManager(Context contextWeakReference) {
        this.contextWeakReference = new WeakReference<Context>(contextWeakReference);

//        this.montserratBlack = Typeface.createFromAsset(contextWeakReference.getAssets(), "fonts/Montserrat-Black.otf");
//        this.montserratBold = Typeface.createFromAsset(contextWeakReference.getAssets(), "fonts/Montserrat-Bold.otf");
//        this.montserratExtraBold = Typeface.createFromAsset(contextWeakReference.getAssets(), "fonts/Montserrat-ExtraBold.otf");
//        this.montserratHairline = Typeface.createFromAsset(contextWeakReference.getAssets(), "fonts/Montserrat-Hairline.otf");
//        this.montserratLight = Typeface.createFromAsset(contextWeakReference.getAssets(), "fonts/Montserrat-Light.otf");
//        this.montserratRegular = Typeface.createFromAsset(contextWeakReference.getAssets(), "fonts/Montserrat-Regular.otf");
//        this.montserratSemiBold = Typeface.createFromAsset(contextWeakReference.getAssets(), "fonts/Montserrat-SemiBold.otf");
//        this.montserratThin = Typeface.createFromAsset(contextWeakReference.getAssets(), "fonts/Montserrat-Thin.ttf");
//        this.montserratUltraLight = Typeface.createFromAsset(contextWeakReference.getAssets(), "fonts/Montserrat-UltraLight.otf");
        this.poppinsBoldItalic = Typeface.createFromAsset(contextWeakReference.getAssets(), "fonts/Poppins-BoldItalic.ttf");
        this.poppinsItalic = Typeface.createFromAsset(contextWeakReference.getAssets(), "fonts/Poppins-Italic.ttf");
        this.poppinsBold = Typeface.createFromAsset(contextWeakReference.getAssets(), "fonts/poppins_Bold.otf");
        this.poppinsMedium = Typeface.createFromAsset(contextWeakReference.getAssets(), "fonts/poppins_Medium.otf");
        this.poppinsRegular = Typeface.createFromAsset(contextWeakReference.getAssets(), "fonts/poppins_Regular.otf");
        this.poppinsSemiBold = Typeface.createFromAsset(contextWeakReference.getAssets(), "fonts/poppins_SemiBold.otf");

    }

    public static FontManager getInstance(Context context) {
        if (Instance == null) {
            Instance = new FontManager(context);
        }
        return Instance;
    }

    public Typeface getByType(int type) {
        switch (type) {
//            case 0:
//                return getMontserratBlack();
//            case 1:
//                return getMontserratBold();
//            case 2:
//                return getMontserratExtraBold();
//            case 3:
//                return getMontserratHairline();
//            case 4:
//                return getMontserratLight();
//            case 5:
//                return getMontserratRegular();
//            case 6:
//                return getMontserratSemiBold();
//            case 7:
//                return getMontserratThin();
//            case 8:
//                return getMontserratUltraLight();
            case 9:
                return getPoppinsBoldItalic();
            case 10:
                return getPoppinsItalic();
            case 11:
                return getPoppinsBold();
            case 12:
                return getPoppinsMedium();
            case 14:
                return getPoppinsSemiBold();
            case 13:
            default:
                return getPoppinsRegular();
        }
    }

//    public Typeface getMontserratBlack() {
//        return montserratBlack;
//    }
//
//    public Typeface getMontserratBold() {
//        return montserratBold;
//    }
//
//    public Typeface getMontserratExtraBold() {
//        return montserratExtraBold;
//    }
//
//    public Typeface getMontserratHairline() {
//        return montserratHairline;
//    }
//
//    public Typeface getMontserratLight() {
//        return montserratLight;
//    }
//
//    public Typeface getMontserratRegular() {
//        return montserratRegular;
//    }
//
//    public Typeface getMontserratSemiBold() {
//        return montserratSemiBold;
//    }
//
//    public Typeface getMontserratThin() {
//        return montserratThin;
//    }
//
//    public Typeface getMontserratUltraLight() {
//        return montserratUltraLight;
//    }

    public Typeface getPoppinsBoldItalic() {
        return poppinsBoldItalic;
    }

    public Typeface getPoppinsItalic() {
        return poppinsItalic;
    }

    public Typeface getPoppinsBold() {
        return poppinsBold;
    }

    public Typeface getPoppinsMedium() {
        return poppinsMedium;
    }

    public Typeface getPoppinsRegular() {
        return poppinsRegular;
    }

    public Typeface getPoppinsSemiBold() {
        return poppinsSemiBold;
    }
}
