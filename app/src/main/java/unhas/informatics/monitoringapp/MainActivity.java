package unhas.informatics.monitoringapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import unhas.informatics.monitoringapp.Preference.SharedPrefManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.container)
    RecyclerView container;
    @BindView(R.id.progress)
    ProgressBar progressBar;

    private SearchView searchView;
    private MainViewModel viewModel;
    private DataListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        progressBar.setVisibility(View.VISIBLE);
        container.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DataListAdapter(getApplicationContext());
        container.setAdapter(adapter);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.requestData();
        viewModel.getData().observe(this, kuitansi -> {
            if (kuitansi != null) {
                progressBar.setVisibility(View.GONE);
                adapter.setData(kuitansi);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        setupSearchView(this);
        return super.onCreateOptionsMenu(menu);
    }

    private void setupSearchView(final MainActivity activity) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                progressBar.setVisibility(View.VISIBLE);
                viewModel.searchData(s);
                viewModel.getData().observe(activity, kuitansi -> {
                    if (kuitansi != null) {
                        progressBar.setVisibility(View.GONE);
                        adapter.setData(kuitansi);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(activity, "Gagal mendapatkan data", Toast.LENGTH_SHORT);
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout_action) {
            new AlertDialog.Builder(this, R.style.AppTheme_Confirm_Dialog)
                    .setTitle("Konfirmasi")
                    .setMessage("Apakah Anda yakin akan keluar?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        SharedPrefManager sp = new SharedPrefManager(getApplicationContext());
                        sp.setPrefBoolean("login_status", false);
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        }
        return super.onOptionsItemSelected(item);
    }
}