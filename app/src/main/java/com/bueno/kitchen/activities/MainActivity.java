package com.bueno.kitchen.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import com.quantumgraph.sdk.QG;

import com.bueno.kitchen.R;
import com.bueno.kitchen.activities.address.AddressListActivity;
import com.bueno.kitchen.activities.extras.AboutUsActivity;
import com.bueno.kitchen.activities.extras.BuenoCreditsActivity;
import com.bueno.kitchen.activities.extras.ContactUsActivity;
import com.bueno.kitchen.activities.extras.FaqActivity;
import com.bueno.kitchen.activities.extras.LoyalityActivity;
import com.bueno.kitchen.activities.extras.OffersActivity;
import com.bueno.kitchen.activities.extras.ReferActivity;
import com.bueno.kitchen.activities.orders.OrdersListActivity;
import com.bueno.kitchen.activities.prelogin.LoginActivity;
import com.bueno.kitchen.activities.prelogin.WalkthroughActivity;
import com.bueno.kitchen.activities.products.ProductListActivity;
import com.bueno.kitchen.core.BaseActivity;
import com.bueno.kitchen.core.BuenoApplication;
import com.bueno.kitchen.database.AddressOperations;
import com.bueno.kitchen.database.OrderOperations;
import com.bueno.kitchen.managers.analytics.SegmentManager;
import com.bueno.kitchen.models.core.CategoryModel;
import com.bueno.kitchen.models.core.LocalityModel;
import com.bueno.kitchen.models.core.ProductModel;
import com.bueno.kitchen.models.response.LocalitiesResponseModel;
import com.bueno.kitchen.models.response.LoyalityResponseModel;
import com.bueno.kitchen.models.response.ProductListResponseModel;
import com.bueno.kitchen.models.response.location.ConfigResponseModel;
import com.bueno.kitchen.models.response.location.GoogleGeoCodingResponseModel;
import com.bueno.kitchen.models.response.location.GoogleMatrixLocationResponseModel;
import com.bueno.kitchen.models.utils.ProductCategoryModel;
import com.bueno.kitchen.network.services.GoogleService;
import com.bueno.kitchen.utils.Config;
import com.bueno.kitchen.utils.RoundedTransformation;
import com.bueno.kitchen.views.CheckoutShelfView;
import com.bueno.kitchen.views.adapter.CategoryGridAdapter;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.rampo.updatechecker.UpdateChecker;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.intercom.android.sdk.Intercom;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.FuncN;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,
        CategoryGridAdapter.OnClickListener,
        CheckoutShelfView.CheckoutShelfViewInteractionListener,
        NavigationView.OnNavigationItemSelectedListener {
    private static final String ERROR_LOCATION = "ERROR_LOCATION";
    private static final String ERROR_LOCATION_TRAFFIC = "ERROR_LOCATION_TRAFFIC";
    private static final String ERROR_MIN_VERSION = "ERROR_MIN_VERSION";
    private static final int REQUEST_ADDRESS = 101;
    private static final int REQUEST_LOGIN = 103;
    private static final int REQUEST_PLACE_PICKER = 104;
    private static final int REQUEST_WALKTHROUGH_SCREEN = 105;
    @Bind(R.id.products_list_recycler_view) public RecyclerView productsRecyclerView;
    @Bind(R.id.product_list_swipe_layout) public SwipeRefreshLayout productsSwipeRefreshLayout;
    @Bind(R.id.location_text_view) public TextView locationTextView;
    @Bind(R.id.locality_info_text_view) public TextView localityInfoTextView;
    @Bind(R.id.checkout_container) public CheckoutShelfView checkoutContainer;
    @Bind(R.id.drawer_layout) public DrawerLayout drawerLayout;
    @Bind(R.id.version_text) public TextView versionTextView;
    @Bind(R.id.toolbar)
    public Toolbar toolbar;
    @Bind(R.id.navigation_view)
    public NavigationView navigationView;
    @Bind(R.id.loading_switcher)
    public ViewFlipper loadingViewSwitcher;
    @Bind(R.id.app_bar_layout)
    View localityView;
    @Bind(R.id.error_text)
    public TextView errorTexView;
    @Bind(R.id.error_button)
    public TextView errorButton;
    @Inject
    public AddressOperations addressOperations;
    @Inject
    public OrderOperations orderOperations;
    @Inject
    public GoogleService googleService;
    private CategoryGridAdapter mAdapter;
    private int count = 0;
    private Subscription subscription;
    private ErrorType errorType;
    private ProductCategoryModel productCategoryModel;


    private Func1<ArrayList<ProductModel>, ArrayList<ProductModel>> mapOrders = new Func1<ArrayList<ProductModel>, ArrayList<ProductModel>>() {
        @Override
        public ArrayList<ProductModel> call(ArrayList<ProductModel> productModels) {
            ArrayList<ProductModel> cartProductModels = preferenceManager.getTempOrder();

            for (ProductModel productModel : productModels) {
                productModel.selectedQuantity = (cartProductModels != null && cartProductModels.contains(productModel)) ? cartProductModels.get(cartProductModels.indexOf(productModel)).selectedQuantity : 0;
            }
            return productModels;
        }
    };


    private enum ErrorType {
        LOCATION, OTHER, VERSION
    }



    private View.OnClickListener didClickSignOut = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle)
                    .setTitle("Sign out")
                    .setMessage("Are you sure you want to sign out?")
                    .setPositiveButton("Sign out", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            performLogout();
                        }
                    })
                    .setNegativeButton("Dismiss", null)
                    .setCancelable(false);
            AppCompatDialog dialog = builder.create();
            dialog.show();
        }
    };










    private View.OnClickListener didClickSignIn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            drawerLayout.closeDrawers();
            startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), REQUEST_LOGIN);
        }
    };











    private Func1<ProductListResponseModel, Observable<ProductCategoryModel>> mapProductCategories = new Func1<ProductListResponseModel, Observable<ProductCategoryModel>>() {
        @Override
        public Observable<ProductCategoryModel> call(final ProductListResponseModel productListResponseModel) {
            return Observable.create(new Observable.OnSubscribe<ProductCategoryModel>() {
                @Override
                public void call(Subscriber<? super ProductCategoryModel> subscriber) {
                    if (!productListResponseModel.products.isEmpty()) {
                        ProductCategoryModel productCategoryModel = new ProductCategoryModel();
                        ArrayList<ProductModel> cartProductModels = preferenceManager.getTempOrder();
                        for (ProductModel productModel : productListResponseModel.products) {

                            //Update selected quantity
                            productModel.selectedQuantity = (cartProductModels != null && cartProductModels.contains(productModel)) ? cartProductModels.get(cartProductModels.indexOf(productModel)).selectedQuantity : 0;

                            //Collect today Special
                            if (productModel.todaySpecial && productModel.getProductStatus() == ProductModel.ProductStatus.Active)
                                productCategoryModel.todaysSpecial.add(productModel);

                            //Group Categories
                            if (productModel.catId != null && productModel.catName != null) {
                                CategoryModel categoryModel = productModel.getCategoryById(productCategoryModel.categoryModels);
                                if (categoryModel == null) {
                                    categoryModel = new CategoryModel();
                                    categoryModel.catId = productModel.catId;
                                    categoryModel.catName = productModel.catName;
                                    categoryModel.catImage = productModel.image;
                                    categoryModel.productModels = new ArrayList<>();
                                    productCategoryModel.categoryModels.add(categoryModel);
                                }
                                categoryModel.productModels.add(productModel);
                            }
                        }

                        MainActivity.this.productCategoryModel = productCategoryModel;
                        if (!productCategoryModel.categoryModels.isEmpty()) {
                            subscriber.onNext(productCategoryModel);
                            return;
                        }
                    }
                    subscriber.onError(new Throwable(preferenceManager.getConfiguration().noDishesErrorMessage));
                }
            });
        }
    };













    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        BuenoApplication.getApp().getApplicationComponents().inject(this);
        setSupportActionBar(toolbar);
        performRedirection();
    }








    private void performRedirection() {
        if (!preferenceManager.isWalkthroughSeen()){
            startActivityForResult(new Intent(this, WalkthroughActivity.class), REQUEST_WALKTHROUGH_SCREEN);
        }else if (!preferenceManager.isLocalityPicked()){
            localityView.performClick();
        }else {
            checkUpdate();
        }
    }





    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setUpNavigationDrawer();
        setUpProductList();
    }






    private void setUpCart() {
        ArrayList<ProductModel> ordersList = preferenceManager.getTempOrder();
        checkoutContainer.attachListener(this);
        checkoutContainer.updateCart(ordersList);

        if (mAdapter != null && productCategoryModel != null && !productCategoryModel.todaysSpecial.isEmpty()) {
            Observable.just(productCategoryModel.todaysSpecial)
                    .map(mapOrders)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<ArrayList<ProductModel>>() {
                                   @Override
                                   public void call(ArrayList<ProductModel> productModels) {
                                       mAdapter.updateTodaySpecial(productCategoryModel.todaysSpecial);
                                   }
                               },
                            new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                }
                            });
        }
    }






    @Override
    protected void onResume() {
        super.onResume();
        setUpCart();
        updateNavigationMenu();

        if (loadingViewSwitcher.getDisplayedChild() == 0
                && (subscription == null || subscription.isUnsubscribed())) {
            fetchProductsList();
        }
    }





    private void setUpNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener(this);
        updateNavigationMenu();
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.string_blank,
                R.string.string_blank) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        try {
            versionTextView.setText(String.format("v%s", getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName));
        } catch (PackageManager.NameNotFoundException e) {
        }

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void updateNavigationMenu() {
        View headerView = navigationView.getHeaderView(0);
        TextView signOutView = (TextView) headerView.findViewById(R.id.logout_text);
        ImageView userImage = (ImageView) headerView.findViewById(R.id.user_image);
        TextView userName = (TextView) headerView.findViewById(R.id.username_text);
        TextView membershipTextView = (TextView) headerView.findViewById(R.id.membership_text);
        boolean isLoggedIn = preferenceManager.isLoggedIn();
        if (isLoggedIn) {
            int imgSide = utilitySingleton.dpToPx(this, 55);
            signOutView.setOnClickListener(didClickSignOut);
            userName.setText(preferenceManager.getUserName());
            Picasso.with(this)
                    .load(preferenceManager.getUserImage())
                    .placeholder(R.drawable.ic_account_circle_white_48dp)
                    .resize(imgSide, imgSide)
                    .transform(new RoundedTransformation(imgSide, 0))
                    .into(userImage);
            signOutView.setText("Sign out");

            if (preferenceManager.getLoyalityModel() != null
                    && !TextUtils.isEmpty(preferenceManager.getLoyalityModel().membership)) {
                membershipTextView.setVisibility(View.VISIBLE);
                membershipTextView.setText(preferenceManager.getLoyalityModel().membership);
            } else {
                membershipTextView.setVisibility(View.GONE);
            }
        } else {
            signOutView.setText("Sign in");
            signOutView.setOnClickListener(didClickSignIn);
            userImage.setImageResource(R.drawable.ic_account_circle_white_48dp);
            membershipTextView.setVisibility(View.GONE);
            userName.setText("Welcome Guest!");
        }

        navigationView.getMenu().findItem(R.id.order_history).setVisible(isLoggedIn);
        navigationView.getMenu().findItem(R.id.refer).setVisible(isLoggedIn);
        navigationView.getMenu().findItem(R.id.credits).setVisible(isLoggedIn);
    }

    private void checkUpdate() {
        UpdateChecker checker = new UpdateChecker(this);
        checker.start();
    }

    private void setUpProductList() {
        mAdapter = new CategoryGridAdapter(this, this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mAdapter.getItemViewType(position)) {
                    case CategoryGridAdapter.TYPE_HEADER:
                        return 2;
                    case CategoryGridAdapter.TYPE_CATEGORY:
                        return 1;
                    default:
                        return -1;
                }
            }
        });
        productsRecyclerView.setLayoutManager(layoutManager);
        productsRecyclerView.setAdapter(mAdapter);
        productsRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                if (parent.getChildLayoutPosition(view) == 0)
                    outRect.set(0, utilitySingleton.dpToPx(MainActivity.this, 4), 0, 0);
            }
        }, 0);
        productsSwipeRefreshLayout.setRefreshing(true);
        productsSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void updateLocation() {
        localityInfoTextView.setText(Html.fromHtml("<font color=#" + Integer.toHexString(ContextCompat.getColor(this, R.color.colorPrimary)) + ">Min. Order:</font> " + preferenceManager.getLocality().minOrderAmount + "<font color=#" + Integer.toHexString(ContextCompat.getColor(this, R.color.colorPrimary)) + ">, Avg. Delivery Time:</font> " + preferenceManager.getLocality().averageDeliveryTime + " Mins"));
        locationTextView.setText(preferenceManager.getLocality().geoAddress);
    }

    @OnClick({R.id.app_bar_layout, R.id.error_button})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.app_bar_layout:
                loadPlacePicker();
                break;
            case R.id.error_button:
                switch (errorType) {
                    case LOCATION:
                        loadPlacePicker();
                        break;
                    case VERSION:
                        goToPlayStore();
                        break;
                    case OTHER:
                        loadingViewSwitcher.setDisplayedChild(0);
                        fetchProductsList();
                        break;
                }
                break;
        }
    }

    private void goToPlayStore() {
        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://details?id=" + getPackageName())));
    }

    private void loadPlacePicker() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), REQUEST_PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Toast.makeText(this, "Google play services not available!!!", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ADDRESS:
                    startActivity(new Intent(this, AddressListActivity.class));
                    break;
                case REQUEST_LOGIN:
                    updateNavigationMenu();
                    break;
                case REQUEST_PLACE_PICKER:
                    Place place = PlacePicker.getPlace(this, data);
                    loadingViewSwitcher.setDisplayedChild(0);
                    fetchProductsList(String.valueOf(place.getLatLng().latitude),
                            String.valueOf(place.getLatLng().longitude),
                            place.getAddress().toString());
                    break;
                case REQUEST_WALKTHROUGH_SCREEN:
                    preferenceManager.walkthroughSeen();
                    performRedirection();
                    break;
            }
        } else if ((requestCode == REQUEST_PLACE_PICKER && !preferenceManager.isLocalityPicked())
                || requestCode == REQUEST_WALKTHROUGH_SCREEN) {
            finish();
        }
    }

    private void fetchProductsList() {
        fetchProductsList(null, null, null);
    }

    private void fetchProductsList(String lat, String lng, final String address) {
        final LocalityModel localityModel;
        if (!TextUtils.isEmpty(lat) && !TextUtils.isEmpty(lng)) {
            localityModel = LocalityModel.Builder()
                    .setLatitude(lat)
                    .setLongitude(lng)
                    .setName(address)
                    .build();
            preferenceManager.deleteTempOrder();
        } else {
            localityModel = preferenceManager.getLocality();
        }

        if (localityModel != null) {
            subscription = restService.getLocationConfig()
                    .zipWith(restService.getLoyalityData(preferenceManager.getMobileNumber()),
                            new Func2<ConfigResponseModel, LoyalityResponseModel, ConfigResponseModel>() {
                                @Override
                                public ConfigResponseModel call(ConfigResponseModel configResponseModel, LoyalityResponseModel loyalityResponseModel) {
                                    preferenceManager.saveLoyalityModel(loyalityResponseModel);
                                    return configResponseModel;
                                }
                            })
                    .flatMap(new Func1<ConfigResponseModel, Observable<LocalityModel>>() {
                        @Override
                        public Observable<LocalityModel> call(ConfigResponseModel configResponseModel) {
                            preferenceManager.saveConfiguration(configResponseModel);
                            long verCode = Long.MAX_VALUE;
                            PackageInfo pInfo;
                            try {
                                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                                verCode = pInfo.versionCode;
                            } catch (PackageManager.NameNotFoundException ignore) {
                            }

                            if (configResponseModel.maxOrderItems < ProductModel.getQuantity(preferenceManager.getTempOrder())) {
                                preferenceManager.deleteTempOrder();
                            }
                            if (configResponseModel.isOrderingClosed) {
                                return Observable.error(new Throwable(configResponseModel.orderingClosedMessage));
                            }
                            else if(configResponseModel.minAndroidVersion>verCode)
                                return Observable.error(new Throwable(ERROR_MIN_VERSION));
                            else
                                return provideLocalityProcessorObserver(localityModel, configResponseModel);
                        }
                    }).flatMap(new Func1<LocalityModel, Observable<ProductListResponseModel>>() {
                        @Override
                        public Observable<ProductListResponseModel> call(LocalityModel localityModel) {
                            if (localityModel != null) {
                                if (localityModel.isLocationError()) {
                                    return Observable.error(new Throwable(ERROR_LOCATION_TRAFFIC));
                                } else {
                                    preferenceManager.localityPicked(localityModel);
                                    return restService.getProductList(localityModel.id);
                                }
                            } else return Observable.error(new Throwable(ERROR_LOCATION));
                        }
                    })
                    .flatMap(mapProductCategories)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Action1<ProductCategoryModel>() {
                        @Override
                        public void call(ProductCategoryModel productCategoryModel) {
                            productsSwipeRefreshLayout.setRefreshing(false);
                            mAdapter.addCategories(productCategoryModel.categoryModels);
                            mAdapter.updateTodaySpecial(productCategoryModel.todaysSpecial);
                            updateLocation();
                            setUpCart();
                            updateNavigationMenu();
                            loadingViewSwitcher.setDisplayedChild(1);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            if (throwable instanceof UnknownHostException) {
                                updateErrorMessage(ErrorType.OTHER, null, getString(R.string.error_network));
                            } else {
                                ErrorType errorType = ErrorType.OTHER;
                                if (throwable.getMessage().equals(ERROR_LOCATION) || throwable.getMessage().equals(ERROR_LOCATION_TRAFFIC))
                                    errorType = ErrorType.LOCATION ;
                                else if (throwable.getMessage().equals(ERROR_MIN_VERSION))
                                    errorType = ErrorType.VERSION ;
                                updateErrorMessage(errorType,
                                        throwable.getMessage().equals(ERROR_LOCATION_TRAFFIC) ? getString(R.string.location_error_text_2) : null,
                                        throwable.getMessage());
                            }
                            productsSwipeRefreshLayout.setRefreshing(false);
                        }
                    });
        } else {
            updateErrorMessage(ErrorType.OTHER, null, null);
            productsSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void updateErrorMessage(ErrorType errorType, String message, String rawMessage) {
        if (this.errorType == null || this.errorType != errorType) {
            switch (errorType) {
                case LOCATION:
                    errorTexView.setText(TextUtils.isEmpty(message) ? getString(R.string.location_error_text) : message);
                    errorButton.setText("Pick Location");
                    errorButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_location_on_white_24dp), null, null, null);
                    break;
                case VERSION:
                    errorTexView.setText(TextUtils.isEmpty(message) ? getString(R.string.location_error_text_version) : message);
                    errorButton.setText("Update Application");
                    errorButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_stat_play_store), null, null, null);
                    break;
                case OTHER:
                    errorTexView.setText(TextUtils.isEmpty(rawMessage) ? getString(R.string.other_error_text) : rawMessage);
                    errorButton.setText("Refresh");
                    errorButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_refresh_white_24dp), null, null, null);
                    break;
            }
        }
        this.errorType = errorType;
        loadingViewSwitcher.setDisplayedChild(2);
    }

    private void performLogout() {
        drawerLayout.closeDrawers();
        preferenceManager.logOut();
        addressOperations.truncateTable();
        orderOperations.truncateTable();
        productsSwipeRefreshLayout.setRefreshing(true);
        fetchProductsList();
        updateNavigationMenu();
        setUpCart();
        Intercom.client().reset();
        Intercom.client().registerUnidentifiedUser();

        SegmentManager.with(this)
                .setName("logout")
                .build(SegmentManager.EventType.TRACK);
    }

    @Override
    public void onBackPressed() {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            if (count > 0) {
                super.onBackPressed();
                System.exit(0);
            } else {
                count++;
                utilitySingleton.ShowToast("Press again to exit.");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        count = 0;
                    }
                }, 2000);
            }
        } else {
            drawerLayout.closeDrawers();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //QGraph
        QG qg = QG.getInstance(getApplicationContext());
        qg.onStart(getString(R.string.qgraph_key));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (subscription != null) {
            subscription.unsubscribe();
        }
        productsSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        fetchProductsList();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        menuItem.setChecked(!menuItem.isChecked());
        drawerLayout.closeDrawers();
        switch (menuItem.getItemId()) {
            case R.id.contact_us:
                startActivity(new Intent(this, ContactUsActivity.class));
                break;
            case R.id.rate_us:
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://details?id=" + getPackageName())));
                break;
            case R.id.blog:
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://bueno.kitchen/blog/")));
                break;
            case R.id.faq_policies:
                startActivity(new Intent(this, FaqActivity.class));
                break;
            case R.id.about_us:
                startActivity(new Intent(this, AboutUsActivity.class));
                break;
            case R.id.order_history:
                if (preferenceManager.isLoggedIn()) {
                    startActivity(new Intent(this, OrdersListActivity.class));
                } else {
                    startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_LOGIN);
                }
                break;
            case R.id.offer:
                startActivity(new Intent(this, OffersActivity.class));
                break;
            case R.id.credits:
                if (preferenceManager.isLoggedIn()) {
                    startActivity(new Intent(this, BuenoCreditsActivity.class));
                } else {
                    startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_LOGIN);
                }
                break;
            case R.id.refer:
                if (preferenceManager.isLoggedIn()) {
                    startActivity(new Intent(this, ReferActivity.class));
                } else {
                    startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_LOGIN);
                }
                break;
            case R.id.loyality:
                startActivity(new Intent(this, LoyalityActivity.class));
                break;
        }
        return false;
    }

    @Override
    public void performCheckout() {
        if (!preferenceManager.isLoggedIn()) {
            startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_ADDRESS);
        } else {
            startActivity(new Intent(this, AddressListActivity.class));
        }
    }

    @Override
    public void onClickItem(CategoryModel categoryModel) {
        Intent intent = new Intent(this, ProductListActivity.class);
        intent.putParcelableArrayListExtra(Config.Intents.INTENT_PRODUCT_LIST, categoryModel.productModels);
        startActivity(intent);
    }

    private Observable<LocalityModel> provideLocalityProcessorObserver(final LocalityModel prefLocalityModel,
                                                                       final ConfigResponseModel configResponseModel) {
        if (!configResponseModel.kitchenLocations.isEmpty()) {
            ArrayList<Observable<GoogleMatrixLocationResponseModel>> googleMatrixObservables = new ArrayList<>();
            for (ConfigResponseModel.KitchenLocation kitchenLocation : configResponseModel.kitchenLocations) {
                googleMatrixObservables.add(googleService.getDirections(String.format("%s,%s", kitchenLocation.latitude, kitchenLocation.longitude),
                        String.format("%s,%s", prefLocalityModel.latitude, prefLocalityModel.longitude),
                        getString(R.string.google_server_key)));
            }

            return Observable.zip(googleMatrixObservables, new FuncN<LocalityModel>() {
                @Override
                public LocalityModel call(Object... args) {
                    boolean isTraffic = false;
                    for (Object object : args) {
                        if (object instanceof GoogleMatrixLocationResponseModel) {
                            GoogleMatrixLocationResponseModel googleMatrixLocationResponseModel = (GoogleMatrixLocationResponseModel) object;
                            if (googleMatrixLocationResponseModel.isSuccess()) {
                                if (googleMatrixLocationResponseModel.getDistance() <= configResponseModel.maxTravelDistance
                                        || googleMatrixLocationResponseModel.getDuration() <= configResponseModel.maxTravelTime) {
                                    try {
                                        return provideLocality(prefLocalityModel, configResponseModel);
                                    } catch (IOException e) {
                                        return null;
                                    }
                                }
                                isTraffic = true;
                            }
                        }
                    }
                    return isTraffic ? LocalityModel.Builder()
                            .setLocationError(getString(R.string.location_error_text_2))
                            .build() : null;
                }
            });
        } else {
            return Observable.error(new Throwable(ERROR_LOCATION));
        }
    }

    private LocalityModel provideLocality(final LocalityModel localityModel, final ConfigResponseModel configResponseModel) throws IOException {
        if (localityModel != null && configResponseModel != null) {
            Response<LocalitiesResponseModel> localityRequest = restService.getLocalities().execute();
            if (localityRequest.isSuccessful()) {
                LocalitiesResponseModel localitiesResponseModel = localityRequest.body();
                if (localitiesResponseModel != null && !localitiesResponseModel.localities.isEmpty()) {
                    for (LocalityModel localityModelTemp : localitiesResponseModel.localities) {
                        localityModelTemp.calculateDistance(localityModel.getLocation());
                    }

                    int minDistanceIndex = localitiesResponseModel.localities.indexOf(Collections.min(localitiesResponseModel.localities, new Comparator<LocalityModel>() {
                        @Override
                        public int compare(LocalityModel lhs, LocalityModel rhs) {
                            return (int) (lhs.distance - rhs.distance);
                        }
                    }));

                    if (minDistanceIndex >= 0 && localitiesResponseModel.localities.get(minDistanceIndex).distance <= configResponseModel.maxDistanceFromNearestLocality) {
                        LocalityModel serviceLocalityModel = localitiesResponseModel.localities.get(minDistanceIndex);
                        serviceLocalityModel.latitude = localityModel.latitude;
                        serviceLocalityModel.longitude = localityModel.longitude;

                        GoogleGeoCodingResponseModel googleGeoCodingResponseModel = googleService.getAddress(String.format("%s,%s", localityModel.latitude, localityModel.longitude),
                                getString(R.string.google_server_key)).execute().body();
                        String address = googleGeoCodingResponseModel.calculateAddress();
                        serviceLocalityModel.geoAddress = TextUtils.isEmpty(address) ? localityModel.geoAddress : address;
                        return serviceLocalityModel;
                    }
                }
            }
            return null;
        } else {
            return null;
        }
    }
}
