package com.haiyunshan.express.app;

/**
 * Created by sanshibro on 2017/12/10.
 */

public class ParagraphConstants {

    public static final int maxTextSize     = 64;
    public static final int minTextSize     = 8;
    public static final int deltaTextSize   = 2;

    public static final int maxLetterSpacing     = 200;
    public static final int minLetterSpacing     = 50;
    public static final int deltaLetterSpacing   = 10;

    public static final int maxLineSpacing     = 200;
    public static final int minLineSpacing     = 50;
    public static final int deltaLineSpacing   = 10;

    public static final int minPadding = 0;
    public static final int maxPadding = 100;
    public static final int deltaPadding = 5;

    /**
     *
     */
    public static final class FractionParams {

        int mValue;
        int mMax;
        int mMin;
        int mDelta;

        int mDenominator;

        String mFormat;

        public FractionParams() {
            this.mValue = 100;
            this.mMax = 200;
            this.mMin = 50;
            this.mDelta = 10;

            this.mDenominator = 100;

            this.mFormat = "%1$1.1f";
        }

        public FractionParams(int value, int max, int min, int delta, String format) {
            this.mValue = value;
            this.mMax = max;
            this.mMin = min;
            this.mDelta = delta;

            this.mDenominator = 100;

            this.mFormat = format;
        }

        public int getIntValue() {
            return this.mValue;
        }

        public float getValue() {
            return this.mValue * 1.f / this.mDenominator;
        }

        public float increase() {
            this.mValue += mDelta;
            mValue = (mValue > mMax)? mMax: mValue;
            return this.getValue();
        }

        public float decrease() {
            this.mValue -= mDelta;
            mValue = (mValue < mMin)? mMin: mValue;
            return getValue();
        }

        public boolean isMax() {
            return mValue >= mMax;
        }

        public boolean isMin() {
            return mValue <= mMin;
        }

        @Override
        public String toString() {
            return String.format(this.mFormat, getValue());
        }
    }

    /**
     *
     */
    public static final class IntParams {

        int mValue;
        int mMax;
        int mMin;
        int mDelta;

        String mFormat;

        public IntParams() {
            this.mValue = 18;
            this.mMax = 48;
            this.mMin = 8;
            this.mDelta = 2;

            this.mFormat = "%1$d磅";
        }

        public IntParams(int value, int max, int min, int delta, String format) {
            this.mValue = value;
            this.mMax = max;
            this.mMin = min;
            this.mDelta = delta;

            this.mFormat = format;
        }

        public int getValue() {
            return this.mValue;
        }

        public int increase() {
            this.mValue += mDelta;
            mValue = (mValue > mMax)? mMax: mValue;
            return mValue;
        }

        public int decrease() {
            this.mValue -= mDelta;
            mValue = (mValue < mMin)? mMin: mValue;
            return mValue;
        }

        public boolean isMax() {
            return mValue >= mMax;
        }

        public boolean isMin() {
            return mValue <= mMin;
        }

        @Override
        public String toString() {
            return String.format(this.mFormat, mValue);
        }
    }

}
