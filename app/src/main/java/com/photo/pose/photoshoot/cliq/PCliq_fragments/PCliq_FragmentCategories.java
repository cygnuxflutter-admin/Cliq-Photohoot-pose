package com.photo.pose.photoshoot.cliq.PCliq_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.photo.pose.photoshoot.cliq.PCliq_adapter.PCliq_AdapterCategories;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIClient;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIInterface;
import com.photo.pose.photoshoot.cliq.PCliq_Activity.PCliq_MainActivity;
import com.photo.pose.photoshoot.cliq.R;
import com.photo.pose.photoshoot.cliq.PCliq_interfaces.PCliq_InterAdListener;
import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemCat;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_ItemCatList;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Constant;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_DBHelper;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Methods;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_RecyclerItemClickListener;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_SharedPref;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ProgressBar;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.AnimationAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PCliq_FragmentCategories extends Fragment {

    private PCliq_DBHelper dbHelper;
    private PCliq_Methods methods;
    private RecyclerView recyclerView;
    private PCliq_AdapterCategories adapterCategories;
    private ArrayList<PCliq_ItemCat> arrayList;
    private ProgressBar progressBar;
    private TextView textView_empty, tvCollectionCount;
    private SearchView searchView;
    private PCliq_SharedPref sharedPref;
    PCliq_APIInterface apiInterface;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pcliq_fragment_categories, container, false);

        apiInterface = PCliq_APIClient.getClient().create(PCliq_APIInterface.class);

        PCliq_InterAdListener interAdListener = new PCliq_InterAdListener() {
            @Override
            public void onClick(int pos, String type) {
                int position = getPosition(adapterCategories.getID(pos));

                PCliq_FragmentSubCategories frag = new PCliq_FragmentSubCategories();
                Bundle bundle = new Bundle();
                bundle.putString("cid", arrayList.get(position).getId());
                bundle.putString("cname", arrayList.get(position).getName());
                bundle.putString("from", "");
                frag.setArguments(bundle);
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
//                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.hide(getParentFragmentManager().findFragmentByTag(getString(R.string.categories)));
                ft.add(R.id.frame_layout, frag, arrayList.get(position).getName());
                ft.addToBackStack(arrayList.get(position).getName());
                ft.commitAllowingStateLoss();
                ((PCliq_MainActivity) getActivity()).getSupportActionBar().setTitle(arrayList.get(position).getName());
            }
        };

        sharedPref = new PCliq_SharedPref(getActivity());
        dbHelper = new PCliq_DBHelper(getActivity());
        methods = new PCliq_Methods(getActivity(), interAdListener);

        arrayList = new ArrayList<>();

        progressBar = rootView.findViewById(R.id.pb_cat);
        textView_empty = rootView.findViewById(R.id.tv_empty_cat);
        tvCollectionCount = rootView.findViewById(R.id.tv_collection_count);
        recyclerView = rootView.findViewById(R.id.rv_cat);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        recyclerView.addOnItemTouchListener(new PCliq_RecyclerItemClickListener(getActivity(), new PCliq_RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                interAdListener.onClick(position, "");
            }
        }));

        View topHeader = rootView.findViewById(R.id.ll_cat_top_header);
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            int statusBarHeight = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.statusBars()).top;
            if (topHeader != null) {
                topHeader.setPadding(topHeader.getPaddingLeft(), statusBarHeight + (int) (12 * getResources().getDisplayMetrics().density), topHeader.getPaddingRight(), topHeader.getPaddingBottom());
            }
            return insets;
        });

        // Setup Mood & Style Filter Chips
        RecyclerView rvCatChips = rootView.findViewById(R.id.rv_cat_chips);
        if (rvCatChips != null) {
            rvCatChips.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            ArrayList<PCliq_ItemCat> moodChips = new ArrayList<>();
            moodChips.add(new PCliq_ItemCat("0", "All", ""));
            moodChips.add(new PCliq_ItemCat("1", "Bride", ""));
            moodChips.add(new PCliq_ItemCat("2", "Couple", ""));
            moodChips.add(new PCliq_ItemCat("3", "Boy", ""));
            moodChips.add(new PCliq_ItemCat("4", "Girl", ""));
            moodChips.add(new PCliq_ItemCat("5", "Father", ""));
            moodChips.add(new PCliq_ItemCat("6", "Mother", ""));

            com.photo.pose.photoshoot.cliq.PCliq_adapter.PCliq_AdapterHomeChips adapterMoodChips = new com.photo.pose.photoshoot.cliq.PCliq_adapter.PCliq_AdapterHomeChips(getContext(), moodChips, (item, position) -> {
                if (adapterCategories != null) {
                    if (position == 0 || item.getId().equals("0")) {
                        adapterCategories.getFilter().filter("");
                    } else {
                        adapterCategories.getFilter().filter(item.getName());
                    }
                }
            });
            rvCatChips.setAdapter(adapterMoodChips);
        }

        // Setup Surprise Category Button
        View btnSurprise = rootView.findViewById(R.id.btn_surprise_category);
        if (btnSurprise != null) {
            btnSurprise.setOnClickListener(v -> {
                if (arrayList != null && !arrayList.isEmpty()) {
                    int randomPos = (int) (Math.random() * arrayList.size());
                    PCliq_ItemCat randomCat = arrayList.get(randomPos);

                    PCliq_FragmentSubCategories frag = new PCliq_FragmentSubCategories();
                    Bundle bundle = new Bundle();
                    bundle.putString("cid", randomCat.getId());
                    bundle.putString("cname", randomCat.getName());
                    bundle.putString("from", "");
                    frag.setArguments(bundle);
                    FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                    ft.hide(getParentFragmentManager().findFragmentByTag(getString(R.string.categories)));
                    ft.add(R.id.frame_layout, frag, randomCat.getName());
                    ft.addToBackStack(randomCat.getName());
                    ft.commitAllowingStateLoss();
                    if (getActivity() instanceof PCliq_MainActivity && ((PCliq_MainActivity) getActivity()).getSupportActionBar() != null) {
                        ((PCliq_MainActivity) getActivity()).getSupportActionBar().setTitle(randomCat.getName());
                    }
                }
            });
        }

        android.widget.EditText etCatSearch = rootView.findViewById(R.id.et_cat_search);
        ImageView ivClearCatSearch = rootView.findViewById(R.id.iv_clear_cat_search);
        if (etCatSearch != null) {
            etCatSearch.addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (ivClearCatSearch != null) {
                        ivClearCatSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                    }
                    if (adapterCategories != null) {
                        adapterCategories.getFilter().filter(s);
                    }
                }

                @Override
                public void afterTextChanged(android.text.Editable s) {}
            });
        }
        if (ivClearCatSearch != null && etCatSearch != null) {
            ivClearCatSearch.setOnClickListener(v -> etCatSearch.setText(""));
        }

        getCategories();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
                menuInflater.inflate(R.menu.pcliq_menu_search, menu);
                menu.findItem(R.id.menu_filter).setVisible(false);
                MenuItem item = menu.findItem(R.id.menu_search);
                item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
                searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
                methods.styleSearchView(searchView);
                searchView.setOnQueryTextListener(queryTextListener);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        super.onViewCreated(view, savedInstanceState);
    }

    private SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return true;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            if (!searchView.isIconified() && adapterCategories != null) {
                adapterCategories.getFilter().filter(s);
                adapterCategories.notifyDataSetChanged();
            }
            return false;
        }
    };

    private void getCategories() {
        if (methods.isNetworkAvailable()) {
            progressBar.setVisibility(View.VISIBLE);

            Call<PCliq_ItemCatList> call = apiInterface.getCategories(methods.getAPIRequest(PCliq_Constant.URL_CATEGORIES, 0, "", "", "", "", "", "", "", "", "", "", "", ""));
            call.enqueue(new Callback<PCliq_ItemCatList>() {
                @Override
                public void onResponse(@NonNull Call<PCliq_ItemCatList> call, @NonNull Response<PCliq_ItemCatList> response) {
                    if(getActivity() != null) {
                        if (response.body() != null && response.body().getArrayListCat() != null) {
                            arrayList.addAll(response.body().getArrayListCat());
                            setAdapter();
                            for (int i = 0; i < response.body().getArrayListCat().size(); i++) {
                                dbHelper.addToCatList(response.body().getArrayListCat().get(i));
                            }
                        } else {
                            setEmpty();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PCliq_ItemCatList> call, @NonNull Throwable t) {
                    call.cancel();
                    setEmpty();
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            arrayList = dbHelper.getCat();
            if (arrayList != null) {

//                int abc = arrayList.lastIndexOf(null);
//                if (((arrayList.size() - (abc + 1)) % new Task_PreferenceClass(getContext()).getInt("rv_count", 4) == 0) ) {
//                    arrayList.add(null);
//                }
                setAdapter();
            }
            progressBar.setVisibility(View.GONE);
        }
    }

    public void setAdapter() {
        adapterCategories = new PCliq_AdapterCategories(getActivity(), arrayList);
        AnimationAdapter adapterAnim = new AlphaInAnimationAdapter(adapterCategories);
        adapterAnim.setFirstOnly(true);
        adapterAnim.setDuration(500);
        adapterAnim.setInterpolator(new OvershootInterpolator(.9f));
        recyclerView.setAdapter(adapterAnim);
        if (tvCollectionCount != null && arrayList != null) {
            tvCollectionCount.setText("✨ " + arrayList.size() + " CURATED COLLECTIONS");
        }
        setEmpty();
    }

    private void setEmpty() {
        if (arrayList.size() == 0) {
            textView_empty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            textView_empty.setVisibility(View.GONE);
        }
    }

    private int getPosition(String id) {
        int count = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            if (id.equals(arrayList.get(i).getId())) {
                count = i;
                break;
            }
        }
        return count;
    }
}
