package pub.monkeyboss.codeinputview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

    private int length;
    private int childWidth = 120;
    private int childHeight = 120;
    private int childHPadding = 14;
    private int childVPadding = 14;
    private int inputType = TYPE_NUMBER;
    private int childBackground;
    private int fullBackground;
    private int blankBackground;
    private boolean cursorVisible;
    private int textColor = Color.parseColor("#333333");
    private OnInputListener onInputListener;

    private StringBuffer buffer = new StringBuffer();

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
        length = typedArray.getInt(R.styleable.codeInputView_code_length, 4);
        childHPadding = (int) typedArray.getDimension(R.styleable.codeInputView_child_h_padding, 0);
        childVPadding = (int) typedArray.getDimension(R.styleable.codeInputView_child_v_padding, 0);
        childBackground = typedArray.getResourceId(R.styleable.codeInputView_child_background, 0);
        fullBackground = typedArray.getResourceId(R.styleable.codeInputView_full_background, 0);
        blankBackground = typedArray.getResourceId(R.styleable.codeInputView_blank_background, 0);
        cursorVisible = typedArray.getBoolean(R.styleable.codeInputView_cursorVisible, true);
        textColor = typedArray.getColor(R.styleable.codeInputView_textColor, textColor);
        inputType = typedArray.getInt(R.styleable.codeInputView_inputType, TYPE_NUMBER);
        childWidth = (int) typedArray.getDimension(R.styleable.codeInputView_child_width, childWidth);
        childHeight = (int) typedArray.getDimension(R.styleable.codeInputView_child_height, childHeight);
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
                if (s.length() == 1 && buffer.length() < length) {
                    input(s.toString());
                    setChildBg();
                    check(true);
                } else if (s.length() == 0) {
                    input(null);
                    setChildBg();
                    check(false);
                }
            }
        };

        OnKeyListener onKeyListener = new OnKeyListener() {
            @Override
            public synchronized boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_UP) {
                    if (buffer.length() > 0) {
                        delete();
                    }
                }
                return false;
            }
        };

        for (int i = 0; i < length; i++) {
            EditText editText = new EditText(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(childWidth, childHeight);
            layoutParams.bottomMargin = childVPadding;
            layoutParams.topMargin = childVPadding;
            layoutParams.leftMargin = childHPadding;
            layoutParams.rightMargin = childHPadding;
            layoutParams.gravity = Gravity.CENTER;


            editText.setOnKeyListener(onKeyListener);
            editText.setBackgroundResource(childBackground > 0 ? childBackground : blankBackground);
            editText.setTextColor(textColor);
            editText.setLayoutParams(layoutParams);
            editText.setGravity(Gravity.CENTER);
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});

            switch (inputType) {
                case TYPE_NUMBER:
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;
                case TYPE_PASSWORD:
                    editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    break;
                case TYPE_TEXT:
                    editText.setInputType(InputType.TYPE_CLASS_TEXT);
                    break;
                default:
                    editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            editText.setId(i);
            editText.setEms(1);
            editText.setCursorVisible(cursorVisible);
            editText.addTextChangedListener(textWatcher);
            addView(editText, i);
        }
    }

    private void check(boolean isInput) {
        if (isInput) {
            if (buffer.length() > 0) {
                if (this.onInputListener != null) {
                    this.onInputListener.onInput(buffer.length(), buffer.charAt(buffer.length() - 1), buffer.toString());
                }
            }
        } else {
            if (this.onInputListener != null) {
                this.onInputListener.onDelete(buffer.toString());
            }
        }
        if (buffer.length() == length) {
            if (this.onInputListener != null) {
                this.onInputListener.onComplete(buffer.toString());
            }
        }
    }

    private void input(String s) {
        if (s != null)
            buffer.append(s);
        if (buffer.length() < length) {
            EditText editText = (EditText) getChildAt(buffer.length());
            editText.requestFocus();
        }
    }

    private void delete() {
        buffer.deleteCharAt(buffer.length() - 1);
        EditText child = (EditText) getChildAt(buffer.length());
        child.setText(null);
    }

    /**
     * 设置子edittext的background
     */
    private void setChildBg() {
        if (childBackground == 0) {
            int count = getChildCount();
            EditText child;
            for (int i = 0; i < count; i++) {
                child = (EditText) getChildAt(i);
                if (child.getText().length() > 0) {
                    child.setBackgroundResource(fullBackground);
                } else {
                    child.setBackgroundResource(blankBackground);
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
        if (buffer.length() != length)
            input(null);
        else delete();
        return true;
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
            int maxH = cHeight + 2 * childVPadding;
            int maxW = (cWidth + childHPadding) * length + childHPadding;
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
            int cl = (i) * (cWidth + childHPadding);
            int cr = cl + cWidth;
            int ct = childVPadding;
            int cb = ct + cHeight;
            child.layout(cl, ct, cr, cb);
        }


    }

    public interface OnInputListener {
        void onInput(int position, char c, String content);

        void onDelete(String content);

        void onComplete(String content);

    }

    public void setOnInputListener(OnInputListener onInputListener) {
        this.onInputListener = onInputListener;
    }
}
