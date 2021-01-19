package sb.yoon.kiosk;

import android.content.Context;
import android.os.Bundle;
import sb.yoon.kiosk.model.Menu;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import java.util.List;

import sb.yoon.kiosk.controller.AdminButtonAdapter;

public class admin_gridLayout extends GridLayout {
    private List<Menu> menuList;

    public admin_gridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
