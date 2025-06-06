package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;

public class Proyectil extends Node {

    private final float velocidad = 10f;
    private Enemigo objetivo;
    private int danio;

    public Proyectil(AssetManager assetManager, Vector3f origen, Enemigo objetivo, int danio) {
        this.objetivo = objetivo;
        this.danio = danio;

        String textura = danio >= 12 ? "Textures/magia.png" : "Textures/flecha.png";

        Quad quad = new Quad(0.5f, 0.5f);
        Geometry geo = new Geometry("Proyectil", quad);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex = assetManager.loadTexture(textura);
        mat.setTexture("ColorMap", tex);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        geo.setQueueBucket(RenderQueue.Bucket.Transparent);
        geo.setMaterial(mat);

        this.attachChild(geo);

        // Centrar nodo
        this.setLocalTranslation(origen.add(0, 0.25f, 0));
    }

    public void actualizar(float tpf) {
        if (objetivo == null || objetivo.isMuerto()) {
            this.removeFromParent();
            return;
        }

        Vector3f posActual = this.getLocalTranslation();
        Vector3f posObjetivo = objetivo.getWorldTranslation();

        Vector3f direccion = posObjetivo.subtract(posActual).normalizeLocal();
        Vector3f movimiento = direccion.mult(tpf * velocidad);
        this.setLocalTranslation(posActual.add(movimiento));

        float distancia = posActual.distance(posObjetivo);
        if (distancia < 0.6f) {
            objetivo.recibirDanio(danio);
            this.removeFromParent();
        }
    }
}
