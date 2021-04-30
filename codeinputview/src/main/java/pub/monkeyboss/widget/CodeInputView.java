package pub.monkeyboss.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;


/**
 * 验证码输入View
 * <attr name="code_length" format="integer" />         验证码长度
 * <attr name="child_h_padding" format="dimension" />   验证码输入框横向padding
 * <attr name="child_v_padding" format="dimension" />   验证码输入框垂直方向padding
 * <attr name="child_width" format="dimension" />       验证码输入框宽度
 * <attr name="child_height" format="dimension" />      验证码输入框高度
 * <attr name="padding" format="dimension" />           验证码输入框padding
 * <attr name="child_background" format="reference" />  验证码输入框背景，注：支持设置selector background，实现输入框在获得焦点和失去焦点状态下的background。child_background、full_background、blank_background同时存在时，仅child_background有效
 * <attr name="full_background" format="reference" />   验证码输入框有内容时的背景
 * <attr name="blank_background" format="reference" />  验证码输入框没有内容时的背景
 * <attr name="cursorVisible" format="boolean" />       验证码输入框光标是否可见
 * <attr name="textColor" format="reference" />         验证码输入框文字颜色
 * <attr name="inputType">                              验证码类型，支持number、password、text三种
 * <enum name="number" value="0" />
 * <enum name="text" value="1" />
 * <enum name="password" value="2" />
 * </attr>
 */

public class CodeInputView extends ViewGroup {

    private final static int TYPE_NUMBER = 0;
    private final static int TYPE_TEXT = 1;
    private final static int TYPE_PASSWORD = 2;

    private TransformationMethod mTransformationMethod;

    private int mLength;
    private int mChildWidth = 120;
    private int mChildHeight = 120;
    private int mChildHPadding = 14;
    private int mChildVPadding = 14;
    private int mInputType = TYPE_NUMBER;
    private int mChildBackground;
    private int mFullBackground;
    private int mBlankBackground;
    private boolean mCursorVisible;
    private int mTextColor = Color.parseColor("#333333");
    private OnInputListener mOnInputListener;

    private StringBuffer mBuffer = new StringBuffer();

    public CodeInputView(Context context) {
        this(context, null);
    }

