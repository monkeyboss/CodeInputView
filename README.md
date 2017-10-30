# CodeInputView
[![](https://jitpack.io/v/monkeyboss/CodeInputView.svg)](https://jitpack.io/#monkeyboss/CodeInputView)

验证码输入控件，可灵活定制各种样式

![Image of Yaktocat](https://github.com/monkeyboss/CodeInputView/blob/master/gif/1.0.gif)

# Requirements
CodeInputView supports Android 2.3 (Gingerbread) and later.

# Using CodeInputView in your application
If you are building with Gradle:
>Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:
```
allprojects {
    repositories {
      ...
      maven { url 'https://jitpack.io' }
    }
}
```
>Step 2. Add the dependency
```
dependencies {
    compile 'com.github.monkeyboss:CodeInputView:1.0.1'
}
```
If you are building with Maven:
>Step 1. Add the JitPack repository to your build file
```
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
>Step 2. Add the dependency
```
<dependency>
    <groupId>com.github.monkeyboss</groupId>
    <artifactId>CodeInputView</artifactId>
    <version>1.0.1</version>
</dependency>
```
Use CodeInputView in your layout xml:
```
<pub.monkeyboss.widget.CodeInputView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
```
# CodeInputView支持的属性
 * name="code_length" format="integer"         验证码长度
 * name="child_h_padding" format="dimension"         验证码输入框横向padding
 * name="child_v_padding" format="dimension"         验证码输入框垂直方向padding
 * name="child_width" format="dimension"         验证码输入框宽度
 * name="child_height" format="dimension"         验证码输入框高度
 * name="padding" format="dimension"         验证码输入框padding
 * name="child_background" format="reference"         验证码输入框背景，注：支持设置selector background，实现输入框在获得焦点和失去焦点状态下的background。child_background、full_background、blank_background同时存在时，仅child_background有效
 * name="full_background" format="reference"         验证码输入框有内容时的背景
 * name="blank_background" format="reference"         验证码输入框没有内容时的背景
 * name="cursorVisible" format="boolean"         验证码输入框光标是否可见
 * name="textColor" format="reference"         验证码输入框文字颜色
 * name="inputType" format="enum"         验证码类型，支持number、password、text三种
 
 # Set input listener
 ```
CodeInputView codeInputView = (CodeInputView) findViewById(R.id.codeInputView);
codeInputView.setOnInputListener(new CodeInputView.OnInputListener() {
    @Override
    public void onInput(int position, char c, String content) {
        //监听输入
    }

    @Override
    public void onDelete(String content) {
        //监听删除
    }

    @Override
    public void onComplete(String content) {
        //监听输入完成
    }
});
 ```
