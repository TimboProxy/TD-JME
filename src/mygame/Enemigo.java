package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;

public class Enemigo extends Node {

    private float velocidad = 2f;
    private int vida = 30;
    private int vidaMaxima = 30;
    private boolean muerto = false;

    private Geometry barraVida;
    private Geometry slimeQuad;
    private float tiempoAnimacion = 0f;

    private Main juego;

    public Enemigo(AssetManager assetManager, Vector3f inicio, int vidaInicial, Main juego) {
        this.vida = vidaInicial;
        this.vidaMaxima = vidaInicial;
        this.juego = juego;

        // Crear un quad con textura
        Quad quad = new Quad(2f, 2f);
        slimeQuad = new Geometry("SlimeQuad", quad);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex = assetManager.loadTexture("Textures/slime.png");
        mat.setTexture("ColorMap", tex);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        slimeQuad.setQueueBucket(RenderQueue.Bucket.Transparent);
        slimeQuad.setMaterial(mat);

        // Rotar para que mire a la cámara
        slimeQuad.rotate((float) -Math.PI / 4f, 0, 0);
        slimeQuad.setLocalTranslation(-1f, 0, 0); // centrado

        this.attachChild(slimeQuad);

        // Barra de vida
        barraVida = new Geometry("BarraVida", new Box(0.4f, 0.05f, 0.01f));
        Material matBarra = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matBarra.setColor("Color", ColorRGBA.Green);
        barraVida.setMaterial(matBarra);
        barraVida.setLocalTranslation(0, 1.5f, 0);
        this.attachChild(barraVida);

        // Posición inicial
        this.setLocalTranslation(inicio.add(0, 1f, 0));
    }

    public void actualizar(float tpf) {
        if (muerto) return;

        // Animación de rebote suave
        tiempoAnimacion += tpf;
        float salto = (float) Math.sin(tiempoAnimacion * 4f) * 0.1f;
        slimeQuad.setLocalTranslation(-1f, salto, 0); // ajustar eje Y con rebote

        // Movimiento hacia adelante
        this.move(0, 0, velocidad * tpf);

        // Eliminar si sale de la zona
        if (this.getLocalTranslation().z > 10f) {
            muerto = true;
            this.removeFromParent();
            if (juego != null) {
                juego.reducirVidaJugador(vida);
            }
        }
    }

    public void recibirDanio(int cantidad) {
        if (muerto) return;

        vida -= cantidad;
        float ratio = Math.max(0, (float) vida / vidaMaxima);
        barraVida.setLocalScale(ratio, 1, 1);
        barraVida.setLocalTranslation(-0.4f * (1 - ratio), 1.5f, 0);

        if (vida <= 0) {
            muerto = true;
            this.removeFromParent();
        }
    }

    public boolean isMuerto() {
        return muerto;
    }
}
