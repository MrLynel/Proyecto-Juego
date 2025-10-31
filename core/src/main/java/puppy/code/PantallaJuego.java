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

    
    private float tiempoParaNuevoAsteroide = 1f;
    private float intervaloGeneracion = 0.8f;
    private Random random = new Random();
    private int puntajeParaSiguienteRonda;

    public PantallaJuego(SpaceNavigation game, int ronda, int vidas, int score,
            int velXAsteroides, int velYAsteroides, int cantAsteroides) {
        this.game = game;
        this.ronda = ronda;
        this.score = score;
        this.velXAsteroides = velXAsteroides;
        this.velYAsteroides = velYAsteroides;
        this.cantAsteroides = cantAsteroides;

        // AVANZAR USANDO PUNTAJE 
        this.puntajeParaSiguienteRonda = score + (ronda * 50);

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
    }

    private void generarNuevoAsteroide() {
        // Generacion DE METEORITOS
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

    public void dibujaEncabezado() {
        CharSequence str = "Vidas: " + nave.getVidas() + " Ronda: " + ronda;
        game.getFont().getData().setScale(2f);
        game.getFont().draw(batch, str, 10, 30);
        game.getFont().draw(batch, "Score:" + this.score, Gdx.graphics.getWidth() - 150, 30);
        game.getFont().draw(batch, "HighScore:" + game.getHighScore(), Gdx.graphics.getWidth() / 2 - 100, 30);
        
        // MOSTRAR PUNTAJE PARA AVANZAR DE ETAPA
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

        // GENERAR RAPIDOS
        tiempoParaNuevoAsteroide -= delta;
        if (tiempoParaNuevoAsteroide <= 0) {
            generarNuevoAsteroide();
            
            float nuevoIntervalo = Math.max(0.3f, 0.8f - (ronda * 0.05f));
            tiempoParaNuevoAsteroide = nuevoIntervalo;
        }

        if (!nave.estaHerido()) {
            // ACTUALIZAR BALAS
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

            // ACTUALIZAR ASTEROIDES
            for (int i = 0; i < balls1.size(); i++) {
                Ball2 ball = balls1.get(i);
                ball.update();

                if (!ball.isActive()) {
                    balls1.remove(i);
                    balls2.remove(i);
                    i--;
                }
            }

            // COLISIONES ENTRE ASTEROIDES
            for (int i = 0; i < balls1.size(); i++) {
                Ball2 ball1 = balls1.get(i);
                for (int j = i + 1; j < balls2.size(); j++) {
                    Ball2 ball2 = balls2.get(j);
                    ball1.checkCollision(ball2);
                }
            }
        }

        // DIBUJAR BALAS
        for (Bullet b : balas) {
            b.draw(batch);
        }

        nave.draw(batch, this);

        // DIBUJAR ASTEROIDES Y COLISIONES CON NAVE
        for (int i = 0; i < balls1.size(); i++) {
            Ball2 b = balls1.get(i);
            b.draw(batch);

            if (nave.checkCollision(b)) {
                balls1.remove(i);
                balls2.remove(i);
                i--;
            }
        }

        // FIN DEL JUEGO
        if (nave.estaDestruido()) {
            if (score > game.getHighScore())
                game.setHighScore(score);
            Screen ss = new PantallaGameOver(game);
            ss.resize(1200, 800);
            game.setScreen(ss);
            dispose();
        }
        batch.end();

     
        if (score >= puntajeParaSiguienteRonda) {
            int nuevaRonda = ronda + 1;
            
        
            int nuevasVidas = nave.getVidas();
            
         
            int nuevaVelY = velYAsteroides + 1;
            int nuevaCantidad = cantAsteroides + 2;
            
            Screen ss = new PantallaJuego(game, nuevaRonda, nuevasVidas, score,
                    velXAsteroides, nuevaVelY, nuevaCantidad);
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
        this.explosionSound.dispose();
        this.gameMusic.dispose();
    }
}
