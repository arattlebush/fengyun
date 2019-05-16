package net.fengyun.italker.italker.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import net.fengyun.italker.common.app.Application;
import net.fengyun.italker.common.app.ToolbarActivity;
import net.fengyun.italker.italker.R;
import net.qiujuer.genius.res.Resource;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author fengyun
 *         账户
 */
public class BankAccountActivity extends ToolbarActivity implements View.OnClickListener{


    @BindView(R.id.txt_balance)
    TextView mBalance;
    @BindView(R.id.txt_currency)
    TextView mCurrency;


    public static void show(Context context) {
        context.startActivity(new Intent(context, BankAccountActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_bank_account;
    }

    @Override
    protected void initData() {
        super.initData();
        mBalance.setText("105.98");
        mCurrency.setText("993");
    }

    @OnClick(R.id.txt_recharge)
    void onRecharge() {
        //充值
        Application.showToast("充值");
    }

    @OnClick(R.id.txt_withdrawals)
    void onWithdrawals() {
        //提现
        Application.showToast("提现");
    }

    @OnClick(R.id.txt_charging_money)
    void onChargingMoney() {
        //充云币
        Application.showToast("充云币");
    }

    @OnClick(R.id.lay_add_bank)
    void onAddBank() {
        //添加银行卡
        Application.showToast("添加银行卡");
    }

    @OnClick(R.id.lay_apply_bank)
    void onApplyBank() {
        //申请信用卡
        Application.showToast("申请信用卡");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.bank_account, menu);

        // 根据状态设置颜色
        Drawable drawable = DrawableCompat.wrap(getResources()
                .getDrawable(R.drawable.ic_more));
        DrawableCompat.setTint(drawable, Resource.Color.WHITE);
        menu.findItem(R.id.action_more).setIcon(drawable);
        return true;
    }

    PopupWindow popupWindow;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_more) {
            //  进行更多操作
             popupWindow = new PopupWindow();
            View view  = LayoutInflater.from(BankAccountActivity.this).inflate(R.layout.more_popup, null);
            view.findViewById(R.id.txt_transaction_record).setOnClickListener(this);
            view.findViewById(R.id.txt_password_manager).setOnClickListener(this);
            view.findViewById(R.id.txt_cancel).setOnClickListener(this);
            popupWindow.setContentView(view);
            popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            //设置SelectPicPopupWindow弹出窗体的高
            popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            //设置SelectPicPopupWindow弹出窗体可点击
            popupWindow.setFocusable(true);
            //设置SelectPicPopupWindow弹出窗体动画效果
            popupWindow.setAnimationStyle(R.style.PopupWindowToTopAnim);
            //实例化一个ColorDrawable颜色为半透明
            ColorDrawable dw = new ColorDrawable(0xb0000000);
            //设置SelectPicPopupWindow弹出窗体的背景
            popupWindow.setBackgroundDrawable(dw);
            popupWindow.showAtLocation(getWindow().getDecorView(),Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_transaction_record:{
                if (popupWindow != null)
                    popupWindow.dismiss();

                Application.showToast("交易记录");
                break;
            }
            case R.id.txt_password_manager:{
                if (popupWindow != null)
                    popupWindow.dismiss();

                Application.showToast("密码管理");
                break;
            }
            case R.id.txt_cancel:{
                if (popupWindow != null)
                    popupWindow.dismiss();
                Application.showToast("取消");
                break;
            }
        }
    }
}
