package com.example.wee_8.arimage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.wee_8.arimage.Helper.BitmapHelper;
import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;

import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.ModelRenderable;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

public class MainActivity extends AppCompatActivity implements Scene.OnUpdateListener {
    private ExternalTexture texture;
    private MediaPlayer mediaPlayer;

    private Scene scene;
    private ModelRenderable renderable;
    private boolean isImageDetected = false;
    private Anchor videoAnchor;
    private Node videoNode;
    private AnchorNode anchorNode, anchorNode2;

    private CustomArFragment arFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ExternalTexture texture = new ExternalTexture();
        texture = new ExternalTexture();

        // Uri uri = Uri.parse("vnd.youtube://" + "fqyW-uvEbDE");
        mediaPlayer =MediaPlayer.create(this,R.raw.video);
        mediaPlayer.setSurface(texture.getSurface());
        mediaPlayer.setLooping(true);

        ModelRenderable.builder().setSource(this,R.raw.video_screen).build().thenAccept(modelRenderable ->
        {modelRenderable.getMaterial().setExternalTexture("videoTexture",
                texture);
            modelRenderable.getMaterial().setFloat4("keyColor", new Color(0.01843f, 1f, 0.098f));
            renderable = modelRenderable;
        });




        // mediaPlayer =MediaPlayer.create(this, uri);

        arFragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        scene = arFragment.getArSceneView().getScene();
        scene.addOnUpdateListener(this::onUpdate);
        arFragment.getPlaneDiscoveryController().hide();
        //arFragment.getArSceneView().getScene().addOnUpdateListener(this);
    }

    private Bitmap loadBitmapFromUrl(String sourceLink) {

        try{
            URL url = new URL(sourceLink);

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);

            return myBitmap;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setupDatabase(Config config, Session session){




        //Bitmap bit = BitmapFactory.decodeResource(getResources(),R.drawable.aws4);


        // Bitmap bitmap = BitmapHelper.getInstance().getBitmap();
        //  Bitmap bitmap2 = BitmapHelper.getInstance().getBitmap();
        Bitmap bitmap1 = loadBitmapFromUrl("https://media.wired.com/photos/5b86fce8900cb57bbfd1e7ee/master/pass/Jaguar_I-PACE_S_Indus-Silver_065.jpg");
        Bitmap bitmap2 = loadBitmapFromUrl("https://images.techhive.com/images/article/2015/05/aws-logo-100584713-primary.idge.jpg");
        Bitmap bitmap3 = loadBitmapFromUrl("https://fsmedia.imgix.net/96/a2/69/e6/d499/40c5/a0d3/463c5ffb1ab9/justice-league-2017.jpeg?rect=0%2C536%2C2764%2C1390&auto=format%2Ccompress&dpr=2&w=650");
        System.out.println("bitmap1="+bitmap1);
        System.out.println("bitmap2="+bitmap2);
        System.out.println("bitmap3="+bitmap3);
        // Bitmap flashBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.justiceleague2017);
        AugmentedImageDatabase aid = new AugmentedImageDatabase(session);
        aid.addImage("BitmapImage",bitmap1 );
        aid.addImage("BitmapImage2",bitmap2 );
        aid.addImage("BitmapImage3",bitmap3 );
        // aid.addImage("justiceleague2017", flashBitmap);
        config.setAugmentedImageDatabase(aid);

    }

    @Override
    public void onUpdate(FrameTime frameTime) {
        
        if (videoAnchor != null && mediaPlayer != null) {

            if (videoAnchor.getTrackingState() != TrackingState.TRACKING) {

                if (mediaPlayer.isPlaying()) {
                    videoNode.setParent (null);
                    mediaPlayer.pause();
                   
                }

            } else {

                if (!mediaPlayer.isPlaying()) {
                    videoNode.setParent (anchorNode);
                    mediaPlayer.start();
                }

            }

        }

        Frame frame = arFragment.getArSceneView().getArFrame();
        Collection<AugmentedImage> images = frame.getUpdatedTrackables(AugmentedImage.class);

        for (AugmentedImage image : images ) {
            if(isImageDetected)return;
            if(image.getTrackingState() == TrackingState.TRACKING){
                if (image.getName().equals("BitmapImage")) {
                        isImageDetected =true;
                    videoAnchor = image.createAnchor (image.getCenterPose());
                    playVideo (image.getExtentX(),image.getExtentZ());
                    break;
                }

                if (image.getName().equals("BitmapImage2")){
                    String URL1= "https://aws.amazon.com/";
                    Intent intent = new Intent(MainActivity.this,WebViewS.class);
                    intent.putExtra("A", URL1);
                    startActivity(intent);
                }

                if (image.getName().equals("BitmapImage3")){
                    Anchor anchor = image.createAnchor(image.getCenterPose());
                    createModel(anchor);
                }
            }
        }
    }

    private void createModel(Anchor anchor) {
        ModelRenderable.builder()
                .setSource(this, Uri.parse("model.sfb"))
                .build()
                .thenAccept(modelRenderable -> placeModel(modelRenderable, anchor));
    }

    private void placeModel(ModelRenderable modelRenderable, Anchor anchor) {
        anchorNode = new AnchorNode(anchor);
        anchorNode.setRenderable(modelRenderable);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
    }

    private void playVideo(float extentX, float extentZ) {

        mediaPlayer.start();
        anchorNode = new AnchorNode(videoAnchor);
        videoNode = new Node ();
        videoNode.setParent (anchorNode);

        texture.getSurfaceTexture().setOnFrameAvailableListener(surfaceTexture -> {
            videoNode.setRenderable(renderable);
            texture.getSurfaceTexture().setOnFrameAvailableListener(null);
        });

        videoNode.setWorldScale(new Vector3(extentX,1f,extentZ));

        scene.addChild(anchorNode);
    }




}
