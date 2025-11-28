package com.salah.times;

import android.os.Bundle;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AdhkarActivity extends AppCompatActivity {
    
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton fabAdd;
    private AdhkarListFragment morningFragment;
    private AdhkarListFragment eveningFragment;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ThemeManager.applyTheme();
        setContentView(R.layout.activity_adhkar);
        
        if (getSupportActionBar() != null) {
            String currentLang = TranslationManager.getCurrentLanguage();
            String titleText = currentLang.equals("ar") ? "أذكار" :
                              currentLang.equals("es") ? "Adhkar" :
                              currentLang.equals("fr") ? "Adhkar" : "Adhkar";
            getSupportActionBar().setTitle(titleText);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        initViews();
        setupViewPager();
        setupFab();
    }
    
    private void initViews() {
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        fabAdd = findViewById(R.id.fab_add);
    }
    
    private void setupViewPager() {
        morningFragment = AdhkarListFragment.newInstance("morning");
        eveningFragment = AdhkarListFragment.newInstance("evening");
        
        FragmentStateAdapter adapter = new FragmentStateAdapter(this) {
            @Override
            public Fragment createFragment(int position) {
                return position == 0 ? morningFragment : eveningFragment;
            }
            
            @Override
            public int getItemCount() {
                return 2;
            }
        };
        
        viewPager.setAdapter(adapter);
        
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            String currentLang = TranslationManager.getCurrentLanguage();
            if (position == 0) {
                String morningText = currentLang.equals("ar") ? "الصباح" : 
                                   currentLang.equals("es") ? "Mañana" :
                                   currentLang.equals("fr") ? "Matin" : "Morning";
                tab.setText("🌅 " + morningText);
            } else {
                String eveningText = currentLang.equals("ar") ? "المساء" :
                                    currentLang.equals("es") ? "Tarde" :
                                    currentLang.equals("fr") ? "Soir" : "Evening";
                tab.setText("🌙 " + eveningText);
            }
        }).attach();
    }
    
    private void setupFab() {
        fabAdd.setOnClickListener(v -> showAddAdhkarDialog());
    }
    
    private void showAddAdhkarDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        android.view.View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_adhkar, null);
        builder.setView(dialogView);
        
        // Set translated texts
        String currentLang = TranslationManager.getCurrentLanguage();
        
        String titleText = currentLang.equals("ar") ? "إضافة ذكر جديد" :
                          currentLang.equals("es") ? "Agregar Nuevo Adhkar" :
                          currentLang.equals("fr") ? "Ajouter Nouvel Adhkar" : "Add New Adhkar";
        
        String hintText = currentLang.equals("ar") ? "نص الذكر" :
                         currentLang.equals("es") ? "Texto del Adhkar" :
                         currentLang.equals("fr") ? "Texte Adhkar" : "Adhkar Text";
        
        String cancelText = TranslationManager.tr("cancel");
        String saveText = TranslationManager.tr("save");
        
        ((android.widget.TextView) dialogView.findViewById(R.id.dialog_title)).setText(titleText);
        ((com.google.android.material.textfield.TextInputLayout) dialogView.findViewById(R.id.text_input_layout)).setHint(hintText);
        ((android.widget.Button) dialogView.findViewById(R.id.btn_cancel)).setText(cancelText);
        ((android.widget.Button) dialogView.findViewById(R.id.btn_save)).setText(saveText);
        
        EditText editText = dialogView.findViewById(R.id.edit_adhkar_text);
        
        android.app.AlertDialog dialog = builder.create();
        
        dialogView.findViewById(R.id.btn_cancel).setOnClickListener(v -> dialog.dismiss());
        
        dialogView.findViewById(R.id.btn_save).setOnClickListener(v -> {
            String text = editText.getText().toString().trim();
            if (!text.isEmpty()) {
                String currentType = viewPager.getCurrentItem() == 0 ? "morning" : "evening";
                AdhkarManager.addAdhkar(currentType, text);
                
                // Refresh current fragment
                if (currentType.equals("morning")) {
                    morningFragment.refreshList();
                } else {
                    eveningFragment.refreshList();
                }
                
                dialog.dismiss();
            }
        });
        
        dialog.show();
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}