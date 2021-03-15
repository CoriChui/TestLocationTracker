package com.kaonstudio.testlocationtracker.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kaonstudio.testlocationtracker.R;
import com.kaonstudio.testlocationtracker.cache.coordinates.CoordinatesCacheMapper;
import com.kaonstudio.testlocationtracker.databinding.FragmentHistoryBinding;
import com.kaonstudio.testlocationtracker.domain.TrackDomain;
import com.kaonstudio.testlocationtracker.ui.MainViewModel;
import com.kaonstudio.testlocationtracker.ui.map.DataState;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HistoryFragment extends Fragment implements HistoryAdapter.OnTrackClickListener {

    private HistoryAdapter historyAdapter;
    private FragmentHistoryBinding binding;
    MainViewModel viewModel;
    @Inject
    CoordinatesCacheMapper cacheMapper;
    private boolean isTracking = false;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        setHasOptionsMenu(true);
        initRecyclerView();
        observeTracks();
        observeIsTracking();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        menu.add(0, R.id.menu_history_delete_all, Menu.NONE, getString(R.string.fragment_history_delete_all))
                .setIcon(R.drawable.ic_baseline_delete_sweep_24)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == R.id.menu_history_delete_all) {
            viewModel.deleteAllTracks();
        }
        if (item.getItemId() == android.R.id.home) {
            ((AppCompatActivity) requireActivity()).onSupportNavigateUp();
        }
        return true;
    }

    private void initRecyclerView() {
        historyAdapter = new HistoryAdapter(this);
        binding.fragmentHistoryRv.addItemDecoration(
                new DividerItemDecoration(
                        requireContext(),
                        LinearLayoutManager.VERTICAL
                )
        );
        binding.fragmentHistoryRv.setAdapter(historyAdapter);
    }

    private void observeTracks() {
        viewModel.observeTracks().observe(getViewLifecycleOwner(), listDataState -> {
            if (listDataState instanceof DataState.Success) {
                final List<TrackDomain> tracks = listDataState.getData();
                historyAdapter.setTracks(tracks);
            } else if (listDataState instanceof DataState.Error) {
                historyAdapter.setTracks(new ArrayList<>());
            }
        });
    }

    private void observeIsTracking() {
        viewModel.observeIsTracking().observe(getViewLifecycleOwner(), aBoolean -> {
            isTracking = aBoolean;
        });
    }

    @Override
    public void onTrackClicked(TrackDomain trackDomain) {
        if (!isTracking) {
            viewModel.deleteAllCoordinates();
            viewModel.insertCoordinatesList(cacheMapper.mapToEntityList(trackDomain.coordinates));
            ((AppCompatActivity) requireActivity()).onSupportNavigateUp();
        } else {
            Toast.makeText(requireContext(), getString(R.string.fragment_history_is_tracking), Toast.LENGTH_SHORT).show();
        }
    }
}
