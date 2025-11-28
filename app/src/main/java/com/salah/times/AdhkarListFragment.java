package com.salah.times;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import java.util.List;

public class AdhkarListFragment extends Fragment implements AdhkarAdapter.OnItemActionListener {
    
    private static final String ARG_TYPE = "type";
    private String type;
    private RecyclerView recyclerView;
    private AdhkarAdapter adapter;
    private List<AdhkarItem> adhkarList;
    
    public static AdhkarListFragment newInstance(String type) {
        AdhkarListFragment fragment = new AdhkarListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString(ARG_TYPE);
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adhkar_list, container, false);
        
        recyclerView = view.findViewById(R.id.recycler_view);
        setupRecyclerView();
        loadAdhkarList();
        
        return view;
    }
    
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                adapter.moveItem(fromPosition, toPosition);
                return true;
            }
            
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Not used
            }
        });
        
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
    
    private void loadAdhkarList() {
        adhkarList = AdhkarManager.getAdhkarList(type);
        adapter = new AdhkarAdapter(adhkarList, type, this);
        recyclerView.setAdapter(adapter);
    }
    
    public void refreshList() {
        adhkarList = AdhkarManager.getAdhkarList(type);
        adapter.updateList(adhkarList);
    }
    
    @Override
    public void onDeleteItem(int id) {
        AdhkarManager.deleteAdhkar(type, id);
        refreshList();
    }
    
    @Override
    public void onItemMoved(int fromPosition, int toPosition) {
        AdhkarManager.saveAdhkarList(type, adhkarList);
    }
}