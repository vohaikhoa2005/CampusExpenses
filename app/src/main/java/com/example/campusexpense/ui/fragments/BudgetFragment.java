package com.example.campusexpense.ui.fragments;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusexpense.R;
import com.example.campusexpense.data.database.AppDatabase;
import com.example.campusexpense.data.database.BudgetDao;
import com.example.campusexpense.data.database.CategoryDao;
import com.example.campusexpense.data.model.Budget;
import com.example.campusexpense.data.model.Category;
import com.example.campusexpense.ui.budget.BudgetRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class BudgetFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private BudgetRecyclerAdapter adapter;
    private List<Budget> budgetList;
    private List<Category> categoryList;
    private List<String> categoryNames;
    private BudgetDao budgetDao;
    private CategoryDao categoryDao;
    private TextView emptyView;
    private SharedPreferences sharedPreferences;
    private int currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        fabAdd = view.findViewById(R.id.fabAdd);
        emptyView = view.findViewById(R.id.emptyView);
        sharedPreferences = requireContext().getSharedPreferences("UserSession", 0);
        currentUserId = sharedPreferences.getInt("userId", -1);
        AppDatabase database = AppDatabase.getInstance(requireContext());
        budgetDao = database.budgetDao();
        categoryDao = database.categoryDao();
        budgetList = new ArrayList<>();
        categoryList = new ArrayList<>();
        categoryNames = new ArrayList<>();

        adapter = new BudgetRecyclerAdapter(budgetList, categoryNames,
                this::showEditDialog, this::showDeleteDialog);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
        fabAdd.setOnClickListener(v -> showAddDialog());
        refreshList();
        return view;


    }

    private  void refreshList() {
        budgetList.clear();
        budgetList.addAll(budgetDao.getAllBuggetsByUser(currentUserId));
        categoryNames.clear();
        for (Budget budget : budgetList) {
            Category category = categoryDao.getById(budget.getCategoryId());
            categoryNames.add(category != null ? category.getName() : "Unknown");
        }
        adapter.notifyDataSetChanged();
        if (budgetList.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }


    }

    private void showAddDialog() {
        categoryList.clear();
        categoryList.addAll(categoryDao.getAll());

        if (categoryList.isEmpty()) {
            Toast.makeText(requireContext(), "Please add categories first", Toast.LENGTH_SHORT).show();
            return;

        }
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_budget, null);
        Spinner categorySpinner = dialogView.findViewById(R.id.categorySpinner);
        TextInputEditText amountInput = dialogView.findViewById(R.id.amountInput);
        Spinner periodSpinner = dialogView.findViewById(R.id.periodSpinner);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        List<String> categoryNameList = new ArrayList<>();
        for (Category cat : categoryList) {
            categoryNameList.add(cat.getName());
        }

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categoryNameList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        String[] periods = {"Monthly", "Weekly"};
        ArrayAdapter<String> periodAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, periods);
        periodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        periodSpinner.setAdapter(periodAdapter);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();


        saveButton.setOnClickListener(v -> {
            int categoryPosition = categorySpinner.getSelectedItemPosition();
            String amountText = amountInput.getText().toString().trim();
            int periodPosition = periodSpinner.getSelectedItemPosition();
            double amount = Double.parseDouble(amountText);
            Category selectedCategory = categoryList.get(categoryPosition);
            String period = periods[periodPosition];
            Budget exitstingBudget = budgetDao.getBudgetByCategoryAndUser(currentUserId, selectedCategory.getId());
            if (exitstingBudget != null) {
                Toast.makeText(requireContext(), "Budget for this category already exists", Toast.LENGTH_SHORT).show();
                return;
            }
            Budget budget = new Budget(currentUserId, selectedCategory.getId(), amount, period);
            budgetDao.insert(budget);
            refreshList();
            dialog.dismiss();
            Toast.makeText(requireContext(), "Budget added successfully", Toast.LENGTH_SHORT).show();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();




    }


    private void showEditDialog(Budget budget) {

    }

    private void showDeleteDialog(Budget budget) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Budget")
                .setMessage("Are you sure you want to delete this budget?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    budgetDao.delete(budget);
                    refreshList();
                    Toast.makeText(requireContext(), "Budget deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


}
