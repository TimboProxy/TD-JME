package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

public class TorreFlecha extends Torre {

    public TorreFlecha(AssetManager assetManager, Vector3f posicion) {
        super(assetManager, posicion, ColorRGBA.White, 7f, 0.8f, 10, "torre_flechas.png");
    }
}
