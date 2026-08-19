package com.photo.pose.photoshoot.cliq.PCliq_utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemAbout;
import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemCat;
import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemColors;
import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemSubCat;
import com.photo.pose.photoshoot.cliq.PCliq_items.PCliq_ItemPose;

import java.util.ArrayList;

public class PCliq_DBHelper extends SQLiteOpenHelper {

    PCliq_EncryptData encryptData;
    static String DB_NAME = "hdwallpaper.db";
    SQLiteDatabase db;
    final Context context;

    static String TAG_ID = "id";

    private static final String TABLE_ABOUT = "about";
    public static final String TABLE_CAT = "cat";
    public static final String TABLE_SUB_CAT = "sub_cat";
    public static final String TABLE_WALL_BY_LATEST = "wallpaper";
    private static final String TABLE_COLOR = "colors";

    private static final String TAG_IMAGE_BIG = "img";
    private static final String TAG_WALL_ID = "pid";
    private static final String TAG_COLOR_ID = "cid";
    private static final String TAG_HEX = "hex";
//    private static final String TAG_COLOR_NAME = "cname";
    private static final String TAG_TITLE = "title";
    private static final String TAG_CAMERA_SETTING = "camerasetting";
    private static final String TAG_POSE_TIPS = "posetips";
    private static final String TAG_VIEWS = "views";
    private static final String TAG_AVG_RATE = "avg_rate";
    private static final String TAG_TOTAL_DOWNLOAD = "total_download";
    private static final String TAG_TAGS = "tags";
    private static final String TAG_TYPE = "type";
    private static final String TAG_IS_LIVE_WALLPAPER = "is_live_wall";
    private static final String TAG_IS_RECENT = "is_recent";
    private static final String TAG_IS_FEATURED = "is_featured";
    private static final String TAG_IS_FAV = "is_fav";
    private static final String TAG_CAT_ID = "cid";
    private static final String TAG_SUB_CAT_ID = "sub_cat_id";
    private static final String TAG_CAT_NAME = "cname";
    private static final String TAG_CAT_TOT_WALL = "tot_wall";

    private static final String TAG_ABOUT_NAME = "name";
    private static final String TAG_ABOUT_LOGO = "logo";
    private static final String TAG_ABOUT_VERSION = "version";
    private static final String TAG_ABOUT_AUTHOR = "author";
    private static final String TAG_ABOUT_CONTACT = "contact";
    private static final String TAG_ABOUT_EMAIL = "email";
    private static final String TAG_ABOUT_WEBSITE = "website";
    private static final String TAG_ABOUT_DESC = "description";
    private static final String TAG_ABOUT_PRIVACY = "privacy";
    private static final String TAG_ABOUT_PUB_ID = "ad_pub";
    private static final String TAG_ABOUT_BANNER_ID = "ad_banner";
    private static final String TAG_ABOUT_INTER_ID = "ad_inter";
    private static final String TAG_ABOUT_IS_BANNER = "isbanner";
    private static final String TAG_ABOUT_IS_INTER = "isinter";
    private static final String TAG_ABOUT_IS_PORTRAIT = "isportrait";
    private static final String TAG_ABOUT_IS_LANDSCAPE = "islandscape";
    private static final String TAG_ABOUT_IS_SQUARE = "issquare";
    private static final String TAG_ABOUT_CLICK = "click";

    String[] columns_wall = new String[]{TAG_ID, TAG_WALL_ID, TAG_TITLE, TAG_POSE_TIPS, TAG_CAMERA_SETTING, TAG_CAT_ID, TAG_SUB_CAT_ID, TAG_IMAGE_BIG, TAG_VIEWS,
            TAG_AVG_RATE, TAG_TOTAL_DOWNLOAD, TAG_TAGS, TAG_TYPE, TAG_IS_FAV, TAG_IS_LIVE_WALLPAPER, TAG_IS_RECENT, TAG_IS_FEATURED};

    String[] columns_cat = new String[]{TAG_ID, TAG_CAT_ID, TAG_CAT_NAME, TAG_IMAGE_BIG, TAG_CAT_TOT_WALL};

    String[] columns_sub_cat = new String[]{TAG_ID, TAG_SUB_CAT_ID, TAG_CAT_ID, TAG_CAT_NAME, TAG_IMAGE_BIG, TAG_CAT_TOT_WALL};

    String[] columns_colors = new String[]{TAG_ID, TAG_COLOR_ID, TAG_HEX};

