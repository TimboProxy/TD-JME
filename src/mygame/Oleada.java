package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import java.util.List;

public class Oleada {

    private float tiempoEntreEnemigos = 1.5f;
    private float tiempoTranscurrido = 0f;
    private int enemigosPorOleada = 5;
    private int enemigosGenerados = 0;

    private int numeroOleada = 1;
    private float multiplicadorVida = 1.0f;

    private boolean activa = false;

    private AssetManager assetManager;
    private Node rootNode;
    private List<Enemigo> enemigos;
    private Main juego;

    public Oleada(AssetManager assetManager, Node rootNode, List<Enemigo> enemigos, Main juego) {
        this.assetManager = assetManager;
        this.rootNode = rootNode;
        this.enemigos = enemigos;
        this.juego = juego;
    }

    public void iniciar() {
        enemigosGenerados = 0;
        tiempoTranscurrido = 0;
        activa = true;
    }

    public void actualizar(float tpf) {
        if (!activa) return;

        tiempoTranscurrido += tpf;

        if (tiempoTranscurrido >= tiempoEntreEnemigos && enemigosGenerados < enemigosPorOleada) {
            int vidaBase = 30;
            int vidaAjustada = (int) (vidaBase * multiplicadorVida);

            Enemigo enemigo = new Enemigo(assetManager, new Vector3f(0, 0, -10), vidaAjustada, juego);
            rootNode.attachChild(enemigo);
            enemigos.add(enemigo);

            enemigosGenerados++;
            tiempoTranscurrido = 0;
        }

        if (enemigosGenerados >= enemigosPorOleada) {
            activa = false;
            numeroOleada++;
            enemigosPorOleada++; // +1 enemigo
            multiplicadorVida *= 1.05f; // +5% vida
        }
    }

    public boolean estaActiva() {
        return activa;
    }
}
