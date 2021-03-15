package com.kaonstudio.testlocationtracker.ui.map;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.kaonstudio.testlocationtracker.R;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.Objects;

public class SaveToDatabaseDialog extends DialogFragment {

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        final View view = getLayoutInflater().inflate(R.layout.dialog_save_to_db, null);

        final Button saveBtn = view.findViewById(R.id.dialog_save_to_db_save);
        final Button cancelBtn = view.findViewById(R.id.dialog_save_to_db_cancel);
        final EditText input = view.findViewById(R.id.dialog_save_to_db_input);
        WeakReference<DialogInterface.OnDismissListener> weakDismissListener = new WeakReference<>(dialog -> {

        });

        saveBtn.setOnClickListener(v -> {
            if (!input.getText().toString().trim().isEmpty()) {
                Objects.requireNonNull(NavHostFragment.findNavController(this)
                        .getPreviousBackStackEntry())
                        .getSavedStateHandle()
                        .set(MapFragment.SAVE_TO_DATABASE_KEY, input.getText().toString().trim());
                dismiss();
            }
        });
        cancelBtn.setOnClickListener(v -> {
            dismiss();
        });

        return new MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .setOnDismissListener(weakDismissListener.get())
                .create();
    }
}
