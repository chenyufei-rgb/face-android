package com.example.project_android.fragment.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project_android.R;
import com.example.project_android.util.NetUtil;
import com.example.project_android.util.ProjectStatic;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ModifyPasswordFragment extends Fragment {

    @BindView(R.id.modify_password_old_password)
    EditText oldPasswordEdit;
    @BindView(R.id.modify_password_new_password)
    EditText newPasswordEdit;

    private String phone = "";
    private String password;
    private int userType = 0;

    private String usertype;

    private onModifySuccessListener successListener;

    public void setSuccessListener(onModifySuccessListener successListener) {
        this.successListener = successListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_modify_password, container, false);
        ButterKnife.bind(this, view);
        usertype = getContext().getSharedPreferences("localRecord", Context.MODE_PRIVATE).getString("userType", "");
        userType =  Integer.parseInt(usertype);
        return view;
    }

    @OnClick({R.id.modify_password_send})
    public void onClicked(View view) {
        Log.d("TAG", getContext().getSharedPreferences("localRecord", Context.MODE_PRIVATE).getAll().toString());
        switch (view.getId()) {
            case R.id.modify_password_send:
                if (oldPasswordEdit.getText().toString().length() < 6) {
                    Toast.makeText(view.getContext(), "密码长度低于6", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (!oldPasswordEdit.getText().toString().equals(newPasswordEdit.getText().toString())) {
                    Toast.makeText(view.getContext(), "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                    break;
                }
                password = newPasswordEdit.getText().toString();
                modifyPassword(view);
                break;
        }
    }

    private void modifyPassword(View view) {
        Map<String, String> map = new HashMap<>();
        map.put(userType == 1 ? "teacherId" : "studentId", getContext().getSharedPreferences("localRecord", Context.MODE_PRIVATE).getString("id", ""));
        map.put("password", password);
        NetUtil.getNetData("account/modify"+(userType == 1 ? "Teacher" : "Student"), map, new Handler(msg -> {
            Toast.makeText(getContext(), "修改成功", Toast.LENGTH_SHORT).show();
            if (msg.what == 1) {
                //跳转页面
                SharedPreferences.Editor localRecord = getContext().getSharedPreferences("localRecord", Context.MODE_PRIVATE).edit();
                localRecord.putString("password", password);
                localRecord.apply();
                if (successListener != null) {
                    successListener.onModifySuccess();
                }
                Intent loginIntent = new Intent(ProjectStatic.MAIN);
                startActivity(loginIntent);
            }else{
                Toast.makeText(getContext(), "修改失败", Toast.LENGTH_SHORT).show();
            }
            return false;
        }));
    }

    public interface onModifySuccessListener {
        void onModifySuccess();
    }
}
