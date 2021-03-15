package com.kaonstudio.testlocationtracker.ui.map;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kaonstudio.testlocationtracker.R;
import com.kaonstudio.testlocationtracker.domain.CoordinatesDomain;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CustomBottomSheet extends FrameLayout {

    private static final String IS_START_ENABLED = "isStartEnabled";
    private static final String IS_STOP_ENABLED = "isStopEnabled";
    private static final String IS_CLEAR_ENABLED = "isClearEnabled";
    private boolean isOpen = true;
    private ItemCoordinatesAdapter itemCoordinatesAdapter;
    private ObjectAnimator animator;
    private OnClickListener listener;
    private Button btnStart;
    private Button btnStop;
    private Button btnClear;
    private ConstraintLayout cl;
    private SharedPreferences sp;

    public CustomBottomSheet(Context context) {
        super(context);
    }

    public CustomBottomSheet(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        inflate(context, R.layout.custom_bottom_sheet, this);
        itemCoordinatesAdapter = new ItemCoordinatesAdapter();
        RecyclerView rv = findViewById(R.id.bottom_sheet_rv);
        sp = getContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
        rv.setAdapter(itemCoordinatesAdapter);
        TextView title = findViewById(R.id.bottom_sheet_tv);
        title.setOnClickListener(v -> open());
        cl = findViewById(R.id.bottom_sheet_cl);
        btnStart = findViewById(R.id.bottom_sheet_start);
        btnStop = findViewById(R.id.bottom_sheet_stop);
        btnClear = findViewById(R.id.bottom_sheet_clear);
        btnStart.setEnabled(sp.getBoolean(IS_START_ENABLED, true));
        btnStop.setEnabled(sp.getBoolean(IS_STOP_ENABLED, false));
        btnClear.setEnabled(sp.getBoolean(IS_CLEAR_ENABLED, true));
        btnStart.setOnClickListener(v -> {
            listener.onStartClick();
        });
        btnStop.setOnClickListener(v -> {
            listener.onStopClick();
        });
        btnClear.setOnClickListener(v -> {
            listener.onClearClick();
        });
        animator = ObjectAnimator.ofFloat(this, "translationY", getTranslationY(), cl.getMinHeight() - getMinimumHeight());
        animator.setDuration(300);
        animator.setInterpolator(new LinearInterpolator());
    }

    public CustomBottomSheet(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        if (btnStart.isEnabled())
            savedState.isStartButtonEnabled = 1;
        else savedState.isStartButtonEnabled = 0;
        if (btnStop.isEnabled())
            savedState.isStopButtonEnabled = 1;
        else savedState.isStopButtonEnabled = 0;
        if (btnClear.isEnabled())
            savedState.isClearButtonEnabled = 1;
        else savedState.isClearButtonEnabled = 0;
        return savedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            super.onRestoreInstanceState(state);
            SavedState ss = (SavedState) state;
            btnStart.setEnabled(ss.isStartButtonEnabled == 1);
            btnStop.setEnabled(ss.isStopButtonEnabled == 1);
            btnClear.setEnabled(ss.isClearButtonEnabled == 1);
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    private void saveStateToPreferences() {
        sp.edit()
                .putBoolean(IS_START_ENABLED, btnStart.isEnabled())
                .putBoolean(IS_STOP_ENABLED, btnStop.isEnabled())
                .putBoolean(IS_CLEAR_ENABLED, btnClear.isEnabled())
                .apply();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        for (int i = 0, count = getChildCount(); i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChildren(widthMeasureSpec, heightMeasureSpec);
            }
        }
        setMeasuredDimension(
                MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        saveStateToPreferences();
        itemCoordinatesAdapter = null;
    }

    void onStartButtonCallback() {
        btnStart.setEnabled(false);
        btnClear.setEnabled(false);
        btnStop.setEnabled(true);
    }

    void onStopButtonCallback() {
        btnStop.setEnabled(false);
        btnStart.setEnabled(true);
        btnClear.setEnabled(true);
    }

    void setData(List<CoordinatesDomain> list) {
        itemCoordinatesAdapter.setCoordinates(list);
    }

    void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    void open() {
        if (!isOpen) animator.reverse();
        isOpen = true;
    }

    void hide() {
        if (isOpen) animator.start();
        isOpen = false;
    }

    public interface OnClickListener {
        void onStartClick();
        void onStopClick();
        void onClearClick();
    }

    private static class SavedState extends BaseSavedState {
        int isStopButtonEnabled;
        int isStartButtonEnabled;
        int isClearButtonEnabled;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            isStartButtonEnabled = in.readInt();
            isStopButtonEnabled = in.readInt();
            isClearButtonEnabled = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(isStartButtonEnabled);
            out.writeInt(isStopButtonEnabled);
            out.writeInt(isClearButtonEnabled);
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}

class ItemCoordinatesAdapter extends RecyclerView.Adapter<ItemCoordinatesAdapter.ItemHolder> {

    private List<CoordinatesDomain> coordinates = new ArrayList<>();


    @NonNull
    @NotNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coordinates, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ItemHolder holder, int position) {
        holder.bind(coordinates.get(position));
    }

    @Override
    public int getItemCount() {
        return coordinates.size();
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        private final TextView latTv;
        private final TextView longTv;

        public ItemHolder(View view) {
            super(view);
            latTv = view.findViewById(R.id.item_coordinates_lat);
            longTv = view.findViewById(R.id.item_coordinates_long);
        }

        void bind(CoordinatesDomain coordinatesDomain) {
            latTv.setText(
                    itemView.getContext().getString(R.string.item_coordinates_lat, coordinatesDomain.latitude)
            );
            longTv.setText(
                    itemView.getContext().getString(R.string.item_coordinates_long, coordinatesDomain.latitude)
            );
        }

    }

    public void setCoordinates(List<CoordinatesDomain> newCoordinates) {
        final DiffUtil.Callback diffCallback = new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return coordinates.size();
            }

            @Override
            public int getNewListSize() {
                return newCoordinates.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return coordinates.get(oldItemPosition).id == newCoordinates.get(newItemPosition).id;
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return coordinates.get(oldItemPosition).hashCode() == newCoordinates.get(newItemPosition).hashCode();
            }
        };
        final DiffUtil.DiffResult duffResult = DiffUtil.calculateDiff(diffCallback);
        coordinates = newCoordinates;
        duffResult.dispatchUpdatesTo(this);
    }
}