    public CodeInputView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CodeInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.codeInputView);
        mLength = typedArray.getInt(R.styleable.codeInputView_code_length, 4);
        mChildHPadding = (int) typedArray.getDimension(R.styleable.codeInputView_child_h_padding, 0);
        mChildVPadding = (int) typedArray.getDimension(R.styleable.codeInputView_child_v_padding, 0);
        mChildBackground = typedArray.getResourceId(R.styleable.codeInputView_child_background, 0);
        mFullBackground = typedArray.getResourceId(R.styleable.codeInputView_full_background, 0);
        mBlankBackground = typedArray.getResourceId(R.styleable.codeInputView_blank_background, 0);
        mCursorVisible = typedArray.getBoolean(R.styleable.codeInputView_cursorVisible, true);
        mTextColor = typedArray.getColor(R.styleable.codeInputView_textColor, mTextColor);
        mInputType = typedArray.getInt(R.styleable.codeInputView_inputType, TYPE_NUMBER);
        mChildWidth = (int) typedArray.getDimension(R.styleable.codeInputView_child_width, mChildWidth);
        mChildHeight = (int) typedArray.getDimension(R.styleable.codeInputView_child_height, mChildHeight);
        typedArray.recycle();
        initViews();
    }

    private void initViews() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1 && mBuffer.length() < mLength) {
                    inputAndMoveToNext(s.toString());
                    setChildBg();
                    check(true);
                } else if (s.length() == 0) {
                    inputAndMoveToNext(null);
                    setChildBg();
                    check(false);
                }
            }
        };

        OnKeyListener onKeyListener = new OnKeyListener() {
            @Override
            public synchronized boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_UP) {
                    if (mBuffer.length() > 0) {
                        delete();
                    }
                }
                return false;
            }
        };

        for (int i = 0; i < mLength; i++) {
            EditText editText = new EditText(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(mChildWidth, mChildHeight);
            layoutParams.bottomMargin = mChildVPadding;
            layoutParams.topMargin = mChildVPadding;
            layoutParams.leftMargin = mChildHPadding;
            layoutParams.rightMargin = mChildHPadding;
            layoutParams.gravity = Gravity.CENTER;


            editText.setOnKeyListener(onKeyListener);
            editText.setBackgroundResource(mChildBackground > 0 ? mChildBackground : mBlankBackground);
            editText.setTextColor(mTextColor);
            editText.setLayoutParams(layoutParams);
            editText.setGravity(Gravity.CENTER);
            editText.setPadding(0, 0, 0, 0);
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});

            switch (mInputType) {
                case TYPE_NUMBER:
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;
                case TYPE_PASSWORD:
                    editText.setTransformationMethod(mTransformationMethod != null ? mTransformationMethod : PasswordTransformationMethod.getInstance());
                    break;
                case TYPE_TEXT:
                    editText.setInputType(InputType.TYPE_CLASS_TEXT);
                    break;
                default:
                    editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            editText.setId(i);
            editText.setEms(1);
            editText.setCursorVisible(mCursorVisible);
            editText.addTextChangedListener(textWatcher);
            addView(editText, i);
        }
    }

    private void check(boolean isInput) {
        if (isInput) {
            if (mBuffer.length() > 0) {
                if (this.mOnInputListener != null) {
                    this.mOnInputListener.onInput(mBuffer.length(), mBuffer.charAt(mBuffer.length() - 1), mBuffer.toString());
                }
            }
        } else {
            if (this.mOnInputListener != null) {
                this.mOnInputListener.onDelete(mBuffer.toString());
            }
        }
        if (mBuffer.length() == mLength) {
            if (this.mOnInputListener != null) {
                this.mOnInputListener.onComplete(mBuffer.toString());
            }
        }
    }

    private void inputAndMoveToNext(String s) {
        if (s != null) {
            mBuffer.append(s);
        }
        if (mBuffer.length() < mLength) {
            EditText editText = (EditText) getChildAt(mBuffer.length());
            editText.requestFocus();
        }
    }

    private void delete() {
        mBuffer.deleteCharAt(mBuffer.length() - 1);
        EditText child = (EditText) getChildAt(mBuffer.length());
        child.setText(null);
    }

    /**
     * 获取当前焦点edittext
     *
     * @return
     */
    private EditText getCurrent() {
        if (mBuffer.length() < mLength) {
            return (EditText) getChildAt(mBuffer.length());
        }
        return (EditText) getChildAt(mLength);
    }

    /**
     * 设置子edittext的background
     */
    private void setChildBg() {
        if (mChildBackground == 0) {
            int count = getChildCount();
            EditText child;
            for (int i = 0; i < count; i++) {
                child = (EditText) getChildAt(i);
                if (child.getText().length() > 0) {
                    child.setBackgroundResource(mFullBackground);
                } else {
                    child.setBackgroundResource(mBlankBackground);
                }
            }
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LinearLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            if (mBuffer.length() != mLength) {
                inputAndMoveToNext(null);
            } else {
                delete();
            }
            showKeyBoard(getCurrent());
            return true;
        }
        return false;
    }

    public void setTransformationMethod(TransformationMethod transformationMethod) {
        mTransformationMethod = transformationMethod;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child instanceof EditText) {
                EditText editText = (EditText) child;
                editText.setTransformationMethod(mTransformationMethod);
            }
        }
    }

    /**
     * 清空输入
     */
    public void clear() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child instanceof EditText) {
                EditText editText = (EditText) child;
                editText.setText(null);
            }
        }
        ((EditText)getChildAt(0)).requestFocus();
    }

    /**
     * 显示软键盘
     *
     * @param view
     */
    private void showKeyBoard(View view) {
        if (view != null) {
            InputMethodManager inputMethodManager = getInputMethodManager();
            if (inputMethodManager != null) {
                view.requestFocus();
                inputMethodManager.showSoftInput(view, 0);
            }
        }
    }

    public void showKeyBoard() {
        showKeyBoard(getCurrent());
    }

    /**
     * 隐藏软键盘
     *
     * @param view
     */
    private void hideKeyBoard(View view) {
        InputMethodManager inputMethodManager = getInputMethodManager();
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void hideKeyBoard() {
        hideKeyBoard(getCurrent());
    }

    public String getInput() {
        return mBuffer.toString();
    }

    private InputMethodManager getInputMethodManager() {
        return (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            this.measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
        if (count > 0) {
            View child = getChildAt(0);
            int cHeight = child.getMeasuredHeight();
            int cWidth = child.getMeasuredWidth();
            int maxH = cHeight + 2 * mChildVPadding;
            int maxW = cWidth * mLength + mChildHPadding * (mLength - 1);
            setMeasuredDimension(resolveSize(maxW, widthMeasureSpec),
                    resolveSize(maxH, heightMeasureSpec));
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);

            child.setVisibility(View.VISIBLE);
            int cWidth = child.getMeasuredWidth();
            int cHeight = child.getMeasuredHeight();
            int cl = (i) * (cWidth + mChildHPadding);
            int cr = cl + cWidth;
            int ct = mChildVPadding;
            int cb = ct + cHeight;
            child.layout(cl, ct, cr, cb);
        }
    }

    public interface OnInputListener {
        /**
         * 输入回调
         *
         * @param position
         * @param c
         * @param content
         */
        void onInput(int position, char c, String content);

        /**
         * 删除回调
         *
         * @param content
         */
        void onDelete(String content);

        /**
         * 输入完成回调
         *
         * @param content
         */
        void onComplete(String content);

    }

    public void setOnInputListener(OnInputListener mOnInputListener) {
        this.mOnInputListener = mOnInputListener;
    }
}
