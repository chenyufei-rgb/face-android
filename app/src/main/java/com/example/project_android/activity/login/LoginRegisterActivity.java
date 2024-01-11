package com.example.project_android.activity.login;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.project_android.R;
import com.example.project_android.dialog.LoadingDialog;
import com.example.project_android.entity.AccountStudent;
import com.example.project_android.util.ViewUtils;
import com.example.project_android.util.CommenUtil;
import com.example.project_android.util.NetUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;


@SuppressLint("NonConstantResourceId")
public class LoginRegisterActivity extends AppCompatActivity {
    @BindView(R.id.account_register_name) EditText nameText;
    @BindView(R.id.account_register_account) EditText studentText;
    @BindView(R.id.account_register_password) EditText passwordText;
    @BindView(R.id.account_register_confirm) EditText confirmText;
    @BindView(R.id.account_register_class) EditText classText;
    @BindView(R.id.account_register_phone) EditText phoneText;
    @BindView(R.id.account_register_email) EditText emailText;

    private Boolean sex=true;
    private AccountStudent student;

    private LoadingDialog dialog;

    /**
     * 处理注册请求，成功则跳转到登陆页面
     */
    Handler registerHandler = new Handler(msg -> {
        dialog.setMessage(msg.getData().getString("message"));
        if (msg.what == 1){
            Intent intent = new Intent(this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        finish();
        return true;
    });

    /**
     * 先判断账号是否已存在，账号不存在则进行手机号验证
     */
    Handler handler = new Handler(msg -> {
        String message = msg.getData().getString("message");
        if ("1".equals(message)){
            Toast.makeText(this, "账号已存在", Toast.LENGTH_SHORT).show();
            return false;
        }
        Map<String, String> map = CommenUtil.object2Map(student);
        dialog = new LoadingDialog(this);
        dialog.setTitle("注册账号");
        dialog.show();
        NetUtil.getNetData("account/addStudent",map,registerHandler);
        return false;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        ButterKnife.bind(this);
        ViewUtils.initActionBar(this,"注册");
    }

    @OnClick(R.id.account_register_start)
    public void onRegisterClicked(){
        String name = nameText.getText().toString();
        String account = studentText.getText().toString();
        String major = classText.getText().toString();
        String password = passwordText.getText().toString();
        String phone = phoneText.getText().toString();
        String email = emailText.getText().toString();

        if (!passwordText.getText().toString().equals(confirmText.getText().toString())){
            Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        if (name.isEmpty() || account.isEmpty() ||password.isEmpty()  ||sex == null||
                major.isEmpty() ||phone.isEmpty() ||email.isEmpty() ||
               !CommenUtil.IsEmail(email) || !CommenUtil.isPhone(phone)){
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            return;
        }

        student = new AccountStudent(name,account,password,sex,major,phone,email);
        Map<String, String> map = new HashMap<>();
        map.put("type","2");//true->男，false->女
        map.put("account",student.getAccount());
        //先判断用户是否已经存在，若不存在，则转至手机验证并添加用户
        NetUtil.getNetData("account/confirmAccount",map,handler);
    }

    @OnCheckedChanged({R.id.account_register_male,R.id.account_register_female})
    public void onRadioCheck(CompoundButton view, boolean ischanged) {
        switch (view.getId()) {
            case R.id.account_register_male:
                if (ischanged) {
                    sex = true;
                }
                break;
            case R.id.account_register_female:
                if (ischanged) {
                    sex = false;
                }
                break;
            default:
                break;
        }
    }

}