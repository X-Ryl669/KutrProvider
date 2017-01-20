package fr.byped.kutr.providers;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by cyril on 17/01/2017.
 */

public class CacheProvider  {

    private Context context;
    private SharedPreferences prefs;

    public File getCachePath() {
        Boolean hasCache = prefs.getBoolean("enable_cache", false);
        if (!hasCache) return null;
        // Get the preferences set for the application
        String cachePath = prefs.getString("cache_path", "/kutr");
        if (cachePath.charAt(0) != '/') cachePath = '/' + cachePath;

        return context.getExternalFilesDir(cachePath); // This is better because it's destructed when the app is uninstalled
       // return Environment.getExternalStorageDirectory().toString() + cachePath;
    }

    public boolean hasCachePath() {
        File cacheFile = getCachePath();
        return cacheFile != null && cacheFile.exists() && cacheFile.isDirectory();
    }


    public long getCacheUsageSize()
    {
        if (!hasCachePath()) return 0;
        // For now, it's very very basic...
        File cacheDir = getCachePath();
        File[] files = cacheDir.listFiles();
        if (files == null) return 0;

        long size = 0;
        for (File file: files) {
            size += file.length();
        }
        return size;
    }

    public File hasFileForID(final String id) {
        File cacheFile = getCachePath();
        if (cacheFile == null) return null; // No cache for the given ID
        File[] files = cacheFile.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(id); // or something else
            }});
        if (files == null || files.length == 0) return null;
        return files[0];
    }

    public boolean storeFileWithID(String id, File newFile) {
        File cacheFile = getCachePath();
        if (cacheFile == null) return false; // No cache for the given ID
        File prevFile = hasFileForID(id);
        // Remove previous file if it exists
        if (prevFile != null && prevFile.exists())
            if (!prevFile.delete()) return false;
        return newFile.renameTo(new File(cacheFile, String.format("%s_%u.kutr", id,  System.currentTimeMillis() / 1000L)));
    }

    public CacheProvider(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }
}
