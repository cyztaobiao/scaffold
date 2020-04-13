package jujube.android.widgets.largeimage.factory;

import android.graphics.BitmapRegionDecoder;

import java.io.IOException;

public interface BitmapDecoderFactory {
    BitmapRegionDecoder made() throws Exception, OutOfMemoryError;
}