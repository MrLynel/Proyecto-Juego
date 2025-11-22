package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;

public class PantallaGameOver implements Screen {

    private SpaceNavigation game;
    private OrthographicCamera camera;

    public PantallaGameOver(SpaceNavigation game) {
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
        game.getFont().draw(game.getBatch(), "Game Over !!! ", 120, 400, 400, 1, true);
        game.getFont().draw(game.getBatch(), "Pincha en cualquier lado para reiniciar ...", 100, 300);
        
        // INSTRUCCIÓN PARA MODO HARDCORE
        game.getFont().draw(game.getBatch(), "Presiona F1 en el menú para modo Hardcore", 100, 200);
        
        // MOSTRAR HIGH SCORE
        game.getFont().draw(game.getBatch(), "High Score: " + game.getHighScore(), 100, 150);
    
        game.getBatch().end();

        if (Gdx.input.isTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
            // VOLVER AL MENÚ PRINCIPAL EN LUGAR DE DIRECTAMENTE AL JUEGO
            Screen ss = new PantallaMenu(game);
            ss.resize(1200, 800);
            game.setScreen(ss);
            dispose();
        }
    }
 
    @Override
    public void show() {
        
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