    String[] columns_about = new String[]{TAG_ABOUT_NAME, TAG_ABOUT_LOGO, TAG_ABOUT_VERSION, TAG_ABOUT_AUTHOR, TAG_ABOUT_CONTACT,
            TAG_ABOUT_EMAIL, TAG_ABOUT_WEBSITE, TAG_ABOUT_DESC, TAG_ABOUT_PRIVACY, TAG_ABOUT_PUB_ID,
            TAG_ABOUT_BANNER_ID, TAG_ABOUT_INTER_ID, TAG_ABOUT_IS_BANNER, TAG_ABOUT_IS_INTER, TAG_ABOUT_CLICK, TAG_ABOUT_IS_PORTRAIT, TAG_ABOUT_IS_LANDSCAPE, TAG_ABOUT_IS_SQUARE};


    // Creating table about
    private static final String CREATE_TABLE_ABOUT = "create table " + TABLE_ABOUT + "(" + TAG_ABOUT_NAME
            + " TEXT, " + TAG_ABOUT_LOGO + " TEXT, " + TAG_ABOUT_VERSION + " TEXT, " + TAG_ABOUT_AUTHOR + " TEXT" +
            ", " + TAG_ABOUT_CONTACT + " TEXT, " + TAG_ABOUT_EMAIL + " TEXT, " + TAG_ABOUT_WEBSITE + " TEXT, " + TAG_ABOUT_DESC + " TEXT" +
            ", " + TAG_ABOUT_PRIVACY + " TEXT, " + TAG_ABOUT_PUB_ID + " TEXT, " + TAG_ABOUT_BANNER_ID + " TEXT" +
            ", " + TAG_ABOUT_INTER_ID + " TEXT, " + TAG_ABOUT_IS_BANNER + " TEXT, " + TAG_ABOUT_IS_INTER + " TEXT, " + TAG_ABOUT_IS_PORTRAIT + " TEXT, " + TAG_ABOUT_IS_LANDSCAPE + " TEXT, " + TAG_ABOUT_IS_SQUARE + " TEXT, " + TAG_ABOUT_CLICK + " TEXT);";

    // Creating table query
    private static final String CREATE_TABLE_CAT = "create table " + TABLE_CAT + "(" +
            TAG_ID + " integer PRIMARY KEY AUTOINCREMENT," +
            TAG_CAT_ID + " TEXT," +
            TAG_CAT_NAME + " TEXT," +
            TAG_IMAGE_BIG + " TEXT," +
            TAG_CAT_TOT_WALL + " TEXT);";
    // Creating table query
    private static final String CREATE_TABLE_SUB_CAT = "create table " + TABLE_SUB_CAT + "(" +
            TAG_ID + " integer PRIMARY KEY AUTOINCREMENT," +
            TAG_SUB_CAT_ID + " TEXT," +
            TAG_CAT_ID + " TEXT," +
            TAG_CAT_NAME + " TEXT," +
            TAG_IMAGE_BIG + " TEXT," +
            TAG_CAT_TOT_WALL + " TEXT);";

    // Creating table query
    private static final String CREATE_TABLE_COLORS = "create table " + TABLE_COLOR + "(" +
            TAG_ID + " integer PRIMARY KEY AUTOINCREMENT," +
            TAG_COLOR_ID + " TEXT," +
            TAG_HEX + " TEXT);";

    // Creating table query
    private static final String CREATE_TABLE_WALL_BY_LATEST = "create table " + TABLE_WALL_BY_LATEST + "(" +
            TAG_ID + " integer PRIMARY KEY AUTOINCREMENT," +
            TAG_WALL_ID + " TEXT UNIQUE," +
            TAG_TITLE + " TEXT," +
            TAG_CAMERA_SETTING + " TEXT," +
            TAG_POSE_TIPS + " TEXT," +
            TAG_CAT_ID + " TEXT," +
            TAG_SUB_CAT_ID + " TEXT," +
            TAG_IMAGE_BIG + " TEXT," +
            TAG_VIEWS + " NUMERIC," +
            TAG_AVG_RATE + " TEXT," +
            TAG_TOTAL_DOWNLOAD + " TEXT," +
            TAG_TAGS + " TEXT," +
            TAG_TYPE + " TEXT," +
            TAG_IS_FAV + " TEXT," +
            TAG_IS_LIVE_WALLPAPER + " TEXT," +
            TAG_IS_RECENT + " TEXT," +
            TAG_IS_FEATURED + " TEXT);";

