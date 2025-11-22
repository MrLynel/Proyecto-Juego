package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;

public class PantallaMenu implements Screen {

    private SpaceNavigation game;
    private OrthographicCamera camera;
    private boolean modoHardcore = false;
    private boolean easterEggActivado = false;
    private static final String URL_EASTER_EGG = "https://youtube.com/shorts/PyDMYuRqyu4"; //PROFE NO ME VAYA A SACAR PUNTOS POR ESTE CHISTESITO :)

    public PantallaMenu(SpaceNavigation game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1200, 800);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        camera.update();
        game.getBatch().setProjectionMatrix(camera.combined);

        game.getBatch().begin();
        game.getFont().draw(game.getBatch(), "Bienvenido a Space Navigation !", 140, 400);
        game.getFont().draw(game.getBatch(), "Pincha en cualquier lado o presiona cualquier tecla para comenzar ...", 100, 300);
        
        // INSTRUCCIONES DEL MODO HARDCORE 
        game.getFont().draw(game.getBatch(), "PRESIONA F1 PARA MODO HARDCORE", 100, 200);
        game.getFont().draw(game.getBatch(), "- Ronda inicial: 5", 100, 170);
        game.getFont().draw(game.getBatch(), "- Meteoritos más rápidos y agresivos", 100, 140);
        game.getFont().draw(game.getBatch(), "- Poco spawn de poderes", 100, 110);
        
        // MENSAJE EASTER EGG
        if (easterEggActivado) {
            game.getFont().draw(game.getBatch(), "¡SORPRESA SECRETA ACTIVADA! 🎉", 100, 80);
            game.getFont().draw(game.getBatch(), "Disfruta del regalo...", 100, 50);
        }
        
        // INDICADOR MODO HARDCORE
        if (modoHardcore) {
            game.getFont().draw(game.getBatch(), "MODO HARDCORE ACTIVADO!", 100, 250);
        }
        
        game.getBatch().end();

        // VERIFICAR TECLA F1 PARA MODO HARDCORE
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            modoHardcore = !modoHardcore;
            Gdx.app.log("PantallaMenu", "Modo Hardcore: " + modoHardcore);
        }

        // EASTER EGG SECRETO: TECLA F2 
        if (Gdx.input.isKeyJustPressed(Input.Keys.F2)) {
            activarEasterEgg();
        }

        // INICIAR JUEGO CON CUALQUIER TECLA O CLIC A EXCEPCION DE QUE ESTE ACTIVO EL EASTER EGG
        if ((Gdx.input.isTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) && !easterEggActivado) {
            // NO VERIFICAR F1 Y F2 O HABRA CONFLICTO
            if (!Gdx.input.isKeyJustPressed(Input.Keys.F1) && !Gdx.input.isKeyJustPressed(Input.Keys.F2)) {
                Screen ss;
                if (modoHardcore) {
                    ss = new PantallaJuego(game, 5, 3, 0, 3, 3, 15, true);
                } else {
                    ss = new PantallaJuego(game, 1, 3, 0, 1, 1, 10, false);
                }
                ss.resize(1200, 800);
                game.setScreen(ss);
                dispose();
            }
        }
    }

    private void activarEasterEgg() {
        easterEggActivado = true;
        Gdx.app.log("PantallaMenu", "EASTER EGG ACTIVADO - Sorpresa secreta!");
        
        try {
            // ENLACE AL VIDEO DEL EASTER EGG
            Gdx.net.openURI(URL_EASTER_EGG);
            Gdx.app.log("PantallaMenu", "Sorpensa secreta activada!");
        } catch (Exception e) {
            Gdx.app.error("PantallaMenu", "Error al abrir la sorpresa: " + e.getMessage());
        }
        
        // DESACTIVAR EASTER EGG
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                    easterEggActivado = false;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }
    
    @Override
    public void show() {
        easterEggActivado = false;
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
    }
}
