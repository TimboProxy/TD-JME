package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.ui.Picture;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main extends SimpleApplication {

    private BitmapText txtEnemigos, txtOleada, txtVida, txtMonedas;
    private BitmapText txtContadorFlecha, txtContadorMagia, txtMensaje;
    private Node menuPausa;

    private int enemigosDestruidos = 0;
    private int oleadaActual = 1;
    private int vidaJugador = 100;
    public static int monedas = 600;

    private final int COSTO_FLECHA = 60;
    private final int COSTO_MAGIA = 100;
    private final int MAX_TORRES = 4;
    private final int MAX_FLECHAS = 2;
    private final int MAX_MAGIAS = 2;

    private int totalTorresFlecha = 1;
    private int totalTorresMagia = 0;

    private List<Enemigo> enemigos = new ArrayList<>();
    private List<Proyectil> proyectiles = new ArrayList<>();
    private List<Torre> torres = new ArrayList<>();
    private Oleada oleada;

    boolean paused = false;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setEnabled(false);
        cam.setLocation(new Vector3f(0, 5, 20));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);

        colocarFondoGlobal();
        cargarFondoLadrillos();
        cargarHUD();
        cargarTextoHUD();
        cargarMusicaFondo();
        configurarControlesCompra();

        oleada = new Oleada(assetManager, rootNode, enemigos, this);
        oleada.iniciar();

        torres.add(new TorreFlecha(assetManager, new Vector3f(-4f, 1, 5)));
        for (Torre t : torres) rootNode.attachChild(t);
    }

    @Override
    public void simpleUpdate(float tpf) {
        if (paused) return;
        oleada.actualizar(tpf);

        for (Torre t : torres) {
            t.actualizar(tpf, enemigos, proyectiles);
        }

        Iterator<Enemigo> iter = enemigos.iterator();
        while (iter.hasNext()) {
            Enemigo e = iter.next();
            if (e.isMuerto()) {
                iter.remove();
                aumentarEnemigosDestruidos();
                monedas += 10;
                actualizarHUD();
            } else {
                e.actualizar(tpf);
            }
        }

        Iterator<Proyectil> it = proyectiles.iterator();
        while (it.hasNext()) {
            Proyectil p = it.next();
            p.actualizar(tpf);
            if (p.getParent() == null) it.remove();
        }
        if (!oleada.estaActiva() && enemigos.isEmpty()) {
            aumentarOleada();
        }
    }

    private void colocarFondoGlobal() {
        Texture tex = assetManager.loadTexture("Textures/pared_ladrillos.png");
        Quad quad = new Quad(80, 55);
        Geometry fondo = new Geometry("FondoGlobal", quad);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", tex);
        fondo.setMaterial(mat);
        fondo.setLocalTranslation(-40, -20, -20);
        rootNode.attachChild(fondo);
    }

    private void cargarFondoLadrillos() {
        Texture tex = assetManager.loadTexture("Textures/ventana_escenario.jpg");
        Quad quad = new Quad(25, 20);
        Geometry geom = new Geometry("FondoLadrillos", quad);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", tex);
        geom.setMaterial(mat);
        geom.setLocalTranslation(-13, -5, -10);
        rootNode.attachChild(geom);
    }

    private void cargarHUD() {
        Picture hud = new Picture("HUDBase");
        hud.setImage(assetManager, "Textures/hud_pantalla.png", true);
        hud.setWidth(settings.getWidth());
        hud.setHeight(200);
        hud.setPosition(0, 10);
        guiNode.attachChild(hud);
    }

    private void cargarTextoHUD() {
        BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");

        txtEnemigos = new BitmapText(font, false);
        txtEnemigos.setSize(30);
        txtEnemigos.setColor(ColorRGBA.Yellow);
        txtEnemigos.setLocalTranslation(40, 120, 0);
        guiNode.attachChild(txtEnemigos);

        txtOleada = new BitmapText(font, false);
        txtOleada.setSize(30);
        txtOleada.setColor(ColorRGBA.Yellow);
        txtOleada.setLocalTranslation(420, 120, 0);
        guiNode.attachChild(txtOleada);

        txtVida = new BitmapText(font, false);
        txtVida.setSize(30);
        txtVida.setColor(ColorRGBA.Yellow);
        txtVida.setLocalTranslation(810, 120, 0);
        guiNode.attachChild(txtVida);

        txtMonedas = new BitmapText(font, false);
        txtMonedas.setSize(30);
        txtMonedas.setColor(ColorRGBA.Yellow);
        txtMonedas.setLocalTranslation(1300, 120, 0);
        guiNode.attachChild(txtMonedas);

        txtContadorFlecha = new BitmapText(font, false);
        txtContadorFlecha.setSize(24);
        txtContadorFlecha.setColor(ColorRGBA.White);
        txtContadorFlecha.setLocalTranslation(40, 80, 0);
        guiNode.attachChild(txtContadorFlecha);

        txtContadorMagia = new BitmapText(font, false);
        txtContadorMagia.setSize(24);
        txtContadorMagia.setColor(ColorRGBA.White);
        txtContadorMagia.setLocalTranslation(40, 50, 0);
        guiNode.attachChild(txtContadorMagia);

        txtMensaje = new BitmapText(font, false);
        txtMensaje.setSize(30);
        txtMensaje.setColor(new ColorRGBA(1f, 0f, 0f, 1f));
        txtMensaje.setLocalTranslation(600, 150, 0);
        guiNode.attachChild(txtMensaje);

        actualizarHUD();
    }

    private void actualizarHUD() {
        txtEnemigos.setText("Enemigos Destruidos: " + enemigosDestruidos);
        txtOleada.setText("Oleada Actual: " + oleadaActual);
        txtVida.setText("Vida Restante: " + vidaJugador);
        txtMonedas.setText("Monedas: " + monedas);
        txtContadorFlecha.setText("Torre de arquería: " + totalTorresFlecha + "/2");
        txtContadorMagia.setText("Torre arcana: " + totalTorresMagia + "/2");
        txtMensaje.setText("");
    }

    private void cargarMusicaFondo() {
        AudioNode musica = new AudioNode(assetManager, "Sounds/musica_fondo.ogg", false);
        musica.setPositional(false);
        musica.setLooping(true);
        musica.setVolume(0.3f);
        musica.play();
    }

    public void aumentarEnemigosDestruidos() {
        enemigosDestruidos++;
        actualizarHUD();
    }

    public void reducirVidaJugador(int cantidad) {
        vidaJugador -= cantidad;
        if (vidaJugador < 0) vidaJugador = 0;
        actualizarHUD();
        if (vidaJugador <= 0) {
            mostrarMensajeDerrota();
            paused = true;
        }
    }

    private void mostrarMensajeDerrota() {
        BitmapText derrota = new BitmapText(guiFont, false);
        derrota.setSize(50);
        derrota.setColor(ColorRGBA.Red);
        derrota.setText("¡DERROTA!");
        derrota.setLocalTranslation(600, 400, 0);
        guiNode.attachChild(derrota);
    }

    public void aumentarOleada() {
        oleadaActual++;
        actualizarHUD();
        oleada.iniciar();
    }

    private void configurarControlesCompra() {
        inputManager.addMapping("ComprarFlecha", new KeyTrigger(KeyInput.KEY_F));
        inputManager.addMapping("ComprarMagia", new KeyTrigger(KeyInput.KEY_M));
        inputManager.addMapping("Pausa", new KeyTrigger(KeyInput.KEY_RETURN));

        inputManager.addListener(actionListener, "ComprarFlecha", "ComprarMagia", "Pausa");
    }

    private final com.jme3.input.controls.ActionListener actionListener = new com.jme3.input.controls.ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (!isPressed) return;

            switch (name) {
                case "ComprarFlecha" -> comprarTorre("flecha");
                case "ComprarMagia" -> comprarTorre("magia");
                case "Pausa" -> togglePausa();
            }
        }
    };

    private void togglePausa() {
        paused = !paused;

        if (paused) {
            mostrarMenuPausa();
        } else {
            ocultarMenuPausa();
        }
    }

    private void mostrarMenuPausa() {
        menuPausa = new Node("MenuPausa");

        BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");

        BitmapText texto = new BitmapText(font, false);
        texto.setSize(40);
        texto.setColor(ColorRGBA.Blue);
        texto.setText("Juego en Pausa");
        texto.setLocalTranslation(500, 400, 0);
        menuPausa.attachChild(texto);

        BitmapText btnReanudar = new BitmapText(font, false);
        btnReanudar.setSize(30);
        btnReanudar.setColor(ColorRGBA.Green);
        btnReanudar.setText("Presiona R para Reanudar");
        btnReanudar.setLocalTranslation(500, 350, 0);
        menuPausa.attachChild(btnReanudar);

        BitmapText btnSalir = new BitmapText(font, false);
        btnSalir.setSize(30);
        btnSalir.setColor(ColorRGBA.Red);
        btnSalir.setText("Presiona Q para Salir");
        btnSalir.setLocalTranslation(500, 310, 0);
        menuPausa.attachChild(btnSalir);

        guiNode.attachChild(menuPausa);

        inputManager.addMapping("Reanudar", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addMapping("Salir", new KeyTrigger(KeyInput.KEY_Q));

        inputManager.addListener(pausaListener, "Reanudar", "Salir");
    }

    private void ocultarMenuPausa() {
        if (menuPausa != null) {
            guiNode.detachChild(menuPausa);
            menuPausa = null;
        }
        inputManager.deleteMapping("Reanudar");
        inputManager.deleteMapping("Salir");
        inputManager.removeListener(pausaListener);
    }

    private final com.jme3.input.controls.ActionListener pausaListener = new com.jme3.input.controls.ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (!isPressed) return;

            switch (name) {
                case "Reanudar" -> togglePausa();
                case "Salir" -> stop();
            }
        }
    };

    private void comprarTorre(String tipo) {
        if (torres.size() >= MAX_TORRES) {
            txtMensaje.setText("Límite de torres alcanzado");
            return;
        }

        Torre nueva = null;

        Vector3f[] posicionesFlecha = {
            new Vector3f(0f, 0, 0),
            new Vector3f(4f, 1, 5)
        };
        Vector3f[] posicionesMagia = {
            new Vector3f(-3f, 3, -1),
            new Vector3f(2f, 3, -1)
        };

        if (tipo.equals("flecha")) {
            if (totalTorresFlecha >= MAX_FLECHAS) {
                txtMensaje.setText("Máximo de torres de arquería");
                return;
            }
            if (monedas < COSTO_FLECHA) {
                txtMensaje.setText("No tienes suficientes monedas");
                return;
            }
            nueva = new TorreFlecha(assetManager, posicionesFlecha[totalTorresFlecha]);
            totalTorresFlecha++;
            monedas -= COSTO_FLECHA;

        } else if (tipo.equals("magia")) {
            if (totalTorresMagia >= MAX_MAGIAS) {
                txtMensaje.setText("Máximo de torres arcanas");
                return;
            }
            if (monedas < COSTO_MAGIA) {
                txtMensaje.setText("No tienes suficientes monedas");
                return;
            }
            nueva = new TorreMagia(assetManager, posicionesMagia[totalTorresMagia]);
            totalTorresMagia++;
            monedas -= COSTO_MAGIA;
        }

        if (nueva != null) {
            torres.add(nueva);
            rootNode.attachChild(nueva);
            actualizarHUD();
        }
    }
}