    public PCliq_DBHelper(Context context) {
        super(context, DB_NAME, null, 5);
        this.context = context;
        encryptData = new PCliq_EncryptData(context);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_ABOUT);
            db.execSQL(CREATE_TABLE_CAT);
            db.execSQL(CREATE_TABLE_SUB_CAT);
            db.execSQL(CREATE_TABLE_COLORS);
            db.execSQL(CREATE_TABLE_WALL_BY_LATEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("Range")
    public ArrayList<PCliq_ItemPose> getWallpapers(String filter, String wallType) {
        ArrayList<PCliq_ItemPose> arrayList = new ArrayList<>();

        String query = "";
        switch (filter) {
            case "":
                query = null;
                break;
            case "id":
                query = TAG_ID + " DESC";
                break;
            case "views":
                query = TAG_VIEWS + " DESC";
                break;
            case "rate":
                query = TAG_AVG_RATE + " DESC";
                break;
            case "download":
                query = TAG_TOTAL_DOWNLOAD + " DESC";
                break;
        }

        Cursor cursor;
        if (wallType.equals("")) {
            cursor = db.query(TABLE_WALL_BY_LATEST, columns_wall, null, null, null, null, query);
        } else {
            cursor = db.query(TABLE_WALL_BY_LATEST, columns_wall, TAG_TYPE + "='" + wallType + "'", null, null, null, query);
        }

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String pid = cursor.getString(cursor.getColumnIndex(TAG_WALL_ID));
                String title = cursor.getString(cursor.getColumnIndex(TAG_TITLE));
                String camerasetting = cursor.getString(cursor.getColumnIndex(TAG_CAMERA_SETTING));
                String posetips = cursor.getString(cursor.getColumnIndex(TAG_POSE_TIPS));
                Log.e("TAGGGG", "getWallpapers: 1" + posetips);

                String img = encryptData.decrypt(cursor.getString(cursor.getColumnIndex(TAG_IMAGE_BIG)));

                String views = String.valueOf(cursor.getInt(cursor.getColumnIndex(TAG_VIEWS)));
                String averagerate = cursor.getString(cursor.getColumnIndex(TAG_AVG_RATE));
                String download = cursor.getString(cursor.getColumnIndex(TAG_TOTAL_DOWNLOAD));
                String tags = cursor.getString(cursor.getColumnIndex(TAG_TAGS));
                String type = cursor.getString(cursor.getColumnIndex(TAG_TYPE));
                boolean fav = cursor.getString(cursor.getColumnIndex(TAG_IS_FAV)).equals("1");

                PCliq_ItemPose itemWallpaper = new PCliq_ItemPose(pid, title, camerasetting, posetips, img, views, averagerate, download, tags, type, fav);
                arrayList.add(itemWallpaper);

                cursor.moveToNext();
            }
            cursor.close();
        }

