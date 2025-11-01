package puppy.code;

import java.util.ArrayList;
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

    public PantallaJuego(SpaceNavigation game, int ronda, int vidas, int score,
            int velXAsteroides, int velYAsteroides, int cantAsteroides) {
        this.game = game;
        this.ronda = ronda;
        this.score = score;
        this.velXAsteroides = velXAsteroides;
        this.velYAsteroides = velYAsteroides;
        this.cantAsteroides = cantAsteroides;

        // PUNTAJE POR ETAPA: 50 × 2^(ronda-1)
        this.puntajeParaSiguienteRonda = score + (50 * (int)Math.pow(2, ronda - 1));

        batch = game.getBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 640);

        explosionSound = Gdx.audio.newSound(Gdx.files.internal("explosion.ogg"));
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("piano-loops.wav"));
        gameMusic.setLooping(true);
        gameMusic.play();

        nave = new Nave4(Gdx.graphics.getWidth() / 2 - 50, 30,
                new Texture(Gdx.files.internal("MainShip3.png")),
                Gdx.audio.newSound(Gdx.files.internal("hurt.ogg")),
                new Texture(Gdx.files.internal("Rocket2.png")),
                Gdx.audio.newSound(Gdx.files.internal("pop-sound.mp3")));
        nave.setVidas(vidas);

        // SOLO UNA VIDA EXTRA AL INICIAR PRIMERA RONDA
        if (ronda == 1) {
            generarVidaExtra();
        }
    }

    private void generarNuevoAsteroide() {
        int cantidad = 1 + random.nextInt(2);
        
        for (int i = 0; i < cantidad; i++) {
            int size = 20 + random.nextInt(35);
            int x = random.nextInt((int) Gdx.graphics.getWidth() - size);
            int y = Gdx.graphics.getHeight();

            int xSpeed = (random.nextInt(7) - 3);
            int ySpeed = -(2 + random.nextInt(3) + (ronda / 2));

            Texture textura = new Texture(Gdx.files.internal("aGreyMedium4.png"));

            Ball2 asteroide = new Ball2(x, y, size, xSpeed, ySpeed, textura);
            balls1.add(asteroide);
            balls2.add(asteroide);
        }
    }

    //(SOLO UNA POR RONDA)
    private void generarVidaExtra() {
        if (vidaExtraGenerada) return;
        
        int x = random.nextInt((int) Gdx.graphics.getWidth() - 40);
        int y = Gdx.graphics.getHeight();
        
        Texture texturaVida;
        try {
            texturaVida = new Texture(Gdx.files.internal("oneup.png"));
        } catch (Exception e) {
            texturaVida = new Texture(Gdx.files.internal("Rocket2.png"));
        }
        
        VidaExtra vida = new VidaExtra(x, y, texturaVida);
        poderesActivos.add(vida);
        vidaExtraGenerada = true;
    }

    // GENERAR ESCUDOS
    private void generarEscudoAleatorio() {
        if (random.nextFloat() < 0.002f) { // 0.2% de probabilidad - MUY RARO
            int x = random.nextInt((int) Gdx.graphics.getWidth() - 40);
            int y = Gdx.graphics.getHeight();
            
            Texture texturaEscudo;
            try {
                texturaEscudo = new Texture(Gdx.files.internal("escudo.png"));
            } catch (Exception e) {
                texturaEscudo = new Texture(Gdx.files.internal("Rocket2.png"));
            }
            
            PoderEscudo escudo = new PoderEscudo(x, y, texturaEscudo);
            poderesActivos.add(escudo);
        }
    }

    public void dibujaEncabezado() {
        CharSequence str = "Vidas: " + nave.getVidas() + " Ronda: " + ronda;
        game.getFont().getData().setScale(2f);
        game.getFont().draw(batch, str, 10, 30);
        game.getFont().draw(batch, "Score:" + this.score, Gdx.graphics.getWidth() - 150, 30);
        game.getFont().draw(batch, "HighScore:" + game.getHighScore(), Gdx.graphics.getWidth() / 2 - 100, 30);
        
        // MOSTRAR TIEMPO DE ESCUDO
        if (nave.tieneEscudoActivo()) {
            String escudoStr = String.format("Escudo: %.1fs", nave.getTiempoEscudoRestante());
            game.getFont().draw(batch, escudoStr, Gdx.graphics.getWidth() / 2 - 100, 90);
        }
        
        // MOSTRAR PUNTOS PARA SIGUIENTE RONDA
        int puntosRestantes = puntajeParaSiguienteRonda - score;
        if (puntosRestantes > 0) {
            game.getFont().draw(batch, "Siguiente ronda: " + puntosRestantes + " pts", 
                              Gdx.graphics.getWidth() / 2 - 100, 60);
        } else {
            game.getFont().draw(batch, "¡Pasa a ronda " + (ronda + 1) + "!", 
                              Gdx.graphics.getWidth() / 2 - 100, 60);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        dibujaEncabezado();

        // GENERACIÓN DE METEORITOS
        tiempoParaNuevoAsteroide -= delta;
        if (tiempoParaNuevoAsteroide <= 0) {
            generarNuevoAsteroide();
            
            float nuevoIntervalo = Math.max(0.3f, 0.8f - (ronda * 0.05f));
            tiempoParaNuevoAsteroide = nuevoIntervalo;
        }

        // GENERAR ESCUDOS ALEATORIOS
        generarEscudoAleatorio();

        if (!nave.estaHerido()) {
            
            for (int i = 0; i < balas.size(); i++) {
                Bullet b = balas.get(i);
                b.update();

                for (int j = 0; j < balls1.size(); j++) {
                    if (b.checkCollision(balls1.get(j))) {
                        explosionSound.play();
                        int puntosGanados = 10 + (ronda * 3);
                        score += puntosGanados;
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

            // ACTUALIZAR METEORITOS
            for (int i = 0; i < balls1.size(); i++) {
                Ball2 ball = balls1.get(i);
                ball.update();

                if (!ball.isActive()) {
                    balls1.remove(i);
                    balls2.remove(i);
                    i--;
                }
            }

            // COLISIONES ENTRE METEORITOS
            for (int i = 0; i < balls1.size(); i++) {
                Ball2 ball1 = balls1.get(i);
                for (int j = i + 1; j < balls2.size(); j++) {
                    Ball2 ball2 = balls2.get(j);
                    ball1.checkCollision(ball2);
                }
            }
        }

        // ACTUALIZAR Y DIBUJAR PODERES
        for (int i = 0; i < poderesActivos.size(); i++) {
            GameObject poder = poderesActivos.get(i);
            poder.update();
            poder.draw(batch);
            
            
            if (poder.getArea().overlaps(nave.getArea())) {
                poder.aplicarEfecto(nave);
                
                if (poder instanceof Colisionable) {
                    ((Colisionable) poder).onColision();
                }
                
                
                if (poder instanceof Colisionable && ((Colisionable) poder).debeEliminarse()) {
                    poderesActivos.remove(i);
                    i--;
                }
                continue;
            }
            
            // ELIMINAR PODERES INACTIVOS
            if (!poder.isActive()) {
                poderesActivos.remove(i);
                i--;
            }
        }

        // DIBUJAR BALAS
        for (Bullet b : balas) {
            b.draw(batch);
        }

        nave.draw(batch, this);

        // COLISIONES NAVE-METEORITOS
        for (int i = 0; i < balls1.size(); i++) {
            Ball2 b = balls1.get(i);
            b.draw(batch);

            if (nave.checkCollision(b)) {
                balls1.remove(i);
                balls2.remove(i);
                i--;
            }
        }

        // GAME OVER
        if (nave.estaDestruido()) {
            if (score > game.getHighScore())
                game.setHighScore(score);
            Screen ss = new PantallaGameOver(game);
            ss.resize(1200, 800);
            game.setScreen(ss);
            dispose();
        }
        batch.end();

        // AVANZAR DE RONDA
        if (score >= puntajeParaSiguienteRonda) {
            int nuevaRonda = ronda + 1;
            int nuevasVidas = nave.getVidas();
            
            Screen ss = new PantallaJuego(game, nuevaRonda, nuevasVidas, score,
                    velXAsteroides, velYAsteroides + 1, cantAsteroides + 2);
            ss.resize(1200, 800);
            game.setScreen(ss);
            dispose();
        }
    }

    public boolean agregarBala(Bullet bb) {
        return balas.add(bb);
    }

    @Override
    public void show() {
        gameMusic.play();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        explosionSound.dispose();
        gameMusic.dispose();
    }
}
