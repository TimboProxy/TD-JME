package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

public class TorreMagia extends Torre {

    public TorreMagia(AssetManager assetManager, Vector3f posicion) {
        super(assetManager, posicion, ColorRGBA.Cyan, 5f, 1.2f, 20, "torre_magia.png");
    }
}