        return arrayList;
    }

    @SuppressLint("Range")
    public String getRecentWallpapersID(String wallType) {

        String where;
        String[] args;
        if (wallType.equals("")) {
            where = TAG_IS_RECENT + "=?";
            args = new String[]{"1"};
        } else {
            where = TAG_IS_RECENT + "=? AND " + TAG_TYPE + "=?";
            args = new String[]{"1", wallType};
        }

        Cursor cursor;
        if (wallType.equals("")) {
            cursor = db.query(TABLE_WALL_BY_LATEST, columns_wall, where, args, null, null, null, "30");
        } else {
            cursor = db.query(TABLE_WALL_BY_LATEST, columns_wall, where, args, null, null, null, "30");
        }

        String id = "";
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getString(cursor.getColumnIndex(TAG_WALL_ID));
            cursor.moveToNext();
            for (int i = 1; i < cursor.getCount(); i++) {
                id = id + "," + cursor.getString(cursor.getColumnIndex(TAG_WALL_ID));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return id;
    }

    @SuppressLint("Range")
    public ArrayList<PCliq_ItemPose> getWallByCat(String id, String wallType) {
        ArrayList<PCliq_ItemPose> arrayList = new ArrayList<>();

        String where;
        String[] args;
        if (wallType.equals("")) {
            where = TAG_CAT_ID + "=?";
            args = new String[]{id};
        } else {
            where = TAG_CAT_ID + "=? AND " + TAG_TYPE + "=?";
            args = new String[]{id, wallType};
        }

        Cursor cursor = db.query(TABLE_WALL_BY_LATEST, columns_wall, where, args, null, null, TAG_ID + " DESC");

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String pid = cursor.getString(cursor.getColumnIndex(TAG_WALL_ID));
                String title = cursor.getString(cursor.getColumnIndex(TAG_TITLE));
                String camerasetting = cursor.getString(cursor.getColumnIndex(TAG_CAMERA_SETTING));
                String posetips = cursor.getString(cursor.getColumnIndex(TAG_POSE_TIPS));
                Log.e("TAGGGG", "getWallpapers: 2" + camerasetting);
                String img = encryptData.decrypt(cursor.getString(cursor.getColumnIndex(TAG_IMAGE_BIG)));

                String views = String.valueOf(cursor.getInt(cursor.getColumnIndex(TAG_VIEWS)));
                String averagerate = cursor.getString(cursor.getColumnIndex(TAG_AVG_RATE));
                String download = cursor.getString(cursor.getColumnIndex(TAG_TOTAL_DOWNLOAD));
                String tags = cursor.getString(cursor.getColumnIndex(TAG_TAGS));
                String type = cursor.getString(cursor.getColumnIndex(TAG_TYPE));
                boolean fav = cursor.getString(cursor.getColumnIndex(TAG_IS_FAV)).equals("1");

                PCliq_ItemPose itemWallpaper = new PCliq_ItemPose(pid, title, posetips, camerasetting, img, views, averagerate, download, tags, type, fav);
                arrayList.add(itemWallpaper);

                cursor.moveToNext();
            }
            cursor.close();
        }
        return arrayList;
    }

    @SuppressLint("Range")
    public ArrayList<PCliq_ItemPose> getWallBySubCat(String id, String wallType) {
        ArrayList<PCliq_ItemPose> arrayList = new ArrayList<>();

        String where;
        String[] args;
        if (wallType.equals("")) {
            where = TAG_SUB_CAT_ID + "=?";
            args = new String[]{id};
        } else {
            where = TAG_SUB_CAT_ID + "=? AND " + TAG_TYPE + "=?";
            args = new String[]{id, wallType};
        }

        Cursor cursor = db.query(TABLE_WALL_BY_LATEST, columns_wall, where, args, null, null, TAG_ID + " DESC");

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String pid = cursor.getString(cursor.getColumnIndex(TAG_WALL_ID));
                String title = cursor.getString(cursor.getColumnIndex(TAG_TITLE));
                String camerasetting = cursor.getString(cursor.getColumnIndex(TAG_CAMERA_SETTING));
                String posetips = cursor.getString(cursor.getColumnIndex(TAG_POSE_TIPS));
                Log.e("TAGGGG", "getWallpapers: 3" + camerasetting);
                String img = encryptData.decrypt(cursor.getString(cursor.getColumnIndex(TAG_IMAGE_BIG)));

                String views = String.valueOf(cursor.getInt(cursor.getColumnIndex(TAG_VIEWS)));
                String averagerate = cursor.getString(cursor.getColumnIndex(TAG_AVG_RATE));
                String download = cursor.getString(cursor.getColumnIndex(TAG_TOTAL_DOWNLOAD));
                String tags = cursor.getString(cursor.getColumnIndex(TAG_TAGS));
                String type = cursor.getString(cursor.getColumnIndex(TAG_TYPE));
                boolean fav = cursor.getString(cursor.getColumnIndex(TAG_IS_FAV)).equals("1");

                PCliq_ItemPose itemWallpaper = new PCliq_ItemPose(pid, title, posetips, camerasetting, img, views, averagerate, download, tags, type, fav);
                arrayList.add(itemWallpaper);

                cursor.moveToNext();
            }
            cursor.close();
        }
        return arrayList;
    }

    @SuppressLint("Range")
    public ArrayList<PCliq_ItemPose> getWallByFeatured(String id, String wallType) {
        ArrayList<PCliq_ItemPose> arrayList = new ArrayList<>();

        String where;
        String[] args;
        if (wallType.equals("")) {
            where = TAG_IS_FEATURED + "=?";
            args = new String[]{id};
        } else {
            where = TAG_IS_FEATURED + "=? AND " + TAG_TYPE + "=?";
            args = new String[]{id, wallType};
        }

        Cursor cursor = db.query(TABLE_WALL_BY_LATEST, columns_wall, where, args, null, null, TAG_ID + " DESC");

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String pid = cursor.getString(cursor.getColumnIndex(TAG_WALL_ID));
                String title = cursor.getString(cursor.getColumnIndex(TAG_TITLE));
                String camerasetting = cursor.getString(cursor.getColumnIndex(TAG_CAMERA_SETTING));
                String posetips = cursor.getString(cursor.getColumnIndex(TAG_POSE_TIPS));
                Log.e("TAGGGG", "getWallpapers: 4" + camerasetting);
                String img = encryptData.decrypt(cursor.getString(cursor.getColumnIndex(TAG_IMAGE_BIG)));

                String views = String.valueOf(cursor.getInt(cursor.getColumnIndex(TAG_VIEWS)));
                String averagerate = cursor.getString(cursor.getColumnIndex(TAG_AVG_RATE));
                String download = cursor.getString(cursor.getColumnIndex(TAG_TOTAL_DOWNLOAD));
                String tags = cursor.getString(cursor.getColumnIndex(TAG_TAGS));
                String type = cursor.getString(cursor.getColumnIndex(TAG_TYPE));
                boolean fav = cursor.getString(cursor.getColumnIndex(TAG_IS_FAV)).equals("1");

                PCliq_ItemPose itemWallpaper = new PCliq_ItemPose(pid, title, posetips, camerasetting, img, views, averagerate, download, tags, type, fav);
                arrayList.add(itemWallpaper);

                cursor.moveToNext();
            }
            cursor.close();
        }
        return arrayList;
    }

    @SuppressLint("Range")
    public ArrayList<PCliq_ItemPose> getLiveWallpapers(String id) {
        ArrayList<PCliq_ItemPose> arrayList = new ArrayList<>();

        String where = TAG_IS_LIVE_WALLPAPER + "=?";
        ;
        String[] args = new String[]{id};

        Cursor cursor = db.query(TABLE_WALL_BY_LATEST, columns_wall, where, args, null, null, TAG_ID + " DESC");

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String pid = cursor.getString(cursor.getColumnIndex(TAG_WALL_ID));
                String title = cursor.getString(cursor.getColumnIndex(TAG_TITLE));
                String camerasetting = cursor.getString(cursor.getColumnIndex(TAG_CAMERA_SETTING));
                String posetips = cursor.getString(cursor.getColumnIndex(TAG_POSE_TIPS));
                Log.e("TAGGGG", "getWallpapers: 5" + camerasetting);
                String img = encryptData.decrypt(cursor.getString(cursor.getColumnIndex(TAG_IMAGE_BIG)));

                String views = String.valueOf(cursor.getInt(cursor.getColumnIndex(TAG_VIEWS)));
                String averagerate = cursor.getString(cursor.getColumnIndex(TAG_AVG_RATE));
                String download = cursor.getString(cursor.getColumnIndex(TAG_TOTAL_DOWNLOAD));
                String tags = cursor.getString(cursor.getColumnIndex(TAG_TAGS));
                String type = cursor.getString(cursor.getColumnIndex(TAG_TYPE));
                boolean fav = cursor.getString(cursor.getColumnIndex(TAG_IS_FAV)).equals("1");

                PCliq_ItemPose itemWallpaper = new PCliq_ItemPose(pid, title, posetips, camerasetting, img, views, averagerate, download, tags, type, fav);
                arrayList.add(itemWallpaper);

                cursor.moveToNext();
            }
            cursor.close();
        }
        return arrayList;
    }

    @SuppressLint("Range")
    public ArrayList<PCliq_ItemPose> getWallByRecent(String id, String wallType) {
        ArrayList<PCliq_ItemPose> arrayList = new ArrayList<>();

        String where;
        String[] args;
        if (wallType.equals("")) {
            where = TAG_IS_RECENT + "=?";
            args = new String[]{id};
        } else {
            where = TAG_IS_RECENT + "=? AND " + TAG_TYPE + "=?";
            args = new String[]{id, wallType};
        }

        Cursor cursor = db.query(TABLE_WALL_BY_LATEST, columns_wall, where, args, null, null, TAG_ID + " DESC", "30");

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String pid = cursor.getString(cursor.getColumnIndex(TAG_WALL_ID));
                String title = cursor.getString(cursor.getColumnIndex(TAG_TITLE));
                String camerasetting = cursor.getString(cursor.getColumnIndex(TAG_CAMERA_SETTING));
                String posetips = cursor.getString(cursor.getColumnIndex(TAG_POSE_TIPS));
                Log.e("TAGGGG", "getWallpapers: 6" + camerasetting);
                String img = encryptData.decrypt(cursor.getString(cursor.getColumnIndex(TAG_IMAGE_BIG)));

                String views = String.valueOf(cursor.getInt(cursor.getColumnIndex(TAG_VIEWS)));
                String averagerate = cursor.getString(cursor.getColumnIndex(TAG_AVG_RATE));
                String download = cursor.getString(cursor.getColumnIndex(TAG_TOTAL_DOWNLOAD));
                String tags = cursor.getString(cursor.getColumnIndex(TAG_TAGS));
                String type = cursor.getString(cursor.getColumnIndex(TAG_TYPE));
                boolean fav = cursor.getString(cursor.getColumnIndex(TAG_IS_FAV)).equals("1");

                PCliq_ItemPose itemWallpaper = new PCliq_ItemPose(pid, title, posetips, camerasetting, img, views, averagerate, download, tags, type, fav);
                arrayList.add(itemWallpaper);

                cursor.moveToNext();
            }
            cursor.close();
        }
        return arrayList;
    }

    @SuppressLint("Range")
    public ArrayList<PCliq_ItemCat> getCat() {
        ArrayList<PCliq_ItemCat> arrayList = new ArrayList<>();

        Cursor cursor = db.query(TABLE_CAT, columns_cat, null, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String cid = cursor.getString(cursor.getColumnIndex(TAG_CAT_ID));
                String cname = cursor.getString(cursor.getColumnIndex(TAG_CAT_NAME));

                String img = encryptData.decrypt(cursor.getString(cursor.getColumnIndex(TAG_IMAGE_BIG)));

                PCliq_ItemCat itemCat = new PCliq_ItemCat(cid, cname, img);
                arrayList.add(itemCat);

                cursor.moveToNext();
            }
            cursor.close();
        }

        return arrayList;
    }

    @SuppressLint("Range")
    public ArrayList<PCliq_ItemSubCat> getSubCat(String catId) {
        ArrayList<PCliq_ItemSubCat> arrayList = new ArrayList<>();

        String where = TAG_CAT_ID + "=?";
        String[] args = new String[]{catId};


        Cursor cursor = db.query(TABLE_SUB_CAT, columns_sub_cat, where, args, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String cid = cursor.getString(cursor.getColumnIndex(TAG_SUB_CAT_ID));
                String cname = cursor.getString(cursor.getColumnIndex(TAG_CAT_NAME));

                String img = encryptData.decrypt(cursor.getString(cursor.getColumnIndex(TAG_IMAGE_BIG)));

                String total_wall = cursor.getString(cursor.getColumnIndex(TAG_CAT_TOT_WALL));

                PCliq_ItemSubCat itemCat = new PCliq_ItemSubCat(cid, cname, img, total_wall);
                arrayList.add(itemCat);

                cursor.moveToNext();
            }
            cursor.close();
        }

        return arrayList;
    }

    @SuppressLint("Range")
    public ArrayList<PCliq_ItemColors> getColors() {
        ArrayList<PCliq_ItemColors> arrayList = new ArrayList<>();

        Cursor cursor = db.query(TABLE_COLOR, columns_colors, null, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String cid = cursor.getString(cursor.getColumnIndex(TAG_COLOR_ID));
//                String cname = cursor.getString(cursor.getColumnIndex(TAG_COLOR_NAME));
                String hex = cursor.getString(cursor.getColumnIndex(TAG_HEX));

                PCliq_ItemColors itemColors = new PCliq_ItemColors(cid, "", hex);
                arrayList.add(itemColors);

                cursor.moveToNext();
            }
            cursor.close();
        }

        return arrayList;
    }

    public void addWallpaper(PCliq_ItemPose itemWallpaper, String table, String id) {
        if (!checkWallpaperAdded(itemWallpaper.getId())) {
            String imageBig = encryptData.encrypt(itemWallpaper.getImage().replace(" ", "%20"));

            ContentValues contentValues = new ContentValues();
            contentValues.put(TAG_WALL_ID, itemWallpaper.getId());
            contentValues.put(TAG_TITLE, itemWallpaper.getTitle());
            contentValues.put(TAG_CAMERA_SETTING, itemWallpaper.getCamerasetting());
            contentValues.put(TAG_POSE_TIPS, itemWallpaper.getPosetips());
//            Log.e("TAG", "addWallpaper tips: " + itemWallpaper.getPosetips());

//            Log.e("TAG", "addWallpaper camera: " + itemWallpaper.getCamerasetting());
            contentValues.put(TAG_IMAGE_BIG, imageBig);
            contentValues.put(TAG_VIEWS, Integer.parseInt(itemWallpaper.getTotalViews()));
            contentValues.put(TAG_AVG_RATE, itemWallpaper.getAverageRate());
            contentValues.put(TAG_TOTAL_DOWNLOAD, itemWallpaper.getTotalDownloads());
            contentValues.put(TAG_TAGS, itemWallpaper.getTags());
            contentValues.put(TAG_TYPE, itemWallpaper.getType());
            contentValues.put(TAG_IS_FAV, itemWallpaper.getIsFav());

            switch (table) {
                case "cat":
                    contentValues.put(TAG_CAT_ID, id);
                    break;
                case "subcat":
                    contentValues.put(TAG_SUB_CAT_ID, id);
                    break;
                case "featured":
                    contentValues.put(TAG_IS_FEATURED, "1");
                    break;
                case "live_wall":
                    contentValues.put(TAG_IS_LIVE_WALLPAPER, "1");
                    break;
                case "recent":
                    contentValues.put(TAG_IS_RECENT, "1");
                    break;
            }

            db.insert(TABLE_WALL_BY_LATEST, null, contentValues);
        } else {

            String where = TAG_WALL_ID + "=?";
            String[] args = {itemWallpaper.getId()};

            ContentValues contentValues = new ContentValues();
            switch (table) {
                case "cat":
                    contentValues.put(TAG_CAT_ID, id);
                    db.update(TABLE_WALL_BY_LATEST, contentValues, where, args);
                    break;
                case "subcat":
                    contentValues.put(TAG_SUB_CAT_ID, id);
                    db.update(TABLE_WALL_BY_LATEST, contentValues, where, args);
                    break;
                case "featured":
                    contentValues.put(TAG_IS_FEATURED, "1");
                    db.update(TABLE_WALL_BY_LATEST, contentValues, where, args);
                    break;
                case "live_wall":
                    contentValues.put(TAG_IS_LIVE_WALLPAPER, "1");
                    db.update(TABLE_WALL_BY_LATEST, contentValues, where, args);
                    break;
                case "recent":
                    contentValues.put(TAG_IS_RECENT, "1");
                    db.update(TABLE_WALL_BY_LATEST, contentValues, where, args);
                    break;
            }
        }
    }

    private Boolean checkWallpaperAdded(String id) {
        Cursor cursor = db.query(TABLE_WALL_BY_LATEST, columns_wall, TAG_WALL_ID + "='" + id + "'", null, null, null, null);
        Boolean isRecent = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        return isRecent;
    }

    private Boolean checkCatAdded(String id) {
        Cursor cursor = db.query(TABLE_CAT, columns_cat, TAG_CAT_ID + "='" + id + "'", null, null, null, null);
        Boolean isRecent = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        return isRecent;
    }

    private Boolean checkSubCatAdded(String id) {
        Cursor cursor = db.query(TABLE_SUB_CAT, columns_sub_cat, TAG_CAT_ID + "='" + id + "'", null, null, null, null);
        Boolean isRecent = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        return isRecent;
    }

    public void removeWallByCat(String table, String id) {
        try {
            db.delete(table, TAG_CAT_ID + "=" + id, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeColors() {
        try {
            db.delete(TABLE_COLOR, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addToCatList(PCliq_ItemCat itemCategory) {
        if (!checkCatAdded(itemCategory.getId())) {
            String image = encryptData.encrypt(itemCategory.getImage().replace(" ", "%20"));

            ContentValues contentValues = new ContentValues();
            contentValues.put(TAG_CAT_ID, itemCategory.getId());
            contentValues.put(TAG_CAT_NAME, itemCategory.getName());
            contentValues.put(TAG_IMAGE_BIG, image);
            contentValues.put(TAG_CAT_TOT_WALL, itemCategory.getTotalWallpaper());

            db.insert(TABLE_CAT, null, contentValues);
        }
    }

    public void addToSubCatList(PCliq_ItemSubCat itemSubCat, String catID) {
        if (!checkSubCatAdded(itemSubCat.getId())) {
            String image = encryptData.encrypt(itemSubCat.getImage().replace(" ", "%20"));

            ContentValues contentValues = new ContentValues();
            contentValues.put(TAG_CAT_ID, catID);
            contentValues.put(TAG_SUB_CAT_ID, itemSubCat.getId());
            contentValues.put(TAG_CAT_NAME, itemSubCat.getName());
            contentValues.put(TAG_IMAGE_BIG, image);
            contentValues.put(TAG_CAT_TOT_WALL, itemSubCat.getTotalWallpapers());

            db.insert(TABLE_SUB_CAT, null, contentValues);
        }
    }

    public void addtoColorList(PCliq_ItemColors itemColors) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TAG_COLOR_ID, itemColors.getId());
//        contentValues.put(TAG_COLOR_NAME, itemColors.getColorName());
        contentValues.put(TAG_HEX, itemColors.getColorHex());
//        Log.e("TAG", "addtoColorList: " + itemColors.getColorName());
        db.insert(TABLE_COLOR, null, contentValues);
    }

    public void removeAllCat() {
        db.delete(TABLE_CAT, null, null);
    }

    public void updateView(String id, String totview, String download) {

        ContentValues values = new ContentValues();
        values.put(TAG_VIEWS, totview);
        values.put(TAG_TOTAL_DOWNLOAD, download);

        String where = TAG_WALL_ID + "=?";
        String[] args = {id};

        db.update(TABLE_WALL_BY_LATEST, values, where, args);
    }

    public void updateTags(String id, String tags) {

        ContentValues values = new ContentValues();
        values.put(TAG_TAGS, tags);

        String where = TAG_WALL_ID + "=?";
        String[] args = {id};

        db.update(TABLE_WALL_BY_LATEST, values, where, args);
    }

    public void addtoAbout() {
        try {
            db.delete(TABLE_ABOUT, null, null);

            ContentValues contentValues = new ContentValues();
            contentValues.put(TAG_ABOUT_NAME, PCliq_Constant.itemAbout.getAppName());
            contentValues.put(TAG_ABOUT_LOGO, PCliq_Constant.itemAbout.getAppLogo());
            contentValues.put(TAG_ABOUT_VERSION, PCliq_Constant.itemAbout.getAppVersion());
            contentValues.put(TAG_ABOUT_AUTHOR, PCliq_Constant.itemAbout.getAuthor());
            contentValues.put(TAG_ABOUT_CONTACT, PCliq_Constant.itemAbout.getContact());
            contentValues.put(TAG_ABOUT_EMAIL, PCliq_Constant.itemAbout.getEmail());
            contentValues.put(TAG_ABOUT_WEBSITE, PCliq_Constant.itemAbout.getWebsite());
            contentValues.put(TAG_ABOUT_DESC, PCliq_Constant.itemAbout.getAppDesc());
            contentValues.put(TAG_ABOUT_PUB_ID, PCliq_Constant.publisherAdID);
            contentValues.put(TAG_ABOUT_BANNER_ID, PCliq_Constant.bannerAdID);
            contentValues.put(TAG_ABOUT_INTER_ID, PCliq_Constant.interstitialAdID);
            contentValues.put(TAG_ABOUT_IS_BANNER, PCliq_Constant.isBannerAd.toString());
            contentValues.put(TAG_ABOUT_IS_INTER, PCliq_Constant.isInterAd.toString());
            contentValues.put(TAG_ABOUT_IS_PORTRAIT, PCliq_Constant.isPortrait.toString());
            contentValues.put(TAG_ABOUT_IS_LANDSCAPE, PCliq_Constant.isLandscape.toString());
            contentValues.put(TAG_ABOUT_IS_SQUARE, PCliq_Constant.isSquare.toString());
            contentValues.put(TAG_ABOUT_CLICK, PCliq_Constant.interstitialAdShow);

            db.insert(TABLE_ABOUT, null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("Range")
    public Boolean getAbout() {

        Cursor c = db.query(TABLE_ABOUT, columns_about, null, null, null, null, null);

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                String appname = c.getString(c.getColumnIndex(TAG_ABOUT_NAME));
                String applogo = c.getString(c.getColumnIndex(TAG_ABOUT_LOGO));
                String desc = c.getString(c.getColumnIndex(TAG_ABOUT_DESC));
                String appversion = c.getString(c.getColumnIndex(TAG_ABOUT_VERSION));
                String appauthor = c.getString(c.getColumnIndex(TAG_ABOUT_AUTHOR));
                String appcontact = c.getString(c.getColumnIndex(TAG_ABOUT_CONTACT));
                String email = c.getString(c.getColumnIndex(TAG_ABOUT_EMAIL));
                String website = c.getString(c.getColumnIndex(TAG_ABOUT_WEBSITE));

                PCliq_Constant.bannerAdID = c.getString(c.getColumnIndex(TAG_ABOUT_BANNER_ID));
                PCliq_Constant.interstitialAdID = c.getString(c.getColumnIndex(TAG_ABOUT_INTER_ID));
                PCliq_Constant.isBannerAd = Boolean.parseBoolean(c.getString(c.getColumnIndex(TAG_ABOUT_IS_BANNER)));
                PCliq_Constant.isInterAd = Boolean.parseBoolean(c.getString(c.getColumnIndex(TAG_ABOUT_IS_INTER)));
                PCliq_Constant.publisherAdID = c.getString(c.getColumnIndex(TAG_ABOUT_PUB_ID));
                PCliq_Constant.interstitialAdShow = Integer.parseInt(c.getString(c.getColumnIndex(TAG_ABOUT_CLICK)));
                PCliq_Constant.isPortrait = Boolean.parseBoolean(c.getString(c.getColumnIndex(TAG_ABOUT_IS_PORTRAIT)));
                PCliq_Constant.isLandscape = Boolean.parseBoolean(c.getString(c.getColumnIndex(TAG_ABOUT_IS_LANDSCAPE)));
                PCliq_Constant.isSquare = Boolean.parseBoolean(c.getString(c.getColumnIndex(TAG_ABOUT_IS_SQUARE)));

                PCliq_Constant.itemAbout = new PCliq_ItemAbout(appname, applogo, desc, appversion, appauthor, appcontact, email, website);
            }
            c.close();
            return true;
        } else {
            c.close();
            return false;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}