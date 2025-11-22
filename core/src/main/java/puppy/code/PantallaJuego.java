package puppy.code;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PantallaJuego implements Screen {

    private SpaceNavigation game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Sound explosionSound;
    private Music gameMusic;
    private int score;
    private int ronda;
    private int velXAsteroides;
    private int velYAsteroides;
    private int cantAsteroides;

    private Nave4 nave;
    
    private ArrayList<Ball2> balls1 = new ArrayList<>();
    private ArrayList<Ball2> balls2 = new ArrayList<>();
    private ArrayList<Bullet> balas = new ArrayList<>();

    private ArrayList<GameObject> poderesActivos = new ArrayList<>();

    private float tiempoParaNuevoAsteroide = 1f;
    private Random random = new Random();
    private int puntajeParaSiguienteRonda;
    private boolean vidaExtraGenerada = false;

    // GM2.4 - ABSTRACT FACTORY
    private GameObjectFactory objectFactory;

    // NUEVAS VARIABLES PARA MODO HARDCORE
    private boolean modoHardcore;
    private float tiempoUltimoDisparo = 0;
    private static final float TIEMPO_ENTRE_DISPAROS = 0.2f; 
    private Texture txBala;
    private Texture backgroundTexture;

    // CONSTRUCTOR ORIGINAL (PantallaMenu)
    public PantallaJuego(SpaceNavigation game, int ronda, int vidas, int score,
            int velXAsteroides, int velYAsteroides, int cantAsteroides, boolean modoHardcore) {
        this(game, ronda, vidas, score, velXAsteroides, velYAsteroides, cantAsteroides, modoHardcore, null);
    }

    // NUEVO CONSTRUCTOR CON MÚSICA
    public PantallaJuego(SpaceNavigation game, int ronda, int vidas, int score,
            int velXAsteroides, int velYAsteroides, int cantAsteroides, 
            boolean modoHardcore, Music music) {
        this.game = game;
        this.ronda = ronda;
        this.score = score;
        this.velXAsteroides = velXAsteroides;
        this.velYAsteroides = velYAsteroides;
        this.cantAsteroides = cantAsteroides;
        this.modoHardcore = modoHardcore;
        this.gameMusic = music;  // USAR MÚSICA EXISTENTE O NULL

        // PUNTAJE POR ETAPA: 50 × 2^(RONDA-1)
        this.puntajeParaSiguienteRonda = score + (50 * (int)Math.pow(2, ronda - 1));

        batch = game.getBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 640);

        ResourceManager resources = ResourceManager.getInstance();
        explosionSound = resources.getSound("explosion.ogg");
        
        // INICIALIZAR MÚSICA SOLO SI ES NUEVA
        if (gameMusic == null) {
            if (modoHardcore) {
                if (resources.hasMusic("hardcoremusic.mp3")) {
                    gameMusic = resources.getMusic("hardcoremusic.mp3");
                    Gdx.app.log("PantallaJuego", "NUEVA Música Hardcore cargada");
                } else if (resources.hasMusic("hardcoremusic.ogg")) {
                    gameMusic = resources.getMusic("hardcoremusic.ogg");
                    Gdx.app.log("PantallaJuego", "NUEVA Música Hardcore cargada");
                } else if (resources.hasMusic("hardcoremusic.wav")) {
                    gameMusic = resources.getMusic("hardcoremusic.wav");
                    Gdx.app.log("PantallaJuego", "NUEVA Música Hardcore cargada");
                } else {
                    gameMusic = resources.getMusic("piano-loops.wav");
                    Gdx.app.log("PantallaJuego", "Música Hardcore no encontrada, usando normal");
                }
            } else {
                gameMusic = resources.getMusic("piano-loops.wav");
                Gdx.app.log("PantallaJuego", "Música Normal cargada");
            }
        } else {
            Gdx.app.log("PantallaJuego", "Música existente reutilizada");
        }
        
        gameMusic.setLooping(true);
        gameMusic.play();

        // GUARDAR TEXTURA DE LA BALA
        txBala = resources.getTexture("Rocket2.png");

        // FONDO SEGÚN MODO
        if (modoHardcore && resources.hasTexture("spacehardcore.png")) {
            backgroundTexture = resources.getTexture("spacehardcore.png");
            Gdx.app.log("PantallaJuego", "Fondo Hardcore cargado");
        } else {
            backgroundTexture = null;
        }

        nave = new Nave4(Gdx.graphics.getWidth() / 2 - 50, 30,
                resources.getTexture("MainShip3.png"),
                resources.getSound("hurt.ogg"),
                txBala,
                resources.getSound("pop-sound.mp3"));
        nave.setVidas(vidas);

        this.objectFactory = GameObjectFactoryCreator.createFactory(ronda);
        
        if (modoHardcore) {
            Gdx.app.log("PantallaJuego", "MODO HARDCORE ACTIVADO - Ronda " + ronda);
        } else {
            Gdx.app.log("PantallaJuego", "MODO NORMAL - Ronda " + ronda);
        }

        if (!modoHardcore) {
            generarVidaExtra();
        }
    }

    private void generarNuevoAsteroide() {
        int cantidad = 1 + random.nextInt(2);
        
        for (int i = 0; i < cantidad; i++) {
            int x = random.nextInt((int) Gdx.graphics.getWidth() - 40);
            int y = Gdx.graphics.getHeight();
            Ball2 asteroide = objectFactory.createAsteroid(x, y);
            
            balls1.add(asteroide);
            balls2.add(asteroide);
        }
    }

    private void generarVidaExtra() {
        if (vidaExtraGenerada) return;
        
        int x = random.nextInt((int) Gdx.graphics.getWidth() - 40);
        int y = Gdx.graphics.getHeight();
        
        ResourceManager resources = ResourceManager.getInstance();
        Texture texturaVida = resources.hasTexture("oneup.png") ? 
                resources.getTexture("oneup.png") : resources.getTexture("Rocket2.png");
        
        VidaExtra vida = new VidaExtra(x, y, texturaVida);
        poderesActivos.add(vida);
        vidaExtraGenerada = true;
        
        Gdx.app.log("PantallaJuego", "Vida extra generada en ronda " + ronda);
    }

    private void generarPowerUpAleatorio() {
        if (random.nextFloat() < 0.003f) {
            int x = random.nextInt((int) Gdx.graphics.getWidth() - 40);
            int y = Gdx.graphics.getHeight();
            
            GameObject powerUp = objectFactory.createPowerUp(x, y);
            poderesActivos.add(powerUp);
            
            Gdx.app.log("PantallaJuego", "Power-up generado: " + powerUp.getTipo());
        }
    }

    private void disparar() {
        if (nave.tieneDisparoTriple()) {
            Bullet balaCentro = new Bullet(nave.getX() + nave.getArea().width / 2 - 5, 
                                         nave.getY() + nave.getArea().height - 5, 0, 3, txBala);
            Bullet balaIzquierda = new Bullet(nave.getX() + nave.getArea().width / 4 - 5, 
                                            nave.getY() + nave.getArea().height - 5, -1, 3, txBala);
            Bullet balaDerecha = new Bullet(nave.getX() + 3 * nave.getArea().width / 4 - 5, 
                                          nave.getY() + nave.getArea().height - 5, 1, 3, txBala);
            
            agregarBala(balaCentro);
            agregarBala(balaIzquierda);
            agregarBala(balaDerecha);
            nave.getSoundBala().play();
        } else {
            Bullet bala = new Bullet(nave.getX() + nave.getArea().width / 2 - 5, 
                                   nave.getY() + nave.getArea().height - 5, 0, 3, txBala);
            agregarBala(bala);
            nave.getSoundBala().play();
        }
    }

    public void dibujaEncabezado() {
        CharSequence str = "Vidas: " + nave.getVidas() + " Ronda: " + ronda;
        game.getFont().getData().setScale(2f);
        game.getFont().draw(batch, str, 10, 30);
        game.getFont().draw(batch, "Score:" + this.score, Gdx.graphics.getWidth() - 150, 30);
        game.getFont().draw(batch, "HighScore:" + game.getHighScore(), Gdx.graphics.getWidth() / 2 - 100, 30);
        
        if (modoHardcore) {
            game.getFont().draw(batch, "MODO HARDCORE", Gdx.graphics.getWidth() / 2 - 100, 150);
        }
        
        if (nave.tieneEscudoActivo()) {
            String escudoStr = String.format("Escudo: %.1fs", nave.getTiempoEscudoRestante());
            game.getFont().draw(batch, escudoStr, Gdx.graphics.getWidth() / 2 - 100, 60);
        }
        
        if (nave.tieneDisparoTriple()) {
            String tripleStr = String.format("Triple: %.1fs", nave.getTiempoDisparoTripleRestante());
            game.getFont().draw(batch, tripleStr, Gdx.graphics.getWidth() / 2 - 100, 90);
        }
        
        int puntosRestantes = puntajeParaSiguienteRonda - score;
        if (puntosRestantes > 0) {
            game.getFont().draw(batch, "Siguiente ronda: " + puntosRestantes + " pts", 
                              Gdx.graphics.getWidth() / 2 - 100, 120);
        } else {
            game.getFont().draw(batch, "¡Pasa a ronda " + (ronda + 1) + "!", 
                              Gdx.graphics.getWidth() / 2 - 100, 120);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        batch.begin();
        
        if (backgroundTexture != null) {
            batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        
        dibujaEncabezado();

        tiempoUltimoDisparo += delta;

        tiempoParaNuevoAsteroide -= delta;
        if (tiempoParaNuevoAsteroide <= 0) {
            generarNuevoAsteroide();
            
            float nuevoIntervalo = modoHardcore ? 
                Math.max(0.2f, 0.6f - (ronda * 0.05f)) : 
                Math.max(0.3f, 0.8f - (ronda * 0.05f));
            tiempoParaNuevoAsteroide = nuevoIntervalo;
        }

        if (!modoHardcore || random.nextFloat() < 0.001f) {
            generarPowerUpAleatorio();
        }

        if (!nave.estaHerido()) {
            for (int i = 0; i < balas.size(); i++) {
                Bullet b = balas.get(i);
                b.update();

                for (int j = 0; j < balls1.size(); j++) {
                    Ball2 asteroide = balls1.get(j);
                    if (b.checkCollision(asteroide)) {
                        explosionSound.play();
                        int puntosGanados = 10 + (ronda * 3);
                        score += puntosGanados;
                        
                        if (asteroide.esAsteroideGrande() && random.nextFloat() < 0.8f) {
                            List<Ball2> miniAsteroides = asteroide.explotar();
                            for (int k = 0; k < miniAsteroides.size(); k++) {
                                Ball2 mini = miniAsteroides.get(k);
                                mini.setX(mini.getX() + random.nextInt(10) - 5);
                                mini.setY(mini.getY() + random.nextInt(5));
                                balls1.add(mini);
                                balls2.add(mini);
                            }
                        }
                        
                        balls1.remove(j);
                        balls2.remove(j);
                        j--;
                    }
                }

                if (b.isDestroyed()) {
                    balas.remove(b);
                    i--;
                }
            }

            for (int i = 0; i < balls1.size(); i++) {
                Ball2 ball = balls1.get(i);
                ball.update();

                if (!ball.isActive()) {
                    balls1.remove(i);
                    balls2.remove(i);
                    i--;
                }
            }

            for (int i = 0; i < balls1.size(); i++) {
                Ball2 ball1 = balls1.get(i);
                for (int j = i + 1; j < balls2.size(); j++) {
                    Ball2 ball2 = balls2.get(j);
                    ball1.checkCollision(ball2);
                }
            }
        }

        for (int i = 0; i < poderesActivos.size(); i++) {
            GameObject poder = poderesActivos.get(i);
            poder.update();
            poder.draw(batch);
            
            if (poder.isActive() && poder.getArea().overlaps(nave.getArea())) {
                poder.aplicarEfecto(nave);
                
                if (poder instanceof Colisionable) {
                    ((Colisionable) poder).onColision();
                }
                
                poder.active = false;
                continue;
            }
            
            if (!poder.isActive()) {
                poderesActivos.remove(i);
                i--;
            }
        }

        for (Bullet b : balas) {
            b.draw(batch);
        }

        nave.draw(batch, this);

        for (int i = 0; i < balls1.size(); i++) {
            Ball2 b = balls1.get(i);
            b.draw(batch);

            if (nave.checkCollision(b)) {
                balls1.remove(i);
                balls2.remove(i);
                i--;
            }
        }

        // DISPARO CORREGIDO - SOLO CON PRESIÓN, NO MANTENIDO
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)) {
            if (modoHardcore) {
                if (tiempoUltimoDisparo >= TIEMPO_ENTRE_DISPAROS) {
                    tiempoUltimoDisparo = 0;
                    disparar();
                }
            } else {
                disparar();
            }
        }

        batch.end();

        if (nave.estaDestruido()) {
            if (score > game.getHighScore())
                game.setHighScore(score);
            
            // IMPORTANTE: DETENER MUSICA ANTES DE CAMBIAR PANTALLA
            gameMusic.stop();
            Screen ss = new PantallaGameOver(game);
            ss.resize(1200, 800);
            game.setScreen(ss);
            dispose();
        }

        if (score >= puntajeParaSiguienteRonda) {
            int nuevaRonda = ronda + 1;
            int nuevasVidas = nave.getVidas();
            
            Gdx.app.log("PantallaJuego", "Avanzando a ronda " + nuevaRonda);
            
            // PASAR LA MÚSICA ACTUAL A LA NUEVA PANTALLA
            Screen ss = new PantallaJuego(game, nuevaRonda, nuevasVidas, score,
                    velXAsteroides, velYAsteroides + 1, cantAsteroides + 2, 
                    modoHardcore, gameMusic);  // PASA LA MÚSICA
            
            ss.resize(1200, 800);
            game.setScreen(ss);
            
            // NO HACER DISPOSE A LA MUSICA
            disposeWithoutMusic();
        }
    }

    // NUEVO MÉTODO: DISPOSE SIN DETENER MUSICA
    private void disposeWithoutMusic() {
        // LIBERAR RECURSOS EXCEPTO MUSICA
        explosionSound = null;
        batch = null;
        nave = null;
        balls1.clear();
        balls2.clear();
        balas.clear();
        poderesActivos.clear();
    }

    public boolean agregarBala(Bullet bb) {
        return balas.add(bb);
    }

    @Override
    public void show() {
        if (gameMusic != null && !gameMusic.isPlaying()) {
            gameMusic.play();
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
        if (gameMusic != null) {
            gameMusic.pause();
        }
    }

    @Override
    public void resume() {
        if (gameMusic != null && !gameMusic.isPlaying()) {
            gameMusic.play();
        }
    }

    @Override
    public void hide() {
        
    }

    @Override
    public void dispose() {
        // SOLO DETENER MUSICA SI ES EL FIN DEL JUEGO
        if (gameMusic != null) {
            gameMusic.stop();
            // EVITAR DISPOSE DE LA MUSICA
        }
    }
}
