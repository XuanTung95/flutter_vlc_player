package software.solid.fluttervlcplayer;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import io.flutter.FlutterInjector;
import io.flutter.Log;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;

public class FlutterVlcPlayerPlugin implements FlutterPlugin, ActivityAware, FlutterEngine.EngineLifecycleListener {

    private static FlutterVlcPlayerFactory flutterVlcPlayerFactory;
    private FlutterPluginBinding flutterPluginBinding;

    private static final String VIEW_TYPE = "flutter_video_plugin/getVideoView";

    public FlutterVlcPlayerPlugin() {
    }

    @SuppressWarnings("deprecation")
    public static void registerWith(io.flutter.plugin.common.PluginRegistry.Registrar registrar) {
        if (flutterVlcPlayerFactory == null) {
            flutterVlcPlayerFactory =
                    new FlutterVlcPlayerFactory(
                            registrar.messenger(),
                            registrar.textures(),
                            registrar::lookupKeyForAsset,
                            registrar::lookupKeyForAsset,
                            registrar.context()
                    );
            registrar
                    .platformViewRegistry()
                    .registerViewFactory(
                            VIEW_TYPE,
                            flutterVlcPlayerFactory
                    );
        }
        registrar.addViewDestroyListener(view -> {
            stopListening();
            return false;
        });
        //
        startListening();
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        flutterPluginBinding = binding;
        //
        if (flutterVlcPlayerFactory == null) {
            final FlutterInjector injector = FlutterInjector.instance();
            //
            flutterVlcPlayerFactory =
                    new FlutterVlcPlayerFactory(
                            flutterPluginBinding.getBinaryMessenger(),
                            flutterPluginBinding.getTextureRegistry(),
                            injector.flutterLoader()::getLookupKeyForAsset,
                            injector.flutterLoader()::getLookupKeyForAsset,
                            binding.getApplicationContext()
                    );
            flutterPluginBinding
                    .getPlatformViewRegistry()
                    .registerViewFactory(
                            VIEW_TYPE,
                            flutterVlcPlayerFactory
                    );
            //
        }
        flutterVlcPlayerFactory.onAttachedToEngine(binding);
        startListening();
        binding.getFlutterEngine().addEngineLifecycleListener(this);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        stopListening();
        //
        flutterPluginBinding = null;
        binding.getFlutterEngine().removeEngineLifecycleListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    }

    @Override
    public void onDetachedFromActivity() {
    }

    // extra methods

    private static void startListening() {
        if (flutterVlcPlayerFactory != null)
            flutterVlcPlayerFactory.startListening();
    }

    private static void stopListening() {
        if (flutterVlcPlayerFactory != null) {
            flutterVlcPlayerFactory.stopListening();
            flutterVlcPlayerFactory = null;
        }
    }

    @Override
    public void onPreEngineRestart() {
        if (flutterVlcPlayerFactory != null) {
            flutterVlcPlayerFactory.disposeAllPlayers();
        }
    }

    @Override
    public void onEngineWillDestroy() {

    }
}
