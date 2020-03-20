package com.hzh.frame.core.ImageFrame;

import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ViewGroup;

import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.postprocessors.IterativeBoxBlurPostProcessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.hzh.frame.R;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 图片存储在/data/data/APP包名/cache/image_cache/ 下面以cnt结尾的，拿到文件后只要另存为jpg或png文件即可
 * */
public class BaseImage {
    
    private static BaseImage _instance;
    public static BaseImage getInstance(){
        synchronized(BaseImage.class){
            if(_instance==null){
                _instance=new BaseImage();
            }
            return _instance;
        }
    }
    
	/**
     * 从网络中异步加载图片
     * @param uri 图片地址(支持类型可参考官方:https://www.fresco-cn.org/docs/supported-uris.html)
     * @param imageView 显示图片目标View
     */
    public void loadXY(String uri, SimpleDraweeView imageView) {
        loadXY(uri,imageView,imageView.getHierarchy());
    }

    /**
     * 通过imageWidth 的宽度，自动适应高度
     * * @param simpleDraweeView view
     * * @param imagePath  Uri
     * * @param imageWidth width
     */
    public void loadAutoHeight(final SimpleDraweeView simpleDraweeView, String imagePath, final int imageWidth) {
        final ViewGroup.LayoutParams layoutParams = simpleDraweeView.getLayoutParams();
        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id,ImageInfo imageInfo,Animatable anim) {
                if (imageInfo == null) {
                    return;
                }
                int height = imageInfo.getHeight();
                int width = imageInfo.getWidth();
                layoutParams.width = imageWidth;
                layoutParams.height = (int) ((float) (imageWidth * height) / (float) width);
                simpleDraweeView.setLayoutParams(layoutParams);
            }

            @Override
            public void onIntermediateImageSet(String id,ImageInfo imageInfo) {
                Log.d("TAG", "Intermediate image received");
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                throwable.printStackTrace();
            }
        };
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setControllerListener(controllerListener)
                .setUri(Uri.parse(imagePath))
                .setAutoPlayAnimations(false)
                .build();
        simpleDraweeView.setController(controller);
    }

    /**
     * 通过imageHeight 的高度，自动适应宽度
     * * @param simpleDraweeView view
     * * @param imagePath  Uri
     * * @param imageHeight height
     */
    public void loadAutoWidth(final SimpleDraweeView simpleDraweeView, String imagePath, final int imageHeight) {
        final ViewGroup.LayoutParams layoutParams = simpleDraweeView.getLayoutParams();
        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id,ImageInfo imageInfo,Animatable anim) {
                if (imageInfo == null) {
                    return;
                }
                int height = imageInfo.getHeight();
                int width = imageInfo.getWidth();
                layoutParams.height = imageHeight;
                layoutParams.width = (int) ((float) (imageHeight * width) / (float) height);
                simpleDraweeView.setLayoutParams(layoutParams);
            }

            @Override
            public void onIntermediateImageSet(String id,ImageInfo imageInfo) {
                Log.d("TAG", "Intermediate image received");
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                throwable.printStackTrace();
            }
        };
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setControllerListener(controllerListener)
                .setUri(Uri.parse(imagePath))
                .build();
        simpleDraweeView.setController(controller);
    }

    /**
     * 从网络中异步加载图片
     * @param uri 图片地址(支持类型可参考官方:https://www.fresco-cn.org/docs/supported-uris.html)
     * @param imageView 显示图片目标View
     * @param loadErrorImage 图片加载失败显示的本地图片
     */
    public void loadSetErrorImage(String uri, SimpleDraweeView imageView,int loadErrorImage) {
        GenericDraweeHierarchy hierarchy=imageView.getHierarchy();
        hierarchy.setFadeDuration(0);//图片渐渐显示的时间，单位毫秒
        hierarchy.setFailureImage(loadErrorImage);//图片加载失败后显示的错误图片
        hierarchy.setRetryImage(loadErrorImage);//图片加载失败后，显示的重试加载的图片，重试4次后才形式错误的图片
        hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY);
        imageView.setHierarchy(hierarchy);
        imageView.setImageURI(uri);
    }
    
    /**
     * 从网络中异步加载图片
     * @param uri 图片地址(支持类型可参考官方:https://www.fresco-cn.org/docs/supported-uris.html)
     * @param imageView 显示图片目标View
     */
    public void loadXY(String uri, SimpleDraweeView imageView,GenericDraweeHierarchy hierarchy) {
        hierarchy.setFadeDuration(0);//图片渐渐显示的时间，单位毫秒
        hierarchy.setFailureImage(R.drawable.base_image_default);//图片加载失败后显示的错误图片
        hierarchy.setRetryImage(R.drawable.base_image_error);//图片加载失败后，显示的重试加载的图片，重试4次后才形式错误的图片
        hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY);
        imageView.setHierarchy(hierarchy);
        imageView.setImageURI(uri);
    }


    /**
     * 以高斯模糊显示。
     * @param uri 图片地址(支持类型可参考官方:https://www.fresco-cn.org/docs/supported-uris.html)
     * @param draweeView View。
     * @param blurRadius 模糊图半径，必须大于0，越大越模糊。
     */
    public void loadBlur(String uri,SimpleDraweeView draweeView, int blurRadius) {
        try {
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(uri))
                    .setPostprocessor(new IterativeBoxBlurPostProcessor(6, blurRadius))
                    .build();
            AbstractDraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(draweeView.getController())
                    .setImageRequest(request)
                    .build();
            draweeView.setController(controller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void loadBitmap(String uri, final FrescoCallback callback){
        ImageRequestBuilder requestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(uri));
        ImageRequest imageRequest = requestBuilder.build();
        DataSource<CloseableReference<CloseableImage>> dataSource = ImagePipelineFactory.getInstance().getImagePipeline().fetchDecodedImage(imageRequest, null);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
                                 @Override
                                 public void onNewResultImpl(@Nullable final Bitmap bitmap) {
                                     
                                     Observable.just(bitmap.copy(bitmap.getConfig(), bitmap.isMutable()))
                                             .subscribeOn(Schedulers.io())
                                             .observeOn(AndroidSchedulers.mainThread())
                                             .subscribe(new Consumer<Bitmap>() {
                                                 @Override
                                                 public void accept(Bitmap bitmap) throws Exception {
                                                     if (bitmap != null && !bitmap.isRecycled()){
                                                         callback.loadComplete(bitmap);
                                                     }
                                                 }
                                             });
                                 }

                                 @Override
                                 public void onCancellation(DataSource<CloseableReference<CloseableImage>> dataSource) {
                                 }

                                 @Override
                                 public void onFailureImpl(DataSource dataSource) {
                                 }
                             },
                UiThreadImmediateExecutorService.getInstance());
    }

    /**
     * Fresce加载回调接口
     */
    public interface FrescoCallback{
        
        void loadComplete(Bitmap loadedImage);
        
    }


}
