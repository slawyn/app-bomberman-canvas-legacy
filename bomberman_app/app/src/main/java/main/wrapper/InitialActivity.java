package main.wrapper;

import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import java.util.Vector;

import main.Essentials.App;
import main.Essentials.Client;
import main.Essentials.Pipe;
import main.Essentials.Server;
import main.Globals;
import main.Constants;
import main.ResourceLoader;

/* Bluetooth: http://www.doepiccoding.com/blog/?p=232 */
/* Optimization: https://developer.android.com/training/articles/perf-tips*/
public class InitialActivity extends Activity implements Connector {
    private final String LOGTAG = "InitialActivity";
    private Client client;
    private Server server;
    private boolean serverbootable;
    private boolean bluetoothenabled;
    private boolean waitingforbluetooth;
    private ActivityManager activityManager;
    private ActivityManager.MemoryInfo mem;
    private Vector<BluetoothDevice> devices;
    private Vector<String> deviceNames;
    private int lastDiscoveredDevice;
    private boolean broadcast;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice bdevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            boolean hasuuid = true;
            /* TODO, can't get uuiids without Pairing, always null..hmmm...
            Parcelable uuids[] = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
            for(int i = 0;i<uuids.length;i++){
                if(uuids[i].toString().equals(Constants.SERVER_UUID)){
                    hasuuid = true;
                    break;
                }
            }
            */
            // Add only devices with the specified Bomber UUID
            if (hasuuid) {

                String name = bdevice.getName() + " " + bdevice.getAddress();

                if (!deviceNames.contains(name)) {
                    devices.add(bdevice);
                    deviceNames.add(name);
                    Log.e(LOGTAG, "Found new device:" + name);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create View
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Get version
        try {
            Globals.version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // Get screen Metrics
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        // Analysis
        mem = new ActivityManager.MemoryInfo();
        activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        // Bluetooth
        devices = new Vector<>();
        deviceNames = new Vector<>();
        serverbootable = false;

        bluetoothenabled = false;
        waitingforbluetooth = false;
        lastDiscoveredDevice = 0;

        // Configure resource loader
        ResourceLoader.getInstance().setResources(getResources());
        ResourceLoader.getInstance().calculateTextureOffsets(displayMetrics.densityDpi, displayMetrics.widthPixels, displayMetrics.heightPixels);
        setContentView(new InitialView(this));

    }


    @Override
    public int getAvailableMemory() {
        activityManager.getMemoryInfo(mem);
        return (int) (mem.availMem / 0x100000L);
    }

    @Override
    public boolean connectPipeToServer(Pipe pipe) {
        try {
            if (serverbootable) {
                Log.e(LOGTAG, "Booting Server");
                server = new Server(pipe);
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean connectPipeToClient(String name, Pipe pipe) {
        Log.e(LOGTAG, "Connecting to " + name);
        try {
            client = new Client(devices.get(deviceNames.indexOf(name)), pipe);
            devices.clear();
            deviceNames.clear();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean hasDevices() {
        return lastDiscoveredDevice < devices.size();
    }

    @Override
    public String discoveredDevice() {
        String s = deviceNames.get(lastDiscoveredDevice);
        lastDiscoveredDevice++;
        return s;
    }

    // For client
    @Override
    public void discoverDevices() {
        broadcast = true;
        registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            if (adapter.isDiscovering())
                adapter.cancelDiscovery();
            adapter.startDiscovery();
        }
    }

    // Stop discovery
    @Override
    public void stopDiscovery() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            adapter.cancelDiscovery();
        }
    }

    // For server
    @Override
    public void makeBluetoothDiscoverable() {
        if (bluetoothenabled) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 20);
            startActivityForResult(discoverableIntent, Constants.DISCOVERABLE_REQUEST_CODE);
        }
    }

    @Override
    public boolean isBluetoothEnabled() {
        if (!waitingforbluetooth) {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if (adapter != null) {
                bluetoothenabled = true;
            } else {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, Constants.BLUETOOTH_ENABLE_REQUEST_CODE);
            }
        }
        return bluetoothenabled;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.DISCOVERABLE_REQUEST_CODE) {
            if (resultCode != RESULT_CANCELED) {
                serverbootable = true;
            }
        } else if (requestCode == Constants.BLUETOOTH_ENABLE_REQUEST_CODE) {
            if (resultCode != RESULT_CANCELED) {
                bluetoothenabled = true;
                waitingforbluetooth = false;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcast) {
            unregisterReceiver(broadcastReceiver);
        }
        if (client != null) {
            client.closeConnnection();
            client = null;
        }
        if (server != null) {
            server.closeConnection();
            server = null;
        }
    }


    //////////////////////////////////
    // View, used as main game loop //
    class InitialView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
        private final String LOGTAG = "InitialView";
        private Thread drawingThread;
        private SurfaceHolder holder;
        private App app;
        private boolean gamerunning = false;
        private boolean isTouch[];
        private boolean isMove[];

        //
        public InitialView(Connector context) {
            super((Context) context);
            setDrawingCacheEnabled(true);

            // Variables for touch
            isTouch = new boolean[Constants.MAX_TOUCH_INSTANCES];
            isMove = new boolean[Constants.MAX_TOUCH_INSTANCES];

            // Surfaceview
            SurfaceHolder holder = getHolder();
            holder.addCallback(this);
            setFocusable(true);

            // Start App
            app = new App(context);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (width == 0 || height == 0) {
                // TODO resize your UI
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder hol) {
            holder = hol;
            holder.setFormat(PixelFormat.OPAQUE);

            if (drawingThread != null) {
                Log.d(LOGTAG, "Drawing thread still active..");
                gamerunning = false;
                try {
                    drawingThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                drawingThread = new Thread(this, "##appThread");
                gamerunning = true;
                drawingThread.start();
            }

            Log.d(LOGTAG, "##app Thread Started");
        }

        public void stopDrawingThread() {
            if (drawingThread == null) {
                Log.d(LOGTAG, "#appThread is null");
                return;
            }
            gamerunning = false;
            while (true) {
                try {
                    Log.d(LOGTAG, "Request last frame");
                    drawingThread.join(5000);
                    break;
                } catch (Exception e) {
                    Log.e(LOGTAG, "Could not join with drawing thread");
                }
            }
            drawingThread = null;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            int action = event.getActionMasked();
            int pointerCnt = event.getPointerCount();
            int act_id = event.getPointerId(event.getActionIndex());

            if (act_id > Constants.MAX_TOUCH_INSTANCES) // TODO currently no support !
                return true;

            // if moved we set it to true
            isMove[act_id] = false;
            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    isMove[act_id] = true;
                case MotionEvent.ACTION_POINTER_DOWN:
                case MotionEvent.ACTION_DOWN:
                    isTouch[act_id] = true;
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE:
                    isTouch[act_id] = false;
                    break;
            }

            for (int i = 0; i < pointerCnt; i++) {
                int id = event.getPointerId(i);
                app.passInput(id, (int) event.getX(i), (int) event.getY(i), isTouch[id], isMove[id]);
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }

        // Surface is not used anymore - stop the drawing thread
        @Override
        public void surfaceDestroyed(SurfaceHolder holde) {
            stopDrawingThread();
            holder.getSurface().release();
            holder = null;
            Log.d(LOGTAG, "Destroyed");
        }

        @Override
        public void run() {
            int maxFrameTime = Constants.MAX_FRAME_TIME;
            long delta = 1, frameStartTime;
            Canvas canvas;

            // Color black
            final Paint black = new Paint();
            black.setTextSize(30 * (Globals.positionTranslationFactor));
            black.setStrokeWidth(5 * (Globals.positionTranslationFactor));
            black.setAlpha(255);
            black.setAntiAlias(false);
            black.setColor(Color.BLACK);

            // Color red
            final Paint red = new Paint();
            red.setTextSize(30 * (Globals.positionTranslationFactor));
            red.setStrokeWidth(5 * (Globals.positionTranslationFactor));
            red.setAlpha(255);
            red.setAntiAlias(false);
            red.setColor(Color.RED);


           /* In order to work reliably on Nexus 7, we place ~500ms delay at the start of drawing thread
           (AOSP - Issue 58385)*/

            if (android.os.Build.BRAND.equalsIgnoreCase("google") && android.os.Build.MANUFACTURER.equalsIgnoreCase("asus") && android.os.Build.MODEL.equalsIgnoreCase("Nexus 7")) {
                Log.d(LOGTAG, "Sleep 500ms (Device: Asus Nexus 7)");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                }
            }

            // Load App Resources
            app.loadResources();

            //Drawing loop
            while (gamerunning) {
                frameStartTime = SystemClock.elapsedRealtimeNanos();


                // Update game
                app.updateLogic(delta);


                canvas = holder.lockCanvas();

                // Draw game
                if (canvas != null) {
                    app.draw(canvas, black, red);
                    holder.unlockCanvasAndPost(canvas);
                }

                /*
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
*/

                // calculate the time required to draw the frame in ms
                delta = (SystemClock.elapsedRealtimeNanos() - frameStartTime) / 1000000;// >> 20;
                if (delta < maxFrameTime) {
                    delta = maxFrameTime;
                    try {
                        Thread.sleep(maxFrameTime - delta);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }/**/
            }
            Log.d(LOGTAG, "#appThread finished");
        }
    }
}

