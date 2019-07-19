package com.c0llabor8.kanban.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import com.c0llabor8.kanban.R;
import com.c0llabor8.kanban.adapter.ProjectPagerAdapter;
import com.c0llabor8.kanban.databinding.ActivityMainBinding;
import com.c0llabor8.kanban.fragment.dialog.NewProjectDialog;
import com.c0llabor8.kanban.fragment.dialog.NewTaskDialog;
import com.c0llabor8.kanban.fragment.sheet.BottomNavigationSheet;
import com.c0llabor8.kanban.model.Project;
import com.c0llabor8.kanban.util.ProjectActivityInterface;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity implements ProjectActivityInterface {

  ActivityMainBinding binding;
  ProjectPagerAdapter pagerAdapter;

  NewTaskDialog newTaskDialog;
  NewProjectDialog newProjectDialog;
  BottomNavigationSheet navFragment;

  SparseArray<Project> projectMenuMap = new SparseArray<>();

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

    // Initialize the pagination of our fragments based off our initial Fragments
    pagerAdapter = new ProjectPagerAdapter(getSupportFragmentManager(), null);

    binding.pager.setAdapter(pagerAdapter);
    binding.tabs.setupWithViewPager(binding.pager, true);

    binding.fab.setOnClickListener(view -> openTaskCreationDialog());

    navFragment = BottomNavigationSheet.newInstance();
    newProjectDialog = NewProjectDialog.newInstance();
    newTaskDialog = NewTaskDialog.newInstance();

    setSupportActionBar(binding.bar);

    loadProjects();
  }

  /*
   * Query for all projects the current user is a member of and add them
   * */
  @Override
  public void loadProjects() {

    Project.queryUserProjects((projects, e) -> {
      if (e != null) {
        e.printStackTrace();
        return;
      }

      projectMenuMap.clear();

      for (int i = 0; i < projects.size(); i++) {
        projectMenuMap.put(Menu.FIRST + i, projects.get(i));
      }
    });
  }

  private void openTaskCreationDialog() {
    newTaskDialog.show(getSupportFragmentManager(), "");
  }

  /*
   * Sets the text for TextView(R.id.bottom_title) within the BottomAppBar
   * @param title text to display in as the title
   * */
  @Override
  public void setTitle(CharSequence title) {
    binding.bottomTitle.setText(title);
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {

    // Show our BottomNavigationSheet when the menu icon is clicked
    if (item.getItemId() == android.R.id.home) {
      navFragment.show(getSupportFragmentManager(), "");
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onAttachFragment(@NonNull Fragment fragment) {
    if (fragment instanceof BottomNavigationSheet) {
      BottomNavigationSheet navFragment = (BottomNavigationSheet) fragment;
      navFragment.setListener(this);
    }

    if (fragment instanceof NewProjectDialog) {
      NewProjectDialog projectDialog = (NewProjectDialog) fragment;
      projectDialog.setListener(this);
    }
  }

  /*
   * Inflate the project menu once the BottomSheet's view is created based off our projectMenuMap
   * (SparseArray)
   * */
  @Override
  public void populateProjects(SubMenu subMenu) {
    for (int i = 0; i < projectMenuMap.size(); i++) {
      int key = projectMenuMap.keyAt(i);
      subMenu.add(Menu.NONE, key, key, projectMenuMap.get(key).getName());
    }
  }

  /*
   * Listener used by the BottomNavSheet to determine which navigation item was selected
   * */
  @Override
  public OnNavigationItemSelectedListener onBottomNavItemSelected() {
    return item -> {

      // If the selected item is the user's personal tasks
      if (item.getItemId() == R.id.my_tasks) {
        setTitle(item.getTitle());
        navFragment.dismiss();
        return true;
      }

      // if the selected item's id is in our HashSet<int(menuID), String(Project)>
      if (projectMenuMap.indexOfKey(item.getItemId()) > -1) {
        setTitle(projectMenuMap.get(item.getItemId()).getName());
        navFragment.dismiss();
        return true;
      }

      if (item.getItemId() == R.id.new_project) {
        newProjectDialog.show(getSupportFragmentManager(), "");
        navFragment.dismiss();
        return true;
      }

      if (item.getItemId() == R.id.action_signout) {
        ParseUser.logOutInBackground(e -> {
          Intent intent = new Intent(MainActivity.this, AuthActivity.class);
          startActivity(intent);
          finish();
        });
      }

      return false;
    };
  }
}
