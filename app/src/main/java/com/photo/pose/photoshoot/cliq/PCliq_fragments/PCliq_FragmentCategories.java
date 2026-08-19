package com.photo.pose.photoshoot.cliq.PCliq_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
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
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
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
    private CircularProgressBar progressBar;
    private TextView textView_empty;
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
        recyclerView = rootView.findViewById(R.id.rv_cat);
//        GridLayoutManager grid = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        recyclerView.addOnItemTouchListener(new PCliq_RecyclerItemClickListener(getActivity(), new PCliq_RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                methods.showInter(position, "");
            }
        }));

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