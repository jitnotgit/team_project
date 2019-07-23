package com.c0llabor8.kanban.fragment.sheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import com.c0llabor8.kanban.R;
import com.c0llabor8.kanban.databinding.SheetBottomNavBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;

public class BottomNavigationSheet extends BottomSheetDialogFragment {

  private SheetBottomNavBinding binding;
  private OnNavigationItemSelectedListener itemSelectedListener;
  private ProjectSheetListener listener;

  public static BottomNavigationSheet newInstance() {
    Bundle args = new Bundle();

    BottomNavigationSheet fragment = new BottomNavigationSheet();
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    binding = DataBindingUtil.inflate(
        inflater,
        R.layout.sheet_bottom_nav,
        container,
        false
    );

    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    binding.navigationView.inflateMenu(R.menu.menu_project_main);

    // Once the view is created, pass the project menu to be populated by the listening class
    listener.onPrepareProjectMenu(
        binding.navigationView.getMenu().findItem(R.id.project_list).getSubMenu()
    );

    // Listen for navigation item clicks using the listener from the listening class
    binding.navigationView.setNavigationItemSelectedListener(itemSelectedListener);
  }

  public void setOnNavigationItemSelectedListener(OnNavigationItemSelectedListener listener) {
    itemSelectedListener = listener;
  }

  // Set the class that will be listening to this menu
  public void setProjectNavigationListener(ProjectSheetListener listener) {
    this.listener = listener;
  }

  public interface ProjectSheetListener {
    // Callback to populate projects into the submenu
    void onPrepareProjectMenu(SubMenu subMenu);
  }
}
