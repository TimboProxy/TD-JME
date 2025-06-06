package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;

import java.util.List;

public class Torre extends Node {

    protected float rango;
    protected float tiempoEntreDisparos;
    protected float tiempoDesdeUltimoDisparo = 0f;
    protected int danio;

    protected AssetManager assetManager;
    protected AudioNode sonidoDisparo;

    public Torre(AssetManager assetManager, Vector3f posicion, ColorRGBA color, float rango, float tiempo, int danio, String nombreTextura) {
        this.assetManager = assetManager;
        this.rango = rango;
        this.tiempoEntreDisparos = tiempo;
        this.danio = danio;

        // Crear quad con textura personalizada
        Quad quad = new Quad(2f, 2f);
        Geometry geo = new Geometry("TorreQuad", quad);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex = assetManager.loadTexture("Textures/" + nombreTextura);
        mat.setTexture("ColorMap", tex);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        geo.setQueueBucket(RenderQueue.Bucket.Transparent);
        geo.setMaterial(mat);

        geo.rotate((float) -Math.PI / 10f, 0, 0);
        geo.setLocalTranslation(-1f, 0, 0);

        this.attachChild(geo);
        this.setLocalTranslation(posicion);

        sonidoDisparo = new AudioNode(assetManager, "Sounds/disparo.wav", false);
        sonidoDisparo.setPositional(false);
        sonidoDisparo.setLooping(false);
        sonidoDisparo.setVolume(1f);
    }

    public void actualizar(float tpf, List<Enemigo> enemigos, List<Proyectil> proyectiles) {
        tiempoDesdeUltimoDisparo += tpf;

        if (tiempoDesdeUltimoDisparo >= tiempoEntreDisparos) {
            for (Enemigo enemigo : enemigos) {
                if (enemigo == null || enemigo.isMuerto()) continue;

                float distancia = this.getWorldTranslation().distance(enemigo.getWorldTranslation());
                if (distancia <= rango) {
                    dispararA(enemigo, proyectiles);
                    tiempoDesdeUltimoDisparo = 0;
                    break;
                }
            }
        }
    }

    protected void dispararA(Enemigo enemigo, List<Proyectil> proyectiles) {
        Proyectil p = new Proyectil(assetManager, this.getWorldTranslation(), enemigo, danio);
        this.getParent().attachChild(p);
        proyectiles.add(p);
        sonidoDisparo.playInstance();
    }
}
